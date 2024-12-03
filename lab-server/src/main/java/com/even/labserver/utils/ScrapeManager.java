package com.even.labserver.utils;

import com.even.labserver.bojuser.BojUserDto;
import com.even.labserver.problem.ProblemDto;
import com.even.labserver.problem.tag.AlgorithmTagDto;
import com.google.gson.JsonParser;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.persistence.PostLoad;
import org.aspectj.util.FileUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cglib.core.Local;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 백준과 solved.ac로부터 정보를 스크래핑하는 클래스
 */
@Component
public class ScrapeManager {
    /**
     * 브라우저 User-Agent (봇 차단 방지용)
     */
    static final String UserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Whale/3.28.266.14 Safari/537.36";
    static final String BojUrl = "https://www.acmicpc.net/";
    static final String SolvedUrl = "https://solved.ac/";
    static final String excludedIdsPath = "assets/excludedIds.txt";
    static final int BATCH_SIZE = 50;

    /**
     * 존재하지 않는 문제 ID 목록 (스크래핑 실패한 문제들)
     */
    final Set<Integer> excludedIds = new HashSet<>();

    /**
     * 존재하지 않는 문제 ID 목록을 파일로부터 로드 (서버 실행 시 호출됨)
     */
    @PostConstruct
    public void loadExcludedIds() {
        try {
            if (!new File(excludedIdsPath).exists()) {
                System.out.println(excludedIdsPath + " not found");
                return;
            }
            var ids = FileUtil.readAsLines(new File(excludedIdsPath));
            for (var id : ids) {
                excludedIds.add(Integer.parseInt(id));
            }
            System.out.println("Excluded IDs loaded");
        } catch (Exception e) {
            System.out.println("Failed to load excluded IDs: " + e.getMessage());
        }
    }

    /**
     * 존재하지 않는 문제 ID 목록을 파일에 저장 (서버 종료 시 호출됨)
     */
    @PreDestroy
    public void saveExcludedIds() {
        FileUtil.writeAsString(new File(excludedIdsPath), excludedIds.stream().map(Object::toString).reduce((x, y) -> x + "\n" + y).orElse(""));
        System.out.println("Excluded IDs saved");
    }

    /**
     * 해당 문제 ID가 존재하지 않는지 확인
     */
    public boolean isExcluded(int id) {
        return excludedIds.contains(id);
    }

    /**
     * 해당 문제를 스크래핑하여 반환
     * @param problemId 문제 ID
     * @return 문제 정보 DTO
     */
    public Optional<ProblemDto> getProblem(String problemId) {
        final String url = "https://solved.ac/api/v3/problem/show?problemId=" + problemId;
        var doc = requestJson(url);
        if (doc.isEmpty()) {
            return Optional.empty();
        }

        var problem = fromProblemJson(doc.get());

        return Optional.of(problem);
    }

