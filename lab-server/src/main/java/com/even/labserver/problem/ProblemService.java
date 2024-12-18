package com.even.labserver.problem;

import com.even.labserver.problem.tag.AlgorithmTag;
import com.even.labserver.problem.tag.AlgorithmTagDto;
import com.even.labserver.problem.tag.AlgorithmTagRepository;
import com.even.labserver.problem.tag.ProblemAlgorithmTag;
import com.even.labserver.utils.ScrapeManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedModel;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProblemService {
    private final ProblemRepository problemRepository;
    private final AlgorithmTagRepository algorithmTagRepository;
    private final ProblemUpdateService problemUpdateService;
    private static final Logger logger = LoggerFactory.getLogger(ProblemService.class);

    private final ScrapeManager scrapeManager;
    private final Integer PAGE_SIZE = 10; // 한 페이지에 보여줄 문제 수
    private final Integer OLD_PROBLEM_DAYS = 5; // 오래된 문제로 간주하는 날짜 수
    private final Set<String> allTags = new HashSet<>();
    private final Set<Integer> toUpdateProblems = new HashSet<>();
    private final Map<String, PagedModel<ProblemDto>> searchCache = new HashMap<>();
    private final Map<Integer, List<SimplifiedBojUser>> problemUsersCache = new HashMap<>();

    private Integer startId = 1000;


    @Scheduled(cron = "0 */30 * * * *")
//    @Scheduled(cron = "*/30 * * * * *")
    public void updateProblems() {
        updateProblemsInternal();
    }

    @Scheduled(cron = "15 0 0 * * *")
    public void updateNewProblems() {
        updateNewProblemsInternal();
    }

    /**
     * 문제 정보를 업데이트합니다.
     * 최근에 검색된 적이 있는 문제, 오래된 문제 순으로 {@code BATCH_SIZE} 만큼 업데이트합니다.
     */
    @Async
    public void updateProblemsInternal() {
        final int BATCH_SIZE = 100;

        // 업데이트할 문제 목록. 최근에 검색된 적이 있는 문제들 추가
        List<Integer> toUpdate = new ArrayList<>(toUpdateProblems);

        if (toUpdate.size() < BATCH_SIZE) { // 100개를 못채웠다면 업데이트가 필요한 오래된 문제 추가
            var pagable = PageRequest.of(0, 100 - toUpdate.size(), Sort.by(Sort.Order.asc("modifiedDate")));
            var problems = problemRepository.findAllByModifiedDateBefore(LocalDateTime.now().minusDays(OLD_PROBLEM_DAYS), pagable);
            toUpdate.addAll(problems.stream().map(Problem::getProblemId).toList());
        } else if (toUpdate.size() > BATCH_SIZE) { // 100개를 넘었다면 100개씩으로 자름
            toUpdate = toUpdate.subList(0, BATCH_SIZE);
        }

        if (toUpdate.isEmpty()) {
            System.out.println("No problems to update.");
            return;
        }

        var scraped = scrapeManager.getProblems(toUpdate);
//        for (var dto : scraped) {
//            addOrUpdateProblem(dto);
//        }
        problemUpdateService.updateProblems(scraped);
        System.out.println("Problems updated: " + toUpdate.size() + " problems[:10] = " + toUpdate.subList(0, Math.min(10, toUpdate.size())));

        toUpdate.forEach(toUpdateProblems::remove);
        searchCache.clear(); // 매 30분마다 캐시 초기화
        problemUsersCache.clear();
    }

    /**
     * 추가된 문제 페이지에 있는 문제를 추가하거나 업데이트합니다.
     */
    @Async
    public void updateNewProblemsInternal() {
        var newProblems = scrapeManager.getNewProblems();
        var toUpdate = new ArrayList<Integer>();
        for (var id : newProblems) {
            if (problemRepository.existsById(id)) {
                continue;
            }
            toUpdate.add(id);
        }

        var scraped = scrapeManager.getProblems(toUpdate);

        addOrUpdateProblems(scraped);

        System.out.println("New problems added or updated: " + toUpdate.size() + " and no update: " + (newProblems.size() - toUpdate.size()));

        searchCache.clear();
        problemUsersCache.clear();
    }

    /**
     * 추천 문제를 가져오거나 검색 모드일 경우 문제 검색 결과를 가져옵니다.
     * @param page 페이지 번호
     * @param kw 검색 키워드
     * @param levelStart 난이도 범위 시작
     * @param levelEnd 난이도 범위 끝
     * @param isAsc 오름차순 정렬 여부
     * @param userId 사용자 ID
     * @param searchMode 검색 모드인지 여부
     * @param solvedByUser 사용자가 푼 문제만 가져올지 여부
     * @param sort 정렬 기준
     * @return 추천 문제 목록 또는 검색 결과 목록 PagedModel
     */
    public PagedModel<ProblemDto> getRecommendedProblems(int page, String kw, int levelStart, int levelEnd, boolean isAsc, String userId, boolean searchMode, boolean solvedByUser, String sort) {
        if (searchMode) return getProblemsSearch(page, kw, isAsc, userId, sort, levelStart, levelEnd);

//        var defaultSort = Sort.Order.asc("problemId");
//        List<Sort.Order> sorts = List.of(isAsc ? Sort.Order.asc("solvedCount") : Sort.Order.desc("solvedCount"), defaultSort);

        var pageable = PageRequest.of(page, PAGE_SIZE);
        Specification<Problem> basicSpec = Specification.where(null);

        Specification<Problem> spec = Specification.where(kw.isEmpty() ? basicSpec : ProblemSpecifications.titleLike(kw))
                .and(ProblemSpecifications.levelRange(levelStart, levelEnd))
                .and(userId.isEmpty() ? basicSpec : (solvedByUser ?
                                ProblemSpecifications.solvedBy(userId) :
                                (userId.equals("$cnu") ? ProblemSpecifications.notSolvedByAllUsers() : ProblemSpecifications.notSolvedBy(userId))))
                .and(ProblemSpecifications.sortedBy("solvedCount", isAsc));
//                .and(ProblemSpecifications.weightedShuffledBy("solvedCount", isAsc));

        var userTagWeights = getUserTagWeights(userId);
        if (!userTagWeights.isEmpty()) {
            var totalTagWeights = getTotalTagWeights();
//            spec = spec.and(ProblemSpecifications.sortedByTagSimilarity(userId, userTagWeights, totalTagWeights));
        }

        var ret = problemRepository.findAll(spec, pageable).map(ProblemDto::from);

        return new PagedModel<>(ret);
    }

    /**
     * 문제 검색 결과를 가져옵니다.
     * @param page 페이지 번호
     * @param kw 검색 키워드
     * @param isAsc 오름차순 정렬 여부
     * @param userId 사용자 ID
     * @param sort 정렬 기준
     * @param levelStart 난이도 범위 시작
     * @param levelEnd 난이도 범위 끝
     * @return 검색 결과 목록 PagedModel
     */
    public PagedModel<ProblemDto> getProblemsSearch(int page, String kw, boolean isAsc, String userId, String sort, int levelStart, int levelEnd) {
        final String KEY = kw + "_" + userId + "_" + levelStart + "_" + levelEnd + "_" + isAsc + "_" + sort + "_" + page;
        if (searchCache.containsKey(KEY)) {
            return searchCache.get(KEY); // 검색 결과가 캐시에 이미 존재하면 캐시에서 가져옴
        }

        // userId가 $cnu인 경우 모든 사용자가 푼 문제를 가져옴
        final String ALL_USERS = "$cnu";
        // userId가 $onlyOneUser인 경우 단 한 명의 사용자가 푼 문제를 가져옴,
        // $onlyOneUser_로 시작할 경우 충남대에서 해당 사용자만 푼 문제를 가져옴
        final String ONLY_ONE_USER = "$onlyOneUser";
        // userId가 $notMe_로 시작할 경우 충남대에서 해당 사용자만 못 푼 문제를 가져옴
        final String NOT_ME = "$notMe";

        var defaultSort = Sort.Order.asc("problemId");
        List<Sort.Order> sorts = List.of(isAsc ? Sort.Order.asc(sort) : Sort.Order.desc(sort), defaultSort);

        var pageable = PageRequest.of(page, PAGE_SIZE, Sort.by(sorts));

        Page<ProblemDto> ret;

        var criteria = ProblemSearchCriteria.from(kw, levelStart, levelEnd, userId);

        if (userId.equals(ALL_USERS)) { // 모든 사용자가 푼 문제를 가져옴
            var tmp = problemRepository.findAllNotSolvedByAllUsers(criteria, pageable);
            ret = tmp.map(ProblemDto::from);
        } else if (userId.equals(ONLY_ONE_USER)) { // 단 한 명의 사용자가 푼 문제를 가져옴
            var tmp = problemRepository.findAllOnlyOneSolved(criteria, pageable);
            ret = tmp.map(ProblemDto::from);
        } else if (userId.startsWith(ONLY_ONE_USER + "_")) { // 충남대에서 userId만 푼 문제를 가져옴
            criteria.setUserId(userId.substring(ONLY_ONE_USER.length() + 1));
            var tmp = problemRepository.findAllOnlySolvedBy(criteria, pageable);
            ret = tmp.map(ProblemDto::from);
        } else if (userId.startsWith(NOT_ME + "_")) { // 충남대에서 userId만 못 푼 문제를 가져옴
            criteria.setUserId(userId.substring(NOT_ME.length() + 1));
            var tmp = problemRepository.findAllByOnlyNotSolvedBy(criteria, pageable);
            ret = tmp.map(ProblemDto::from);
        } else { // 전체 검색
            var tmp = problemRepository.findAllSearch(criteria, pageable);
            ret = tmp.map(ProblemDto::from);
        }

        searchCache.put(KEY, new PagedModel<>(ret)); // 검색 결과를 캐시에 저장

        return new PagedModel<>(ret);
    }

    /**
     * 문제 ID로 문제를 가져옵니다.
     * @param id 문제 ID
     * @return 문제 정보 DTO
     */
    public ProblemDto findProblemById(Integer id) {
        var ret = problemRepository.findById(id);
        if (ret.isEmpty()) {
            scrapeProblem(id);
            ret = problemRepository.findById(id);
        } else {
            var now = LocalDateTime.now();
            var lastModified = ret.get().getModifiedDate();
            if ((now.getDayOfYear() - lastModified.getDayOfYear() > 10 || now.getYear() != lastModified.getYear()) && !toUpdateProblems.contains(id)) {
                System.out.println("Problem " + id + " is too old. Added to update list.");
                toUpdateProblems.add(id);
            }
        }
        return ret.map(ProblemDto::from).orElse(null);
    }

    /**
     * 해당 크기 만큼의 스크래핑 되지 않은 문제를 스크래핑하여 DB에 저장합니다.
     * @param size 스크래핑할 문제 수 (최대 500)
     * @return 스크래핑된 문제 목록
     */
    public List<ProblemDto> scrapeUnscrapedProblems(Integer size) {
        final int maxId = 32940, maxBatch = 500;

        if (size < 1 || size > maxBatch) { throw new IllegalArgumentException("Invalid size"); }
        int cur = startId;
        var toScrape = new ArrayList<Integer>();
        while (cur <= maxId && toScrape.size() < size) {
            if (!existsProblem(cur) && !scrapeManager.isExcluded(cur)) {
                toScrape.add(cur);
            }
            cur++;
        }
        startId = cur;
        var scraped = scrapeManager.getProblems(toScrape);
        var ret = new ArrayList<ProblemDto>(addOrUpdateProblems(scraped));

        return ret;
    }

    /**
     * 범위 내 문제를 가져옵니다. 스크래핑되지 않은 문제는 스크래핑하여 DB에 저장합니다.
     * @param start 시작 문제 ID
     * @param end 끝 문제 ID
     * @return 문제 목록
     */
    public List<ProblemDto> findProblemsRange(Integer start, Integer end) {
        var ret = new ArrayList<ProblemDto>();
        var toScrape = new ArrayList<Integer>();
        for (int i = start; i <= end; ++i) {
            if (!existsProblem(i)) {
                toScrape.add(i);
            } else {
                ret.add(findProblemById(i));
            }
        }

        if (!toScrape.isEmpty()) {
            var scraped = scrapeManager.getProblems(toScrape);
            scraped.forEach(this::addOrUpdateProblem);
            ret.addAll(scraped);
        }

        ret.sort((a, b) -> a.getProblemId() - b.getProblemId());

        return ret;
    }

    /**
     * 문제를 추가하거나 수정합니다.
     * @param dto 문제 정보 DTO
     * @return 추가 또는 수정된 문제 정보 DTO
     */
    public ProblemDto addOrUpdateProblem(ProblemDto dto) {
        try {
            Problem existing = problemUpdateService.getProblemWithTagsById(dto.getProblemId());
            if (existing == null) {
                var problem = fromProblemDto(dto);
                return ProblemDto.from(problemRepository.save(problem));
            } else {
                return problemUpdateService.updateProblem(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 여러 문제를 추가하거나 수정합니다.
     * @param dtos 문제 정보 DTO 목록 List
     * @return 추가 또는 수정된 문제 정보 DTO 목록 List
     */
    public List<ProblemDto> addOrUpdateProblems(List<ProblemDto> dtos) {
        List<Problem> problems = new ArrayList<>();
        for (var dto : dtos) {
            Problem existing = problemRepository.findById(dto.getProblemId()).orElse(null);
            if (existing == null) {
                problems.add(fromProblemDto(dto));
            } else {
                problems.add(fromProblemDto(problemUpdateService.updateProblem(dto)));
            }
        }

        List<Problem> saved = problemRepository.saveAll(problems);
        return saved.stream().map(ProblemDto::from).collect(Collectors.toList());
    }

    /**
     * 해당 문제 ID의 문제가 DB에 존재하는지 확인합니다.
     * @param id 문제 ID
     * @return 문제 존재 여부
     */
    public boolean existsProblem(Integer id) {
        return problemRepository.existsById(id);
    }

    /**
     * 문제 ID로 문제를 가져옵니다.
     * @param id 문제 ID
     * @return 문제 정보
     */
    public Problem getProblem(Integer id) {
        return problemRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Problem not found: " + id));
    }

    /**
     * DB에 저장된 전체 문제 수를 가져옵니다.
     * @return 전체 문제 수
     */
    public Integer getProblemCount() {
        return (int) problemRepository.count();
    }

    /**
     * 충남대생이 푼 전체 문제 수를 가져옵니다.
     * @return 충남대생이 푼 전체 문제 수
     */
    public Integer getProblemCountSolvedByAllUsers() {
        return problemRepository.countAllByUsersIsNotEmpty();
    }

    /**
     * 해당 문제 ID의 문제 정보를 스크래핑하여 DB에 저장합니다.
     * @param id
     * @return
     */
    private ProblemDto scrapeProblem(Integer id) {
        var scraped = scrapeManager.getProblem(String.valueOf(id)).orElse(null);
        if (scraped == null) return null;
        return addOrUpdateProblem(scraped);
    }

    /**
     * 해당 태그 키로 태그를 찾거나 없으면 추가합니다.
     * @param dto 태그 정보 DTO
     * @return 태그 정보
     */
    private AlgorithmTag findTagByKey(AlgorithmTagDto dto) {
        var existing = algorithmTagRepository.findByKey(dto.getKey());
        if (existing == null) {
            existing = AlgorithmTag.from(dto);
            existing = algorithmTagRepository.save(existing);
        }
        return existing;
    }

    /**
     * 문제 정보 DTO를 문제 정보로 변환합니다.
     * @param dto 문제 정보 DTO
     * @return 문제 정보
     */
    private Problem fromProblemDto(ProblemDto dto) {
        var ret = Problem.builder()
                .problemId(dto.getProblemId())
                .title(dto.getTitle())
                .level(dto.getLevel())
                .solvedCount(dto.getSolvedCount())
                .votedCount(dto.getVotedCount())
                .givesNoRating(dto.getGivesNoRating())
                .averageTries(dto.getAverageTries())
                .source(dto.getSource())
                .korean(dto.getKorean())
                .solvable(dto.getSolvable())
                .build();

        var tags = Arrays.stream(dto.getTags()).map(x -> {
            var tag = findTagByKey(x);
            return ProblemAlgorithmTag.of(ret, tag);
        }).collect(Collectors.toList());
        ret.setTags(tags);
        return ret;
    }

    private Map<Integer, Long> getUserTagWeights(String userId) {
        if (userId == null || userId.isEmpty()) return Map.of();

        var tagFreqs = problemRepository.findTagFrequenciesByUserId(userId);
        return tagFreqs.stream().collect(Collectors.toMap(x -> (Integer) x[0], x -> (Long) x[1]));
    }

    private Map<Integer, Long> getTotalTagWeights() {
        var tagFreqs = problemRepository.findTagFrequencies();
        return tagFreqs.stream().collect(Collectors.toMap(x -> (Integer) x[0], x -> (Long) x[1]));
    }

    public ProblemUserInfo findProblemUsers(Integer id, Integer page) {
        final int PAGE_SIZE = 10;

        if (!problemUsersCache.containsKey(id)) {
            var problem = getProblem(id);
            var lst = problem.getUsers();
            lst.sort((a, b) -> a.getUser().getUserId().compareTo(b.getUser().getUserId()));
            problemUsersCache.put(id, lst.stream().map(bup -> {
                var user = bup.getUser();
                return new SimplifiedBojUser(user.getUserId(), user.getLevel());
            }).toList());
        }

        var lst = problemUsersCache.get(id);


        var cnt = lst.size();
        if (PAGE_SIZE * page >= cnt) return new ProblemUserInfo(List.of(), cnt);
        lst = lst.subList(PAGE_SIZE * page, Math.min(PAGE_SIZE * (page + 1), lst.size()));
        return new ProblemUserInfo(lst, cnt);
    }

    private List<String> reverseTagOf(List<String> orig) {
        initAllTagsIfEmpty();
        return allTags.stream().filter(x -> !orig.contains(x)).collect(Collectors.toList());
    }

    private void initAllTagsIfEmpty() {
        if (allTags.isEmpty()) {
            allTags.addAll(algorithmTagRepository.findAll().stream().map(AlgorithmTag::getKey).collect(Collectors.toSet()));
        }
    }

    public record SimplifiedBojUser(String userId, Integer level) {}
    public record ProblemUserInfo(List<SimplifiedBojUser> users, Integer totalCnt) {}
}
