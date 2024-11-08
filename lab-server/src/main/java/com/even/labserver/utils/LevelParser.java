package com.even.labserver.utils;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

public class LevelParser {

    public static String parseLevel(Integer level) {
        return table.stream()
                .filter(x -> x.getFirst().equals(level))
                .map(Pair::getSecond)
                .findAny()
                .orElse("Unrated");
    }

    public static Integer parseLevel(String level) {
        return table.stream()
                .filter(x -> x.getSecond().equals(level))
                .map(Pair::getFirst)
                .findAny()
                .orElse(0);
    }

    private static final List<Pair<Integer, String>> table = new ArrayList<>();

    static {
        table.add(Pair.of(0, "Unrated"));
        table.add(Pair.of(1, "Bronze V"));
        table.add(Pair.of(2, "Bronze IV"));
        table.add(Pair.of(3, "Bronze III"));
        table.add(Pair.of(4, "Bronze II"));
        table.add(Pair.of(5, "Bronze I"));
        table.add(Pair.of(6, "Silver V"));
        table.add(Pair.of(7, "Silver IV"));
        table.add(Pair.of(8, "Silver III"));
        table.add(Pair.of(9, "Silver II"));
        table.add(Pair.of(10, "Silver I"));
        table.add(Pair.of(11, "Gold V"));
        table.add(Pair.of(12, "Gold IV"));
        table.add(Pair.of(13, "Gold III"));
        table.add(Pair.of(14, "Gold II"));
        table.add(Pair.of(15, "Gold I"));
        table.add(Pair.of(16, "Platinum V"));
        table.add(Pair.of(17, "Platinum IV"));
        table.add(Pair.of(18, "Platinum III"));
        table.add(Pair.of(19, "Platinum II"));
        table.add(Pair.of(20, "Platinum I"));
        table.add(Pair.of(21, "Diamond V"));
        table.add(Pair.of(22, "Diamond IV"));
        table.add(Pair.of(23, "Diamond III"));
        table.add(Pair.of(24, "Diamond II"));
        table.add(Pair.of(25, "Diamond I"));
        table.add(Pair.of(26, "Ruby V"));
        table.add(Pair.of(27, "Ruby IV"));
        table.add(Pair.of(28, "Ruby III"));
        table.add(Pair.of(29, "Ruby II"));
        table.add(Pair.of(30, "Ruby I"));
        table.add(Pair.of(31, "Master"));
    }

    private LevelParser() { }
}
