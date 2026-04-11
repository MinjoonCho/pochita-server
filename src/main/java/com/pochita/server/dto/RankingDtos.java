package com.pochita.server.dto;

import java.util.List;
import java.util.Map;

public final class RankingDtos {

    private RankingDtos() {
    }

    public record RankingEntry(String id, String name, String emoji, long sec, long minutes, int memberCount) {
    }

    public record CategoryLeaderEntry(
            String categoryId,
            String categoryName,
            String categoryEmoji,
            String winnerUserId,
            String winnerName,
            String winnerEmoji,
            long sec,
            long minutes
    ) {
    }

    public record UserStatsResponse(
            String userId,
            long todaySec,
            long totalSec,
            long averageSec,
            long maxSessionSec,
            List<Map<String, Object>> categoryBreakdown,
            List<Map<String, Object>> weeklyBreakdown
    ) {
    }
}
