package com.pochita.server.controller;

import com.pochita.server.dto.RankingDtos.RankingEntry;
import com.pochita.server.dto.RankingDtos.CategoryLeaderEntry;
import com.pochita.server.dto.RankingDtos.UserStatsResponse;
import com.pochita.server.service.RankingService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RankingController {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @GetMapping("/rankings/universities")
    public List<RankingEntry> getUniversityRanking() {
        return rankingService.getUniversityRanking();
    }

    @GetMapping("/rankings/groups")
    public List<RankingEntry> getGroupRanking() {
        return rankingService.getGroupRanking();
    }

    @GetMapping("/rankings/users")
    public List<RankingEntry> getUserRanking() {
        return rankingService.getUserRanking();
    }

    @GetMapping("/rankings/categories")
    public List<CategoryLeaderEntry> getCategoryRanking() {
        return rankingService.getCategoryRanking();
    }

    @GetMapping("/stats/users/{userId}")
    public UserStatsResponse getUserStats(@PathVariable String userId) {
        return rankingService.getUserStats(userId);
    }
}
