package com.pochita.server.common;

import java.util.Map;

public final class UniversityNormalizer {

    private static final Map<String, String> NORMALIZATION_MAP = Map.ofEntries(
            Map.entry("서울대", "서울대학교"),
            Map.entry("경희대", "경희대학교"),
            Map.entry("연세대", "연세대학교(서울)"),
            Map.entry("연세대학교", "연세대학교(서울)"),
            Map.entry("고려대", "고려대학교(서울)"),
            Map.entry("고려대학교", "고려대학교(서울)"),
            Map.entry("명지대", "명지대학교(자연)"),
            Map.entry("명지대학교", "명지대학교(자연)"),
            Map.entry("명지대(인문)", "명지대학교(인문)"),
            Map.entry("명지대(자연)", "명지대학교(자연)"),
            Map.entry("상명대", "상명대학교(서울)"),
            Map.entry("상명대학교", "상명대학교(서울)"),
            Map.entry("상명대(서울)", "상명대학교(서울)"),
            Map.entry("상명대(천안)", "상명대학교(천안)"),
            Map.entry("한국외대", "한국외국어대학교"),
            Map.entry("한국외국어대학교", "한국외국어대학교"),
            Map.entry("한국외대(글로벌)", "한국외국어대학교(글로벌)")
    );

    private UniversityNormalizer() {
    }

    public static String normalize(String university) {
        String trimmed = university == null ? "" : university.trim();
        if (trimmed.isEmpty()) return trimmed;
        return NORMALIZATION_MAP.getOrDefault(trimmed, trimmed);
    }
}