    /**
     * 해당 사용자의 정보를 스크래핑하여 반환
     * @param userId 백준 계정 ID
     * @return 사용자 정보 DTO
     */
    public Optional<BojUserDto> getUser(String userId) {
        try {
            final String url = BojUrl + "user/" + userId;
            var doc = requestDocument(url).orElse(null);
            if (doc == null || doc.html().isEmpty()) {
                return Optional.empty();
            }
            var ret = BojUserDto.builder().userId(userId).build();

            var solvedacTier = doc.selectXpath(".//img[@class='solvedac-tier']").get(0);
            Arrays.stream(solvedacTier.attribute("src").getValue().split("/"))
                    .filter(x -> x.endsWith(".svg")).findAny().ifPresent(x -> {
                        ret.setLevel(Integer.parseInt(x.substring(0, x.length() - 4)));
                    });

            var problemList = doc.selectXpath(".//div[@class='problem-list']").get(0).children();
            ret.setSolvedProblems(problemList.stream().map(e -> Integer.parseInt(e.text())).toArray(Integer[]::new));
            ret.setSolved(problemList.size());

            return Optional.of(ret);
        } catch (Exception e) {
            System.out.println("Failed to get user " + userId + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * 여러 개의 문제를 스크래핑하여 반환
     * @param ids 문제 ID 목록 List
     * @return 문제 정보 DTO 목록 List
     */
    public List<ProblemDto> getProblems(List<Integer> ids) {
        List<ProblemDto> ret = new ArrayList<>();
        for (int i = 0; i < ids.size(); i += BATCH_SIZE) {
            var subList = ids.subList(i, Math.min(i + BATCH_SIZE, ids.size()));
            ret.addAll(getProblemsInternal(subList));
        }

        System.out.println(LogUtils.prefix() + "Scraped " + ret.size() + " problems");
        return ret;
    }

    /**
     * 충남대생 목록을 반환
     * @return 충남대생 ID 목록 List
     */
    public List<String> getSchoolUsers() {
        List<String> ret = new ArrayList<>();
        int page = 1;
        while (page < 100) {
            var users = getSchoolUsersInternal(page);
            if (users.isEmpty()) {
                break;
            }
            ret.addAll(users.keySet());
            ++page;
        }

        return ret;
    }

    /**
     * 충남대생 목록과 그들의 푼 문제 수를 반환
     * @return 충남대생 ID와 푼 문제 수 Map
     */
    public Map<String, Integer> getSchoolUsersAndCounts() {
        Map<String, Integer> ret = new HashMap<>();
        int page = 1;
        while (page < 100) {
            var users = getSchoolUsersInternal(page);
            if (users.isEmpty()) {
                break;
            }
            ret.putAll(users);
            ++page;
        }

        return ret;
    }

    /**
     * 충남대의 랭킹 정보를 반환
     * @return 전체 랭킹과 대학 랭킹 정보
     */
    public SchoolRanking getSchoolRanking() {
        final String school = "충남대학교";

        var totalDoc = requestDocument("https://www.acmicpc.net/ranklist/school").orElse(null);
        var univDoc = requestDocument("https://www.acmicpc.net/ranklist/university").orElse(null);
        if (totalDoc == null || univDoc == null) {
            return new SchoolRanking(-1, -1, -1,null, null, null);
        }

        var rows = totalDoc.selectXpath(".//tbody/tr");
        int total = -1, univ = -1, solved = -1, solvedPrevTotal = -1, solvedPrevUniv = -1;
        String prevTotal = null, prevUniv = null;
        for (int i = 0; i < rows.size(); ++i) {
            var row = rows.get(i);
            if (row.select("td").get(1).text().trim().equals(school)) {
                total = i + 1;
                solved = Integer.parseInt(row.select("td").get(3).text().trim());
                if (i > 0) {
                    prevTotal = rows.get(i - 1).select("td").get(1).text().trim();
                    solvedPrevTotal = Integer.parseInt(rows.get(i - 1).select("td").get(3).text().trim());
                }
                break;
            }
        }

        rows = univDoc.selectXpath(".//tbody/tr");
        for (int i = 0; i < rows.size(); ++i) {
            var row = rows.get(i);
            if (row.select("td").get(1).text().trim().equals(school)) {
                univ = i + 1;
                if (i > 0) {
                    prevUniv = rows.get(i - 1).select("td").get(1).text().trim();
                    solvedPrevUniv = Integer.parseInt(rows.get(i - 1).select("td").get(3).text().trim());
                }
                break;
            }
        }

        return new SchoolRanking(total, univ, solved, null,
                new SchoolRanking(total - 1, -1, solvedPrevTotal, prevTotal, null, null),
                new SchoolRanking(-1, univ - 1, solvedPrevUniv, prevUniv,null, null));
    }

    /**
     * 해당 사용자가 마지막으로 푼 문제의 제출 시간을 반환
     * @param userId 백준 계정 ID
     * @return 마지막으로 푼 문제의 제출 시간 LocalDateTime
     */
    public LocalDateTime getLastSolvedTime(String userId) {
        final String url = BojUrl + "status?option-status-pid=on&problem_id=&user_id=" + userId + "&language_id=-1&result_id=4";
        var doc = requestDocument(url).orElse(null);
        if (doc == null) {
            return LocalDateTime.MIN;
        }

        var firstRow = doc.selectXpath(".//tbody/tr").get(0);
        var node = firstRow.selectXpath(".//td[9]").first().selectXpath(".//a").first();
        var time = LocalDateTime.parse(node.attribute("title").getValue(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return time;
    }

    /**
     * 충남대생 목록을 반환 페이지별로 호출되는 내부함수
     * @param page 페이지 번호
     * @return 해당 페이지에서의 충남대생 ID 목록 Map
     */
    private Map<String, Integer> getSchoolUsersInternal(int page) {
        final String url = BojUrl + "school/ranklist/385/" + page;
        try {
            var doc = requestDocument(url).orElse(null);
            if (doc == null) return new HashMap<>();
            var users = doc.selectXpath(".//table[@id='ranklist']/tbody/tr/td[2]/a");
            var count = doc.selectXpath(".//table[@id='ranklist']/tbody/tr/td[4]");
            var ret = new HashMap<String, Integer>();
            for (int i = 0; i < users.size(); ++i) {
                ret.put(users.get(i).text().trim(), Integer.parseInt(count.get(i).text().trim()));
            }
//            return users.stream().map(x -> x.text().trim()).collect(Collectors.toList());
            return ret;
        } catch (Exception e) {
            System.out.println("Failed to get school users: " + e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * 문제 ID 목록에 대한 문제 정보를 반환하는 내부함수
     * @param ids 50개 이하의 문제 ID 목록 List
     * @return 문제 정보 DTO 목록 List
     */
    private List<ProblemDto> getProblemsInternal(List<Integer> ids) {
        if (ids.size() > BATCH_SIZE) {
            throw new IllegalArgumentException("Too many IDs, should be less than " + BATCH_SIZE);
        }

        var sb = new StringBuilder("https://solved.ac/api/v3/problem/lookup?problemIds=");
        for (var id: ids) {
            sb.append(id).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);

        final String url = sb.toString();
        var doc = requestJson(url);
        var ret = new ArrayList<ProblemDto>();
        if (doc.isEmpty()) {
            return ret;
        }
        var problemsArray = JsonParser.parseString(doc.get()).getAsJsonArray();

        for (var problemElement : problemsArray) {
            var problem = fromProblemJson(problemElement.toString());
            ret.add(problem);
        }

        boolean added = false;
        for (var id : ids) {
            if (ret.stream().anyMatch(x -> Objects.equals(x.getProblemId(), id))) {
                continue;
            }
            excludedIds.add(id);
            added = true;
        }

        if (added) saveExcludedIds();

        return ret;
    }

    /**
     * 문제 정보 JSON을 ProblemDto로 변환하는 내부함수
     * @param json JSON으로 파싱가능한 문자열
     * @return 문제 정보 DTO
     */
    private static ProblemDto fromProblemJson(String json) {
        var problemElement = JsonParser.parseString(json).getAsJsonObject();
        var ret = ProblemDto.builder()
                .problemId(problemElement.get("problemId").getAsInt())
                .title(problemElement.get("titleKo").getAsString())
                .level(problemElement.get("level").getAsInt())
                .solvedCount(problemElement.get("acceptedUserCount").getAsInt())
                .votedCount(problemElement.get("votedUserCount").getAsInt())
                .givesNoRating(problemElement.get("givesNoRating").getAsBoolean())
                .averageTries(problemElement.get("averageTries").getAsDouble())
                .build();

        var tagsArray = problemElement.getAsJsonArray("tags");
        var tags = new AlgorithmTagDto[tagsArray.size()];
        for (int i = 0; i < tagsArray.size(); ++i) {
            var tagElement = tagsArray.get(i).getAsJsonObject();
            var tag = AlgorithmTagDto.builder()
                    .key(tagElement.get("key").getAsString())
                    .problemCount(tagElement.get("problemCount").getAsInt())
                    .displayName(tagElement.getAsJsonArray("displayNames").get(0).getAsJsonObject().get("name").getAsString())
                    .build();
            tags[i] = tag;
        }
        ret.setTags(tags);

        return ret;
    }

    /**
     * 해당 URL로부터 Document를 요청하는 내부함수
     * @param url 요청할 URL
     * @return 요청 결과 Document
     */
    private static Optional<Document> requestDocument(String url) {
        try {
            System.out.println(LogUtils.prefix() + "Requesting " + url);
            var ret = Optional.of(Jsoup.connect(url)
                    .userAgent(UserAgent).get());
            return ret;
        } catch (IOException e) {
            System.out.println("Failed to connect to " + url + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * 해당 URL로부터 JSON을 요청하는 내부함수
     * @param url 요청할 URL
     * @return 요청 결과 JSON 문자열
     */
    private static Optional<String> requestJson(String url) {
        try {
            System.out.println(LogUtils.prefix() + "Requesting " + url);
            var ret = Optional.of(Jsoup.connect(url)
                    .ignoreContentType(true)
                    .userAgent(UserAgent).execute().body());
            return ret;
        } catch (IOException e) {
            System.out.println("Failed to connect to " + url + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    public record SchoolRanking(Integer total, Integer univ, Integer solved, String name, SchoolRanking prevTotal, SchoolRanking prevUniv) {}
}
