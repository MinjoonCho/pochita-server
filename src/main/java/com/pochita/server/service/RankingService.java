package com.pochita.server.service;

import com.pochita.server.domain.DistractionSession;
import com.pochita.server.domain.GroupMember;
import com.pochita.server.domain.StudyGroup;
import com.pochita.server.domain.User;
import com.pochita.server.common.UniversityNormalizer;
import com.pochita.server.dto.RankingDtos.CategoryLeaderEntry;
import com.pochita.server.dto.RankingDtos.RankingEntry;
import com.pochita.server.dto.RankingDtos.UserStatsResponse;
import com.pochita.server.repository.GroupMemberRepository;
import com.pochita.server.repository.StudyGroupRepository;
import com.pochita.server.repository.UserRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RankingService {

    private record CategoryMeta(String id, String label, String emoji) {
    }

    private static final List<CategoryMeta> CATEGORY_META = List.of(
            new CategoryMeta("game", "게임", "🎮"),
            new CategoryMeta("shortform", "숏폼", "📱"),
            new CategoryMeta("ott", "OTT", "📺"),
            new CategoryMeta("drink", "음주", "🍺"),
            new CategoryMeta("hangout", "친구랑 놀기", "👥"),
            new CategoryMeta("lazy", "무기력", "😴")
    );

    private final UserRepository userRepository;
    private final StudyGroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;
    private final SessionService sessionService;

    public RankingService(
            UserRepository userRepository,
            StudyGroupRepository groupRepository,
            GroupMemberRepository memberRepository,
            SessionService sessionService
    ) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
        this.sessionService = sessionService;
    }

    public List<RankingEntry> getUniversityRanking() {
        Map<String, Long> totals = new LinkedHashMap<>();
        List<User> users = userRepository.findAll();
        for (DistractionSession session : sessionService.getCompletedSessions()) {
            User user = users.stream().filter(candidate -> candidate.getId().equals(session.getUserId())).findFirst().orElse(null);
            if (user == null || user.getUniversity().isBlank() || session.getDuration() == null) continue;
            totals.merge(UniversityNormalizer.normalize(user.getUniversity()), session.getDuration(), Long::sum);
        }
        return totals.entrySet().stream()
                .map(entry -> new RankingEntry(entry.getKey(), entry.getKey(), null, entry.getValue(), entry.getValue() / 60, 0))
                .sorted((a, b) -> Long.compare(b.sec(), a.sec()))
                .toList();
    }

    public List<RankingEntry> getUserRanking() {
        Map<String, Long> totals = new LinkedHashMap<>();
        List<User> users = userRepository.findAll();
        for (DistractionSession session : sessionService.getCompletedSessions()) {
            if (session.getDuration() == null) continue;
            totals.merge(session.getUserId(), session.getDuration(), Long::sum);
        }
        return users.stream()
                .map(user -> new RankingEntry(user.getId(), user.getNickname(), user.getAvatarEmoji(), totals.getOrDefault(user.getId(), 0L), totals.getOrDefault(user.getId(), 0L) / 60, 0))
                .sorted((a, b) -> Long.compare(b.sec(), a.sec()))
                .toList();
    }

    public List<CategoryLeaderEntry> getCategoryRanking() {
        Map<String, User> usersById = userRepository.findAll().stream()
                .collect(java.util.stream.Collectors.toMap(User::getId, user -> user));
        Map<String, Map<String, Long>> totalsByCategory = new LinkedHashMap<>();

        for (DistractionSession session : sessionService.getCompletedSessions()) {
            if (session.getDuration() == null) continue;
            totalsByCategory
                    .computeIfAbsent(session.getCategoryId(), ignored -> new LinkedHashMap<>())
                    .merge(session.getUserId(), session.getDuration(), Long::sum);
        }

        return CATEGORY_META.stream()
                .map(category -> {
                    Map<String, Long> totals = totalsByCategory.getOrDefault(category.id(), Map.of());
                    Map.Entry<String, Long> winner = totals.entrySet().stream()
                            .max(Map.Entry.comparingByValue())
                            .orElse(null);
                    User winnerUser = winner == null ? null : usersById.get(winner.getKey());
                    long seconds = winner == null ? 0 : winner.getValue();

                    return new CategoryLeaderEntry(
                            category.id(),
                            category.label(),
                            category.emoji(),
                            winnerUser == null ? null : winnerUser.getId(),
                            winnerUser == null ? null : winnerUser.getNickname(),
                            winnerUser == null ? null : winnerUser.getAvatarEmoji(),
                            seconds,
                            seconds / 60
                    );
                })
                .sorted((a, b) -> Long.compare(b.sec(), a.sec()))
                .toList();
    }

    public List<RankingEntry> getGroupRanking() {
        List<StudyGroup> groups = groupRepository.findAll();
        List<GroupMember> members = memberRepository.findAll();
        Map<String, List<String>> groupIdsByUser = new LinkedHashMap<>();
        for (GroupMember member : members) {
            groupIdsByUser.computeIfAbsent(member.getUserId(), ignored -> new ArrayList<>()).add(member.getGroupId());
        }

        Map<String, Long> totals = new LinkedHashMap<>();
        for (DistractionSession session : sessionService.getCompletedSessions()) {
            if (session.getDuration() == null) continue;
            List<String> groupIds = groupIdsByUser.getOrDefault(session.getUserId(), List.of());
            for (String groupId : groupIds) {
                totals.merge(groupId, session.getDuration(), Long::sum);
            }
        }

        return groups.stream()
                .map(group -> new RankingEntry(
                        group.getId(),
                        group.getName(),
                        group.getEmoji(),
                        totals.getOrDefault(group.getId(), 0L),
                        totals.getOrDefault(group.getId(), 0L) / 60,
                        (int) members.stream().filter(member -> member.getGroupId().equals(group.getId())).count()
                ))
                .sorted((a, b) -> Long.compare(b.sec(), a.sec()))
                .toList();
    }

    public UserStatsResponse getUserStats(String userId) {
        List<DistractionSession> userSessions = sessionService.getUserSessions(userId);
        List<DistractionSession> todaySessions = sessionService.getTodaySessions(userId);
        long totalSec = sumDuration(userSessions);
        long todaySec = sumDuration(todaySessions);
        long averageSec = userSessions.isEmpty() ? 0 : totalSec / userSessions.size();
        long maxSessionSec = userSessions.stream().map(DistractionSession::getDuration).filter(java.util.Objects::nonNull).mapToLong(Long::longValue).max().orElse(0);

        Map<String, Long> byCategory = new LinkedHashMap<>();
        for (DistractionSession session : userSessions) {
            if (session.getDuration() == null) continue;
            byCategory.merge(session.getCategoryId(), session.getDuration(), Long::sum);
        }

        List<Map<String, Object>> categoryBreakdown = byCategory.entrySet().stream()
                .map(entry -> Map.<String, Object>of(
                        "categoryId", entry.getKey(),
                        "seconds", entry.getValue(),
                        "minutes", entry.getValue() / 60
                ))
                .toList();

        List<Map<String, Object>> weeklyBreakdown = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate targetDate = LocalDate.now().minusDays(i);
            long start = targetDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long end = targetDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long sec = userSessions.stream()
                    .filter(session -> session.getStartTime() >= start && session.getStartTime() < end)
                    .map(DistractionSession::getDuration)
                    .filter(java.util.Objects::nonNull)
                    .mapToLong(Long::longValue)
                    .sum();
            weeklyBreakdown.add(Map.of(
                    "label", switch (targetDate.getDayOfWeek()) {
                        case SUNDAY -> "일";
                        case MONDAY -> "월";
                        case TUESDAY -> "화";
                        case WEDNESDAY -> "수";
                        case THURSDAY -> "목";
                        case FRIDAY -> "금";
                        case SATURDAY -> "토";
                    },
                    "seconds", sec
            ));
        }

        return new UserStatsResponse(userId, todaySec, totalSec, averageSec, maxSessionSec, categoryBreakdown, weeklyBreakdown);
    }

    private long sumDuration(List<DistractionSession> sessions) {
        return sessions.stream()
                .map(DistractionSession::getDuration)
                .filter(java.util.Objects::nonNull)
                .mapToLong(Long::longValue)
                .sum();
    }
}
