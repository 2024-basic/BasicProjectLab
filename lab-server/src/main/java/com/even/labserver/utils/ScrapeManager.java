package com.even.labserver.utils;

import com.even.labserver.bojuser.BojUserDto;
import com.even.labserver.problem.ProblemDto;
import com.even.labserver.problem.tag.AlgorithmTagDto;
import com.google.gson.JsonParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ScrapeManager {
    static final String UserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Whale/3.28.266.14 Safari/537.36";
    static final String BojUrl = "https://www.acmicpc.net/";
    static final String SolvedUrl = "https://solved.ac/";

    public Optional<ProblemDto> getProblem(String problemId) {
        final String url = "https://solved.ac/api/v3/problem/show?problemId=" + problemId;
        var doc = requestJson(url);
        if (doc.isEmpty()) {
            return Optional.empty();
        }

        var problem = fromProblemJson(doc.get());

        return Optional.of(problem);
    }

    public Optional<BojUserDto> getUser(String userId) {
        try {
            final String url = BojUrl + "user/" + userId;
            var doc = requestDocument(url).orElse(null);
            if (doc == null) {
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

    public List<Integer> getSolvedProblemsOf(String userId) {
        final String url = BojUrl + "user/" + userId;
        var doc = requestDocument(url).orElse(null);
        if (doc == null) {
            return new ArrayList<>();
        }
        var problemList = doc.selectXpath(".//div[@class='problem-list']").get(0).children();

        return problemList.stream().map(e -> Integer.parseInt(e.text())).collect(Collectors.toList());
    }

    public List<ProblemDto> getProblems(List<Integer> ids) {
        List<ProblemDto> ret = new ArrayList<>();
        for (int i = 0; i < ids.size(); i += BATCH_SIZE) {
            var subList = ids.subList(i, Math.min(i + BATCH_SIZE, ids.size()));
            ret.addAll(getProblemsInternal(subList));
        }

        return ret;
    }

    public List<ProblemDto> getProblemsRange(int startId, int endId) {
        if (startId < 1000 || endId < 1000 || endId > 38000 || startId > endId) {
            throw new IllegalArgumentException("Invalid range");
        }

        List<ProblemDto> ret = new ArrayList<>();
        for (int i = startId; i <= endId; i += BATCH_SIZE) {
            var ids = new ArrayList<Integer>();
            for (int j = i; j < i + BATCH_SIZE && j <= endId; ++j) {
                ids.add(j);
            }
            ret.addAll(getProblemsInternal(ids));
        }

        return ret;
    }

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

        return ret;
    }

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

    private static Optional<Document> requestDocument(String url) {
        try {
            return Optional.of(Jsoup.connect(url)
                    .userAgent(UserAgent).get());
        } catch (IOException e) {
            System.out.println("Failed to connect to " + url + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    private static Optional<String> requestJson(String url) {
        try {
            return Optional.of(Jsoup.connect(url)
                    .ignoreContentType(true)
                    .userAgent(UserAgent).execute().body());
        } catch (IOException e) {
            System.out.println("Failed to connect to " + url + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    static final int BATCH_SIZE = 50;
}
