package com.meetingbot.service;

import com.meetingbot.model.Meeting;
import com.meetingbot.model.PreferenceRule;
import com.meetingbot.model.ScheduleRule;
import com.meetingbot.repository.MeetingRepository;
import com.meetingbot.repository.PreferenceRuleRepository;
import com.meetingbot.repository.ScheduleRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SchedulingEngineService {

    private final MeetingRepository meetingRepository;
    private final ScheduleRuleRepository scheduleRuleRepository;
    private final PreferenceRuleRepository preferenceRuleRepository;

    @Autowired
    public SchedulingEngineService(MeetingRepository meetingRepository,
                                   ScheduleRuleRepository scheduleRuleRepository,
                                   PreferenceRuleRepository preferenceRuleRepository) {
        this.meetingRepository = meetingRepository;
        this.scheduleRuleRepository = scheduleRuleRepository;
        this.preferenceRuleRepository = preferenceRuleRepository;
    }

    public static class SchedulingResult {
        private final LocalDateTime finalProposedTime;
        private final String justificationString;

        public SchedulingResult(LocalDateTime finalProposedTime, String justificationString) {
            this.finalProposedTime = finalProposedTime;
            this.justificationString = justificationString;
        }

        public LocalDateTime getFinalProposedTime() { return finalProposedTime; }
        public String getJustificationString() { return justificationString; }
    }

    /**
     * Entry point for calculating the best meeting time slot.
     */
    public SchedulingResult negotiateMeeting(String meetingId, LocalDateTime searchStart, LocalDateTime searchEnd) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("Meeting not found: " + meetingId));

        List<ScheduleRule> busyBlocks = scheduleRuleRepository.findByMeetingId(meetingId);
        List<PreferenceRule> preferences = preferenceRuleRepository.findByMeetingId(meetingId);

        // O(D * H) where D is days in search window and H is slots per day.
        // We brute-force generate possible slots within the search window, because
        // bounded candidate generation is highly predictable and easier to score compared to complex interval math.
        List<LocalDateTime> candidateSlots = generateCandidateSlots(searchStart, searchEnd, meeting.getDurationMinutes());

        // O(C * B) where C is candidate slots and B is number of busy blocks.
        // We filter out any candidate slot that overlaps with any busy block from any invitee.
        List<LocalDateTime> validSlots = calculateValidIntersections(candidateSlots, busyBlocks, meeting.getDurationMinutes());

        if (validSlots.isEmpty()) {
            return resolveConflicts();
        }

        // O(V * P) where V is valid slots and P is preference rules.
        // Calculate the score for each valid slot based on all preference rules.
        LocalDateTime bestSlot = null;
        int maxScore = Integer.MIN_VALUE;

        for (LocalDateTime slot : validSlots) {
            int score = scoreAvailableSlots(slot, preferences);
            if (score > maxScore) {
                maxScore = score;
                bestSlot = slot;
            }
        }

        String justification = buildJustification(bestSlot, maxScore, preferences);

        meeting.setFinalProposedTime(bestSlot);
        meeting.setStatus(Meeting.Status.NEGOTIATED);
        meetingRepository.save(meeting);

        return new SchedulingResult(bestSlot, justification);
    }

    /**
     * Generates a candidate list of potential slots in 30-minute increments.
     * O(N) where N is the number of 30-minute intervals between start and end.
     */
    private List<LocalDateTime> generateCandidateSlots(LocalDateTime start, LocalDateTime end, int durationMinutes) {
        List<LocalDateTime> candidates = new ArrayList<>();
        LocalDateTime current = start;
        while (!current.plusMinutes(durationMinutes).isAfter(end)) {
            candidates.add(current);
            current = current.plusMinutes(30);
        }
        return candidates;
    }

    /**
     * Filters candidate slots by strictly eliminating any slot that overlaps with a busy block.
     * O(C * B) where C is the number of candidates and B is the number of busy blocks.
     */
    private List<LocalDateTime> calculateValidIntersections(List<LocalDateTime> candidates, List<ScheduleRule> busyBlocks, int durationMinutes) {
        return candidates.stream().filter(slot -> {
            LocalDateTime slotEnd = slot.plusMinutes(durationMinutes);
            for (ScheduleRule rule : busyBlocks) {
                // If a slot overlaps with a busy block, reject the slot
                if (slot.isBefore(rule.getBusyEnd()) && slotEnd.isAfter(rule.getBusyStart())) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
    }

    /**
     * Scores a given slot by aggregating weights from all preference rules.
     * O(P) where P is the number of preference rules.
     */
    private int scoreAvailableSlots(LocalDateTime slot, List<PreferenceRule> preferences) {
        int score = 0;
        for (PreferenceRule rule : preferences) {
            try {
                switch (rule.getPreferenceType()) {
                    case PREFERRED_DAY:
                        if (slot.getDayOfWeek().name().equalsIgnoreCase(rule.getValue())) {
                            score += rule.getWeight();
                        }
                        break;
                    case PREFERRED_TIME_RANGE:
                        String[] parts = rule.getValue().split("-");
                        if (parts.length == 2) {
                            LocalTime prefStart = LocalTime.parse(parts[0]);
                            LocalTime prefEnd = LocalTime.parse(parts[1]);
                            LocalTime slotTime = slot.toLocalTime();
                            if (!slotTime.isBefore(prefStart) && slotTime.isBefore(prefEnd)) {
                                score += rule.getWeight();
                            }
                        }
                        break;
                    case AVOID_BEFORE:
                        LocalTime avoidBefore = LocalTime.parse(rule.getValue());
                        if (slot.toLocalTime().isBefore(avoidBefore)) {
                            score += rule.getWeight();
                        }
                        break;
                    case AVOID_AFTER:
                        LocalTime avoidAfter = LocalTime.parse(rule.getValue());
                        if (slot.toLocalTime().isAfter(avoidAfter)) {
                            score += rule.getWeight();
                        }
                        break;
                }
            } catch (Exception e) {
                // Ignore malformed rule values
            }
        }
        return score;
    }

    /**
     * Fallback when no intersecting time can be found.
     * O(1) constant time.
     */
    private SchedulingResult resolveConflicts() {
        return new SchedulingResult(null, "No slot matched all availability constraints; unable to find a common time block that satisfies all invitees.");
    }

    /**
     * Constructs a human-readable justification of why a slot was chosen.
     * O(1) constant time.
     */
    private String buildJustification(LocalDateTime slot, int score, List<PreferenceRule> preferences) {
        if (preferences.isEmpty()) {
            return "Matches 100% availability across all invitees. No special preferences were provided.";
        }
        
        if (score > 0) {
            return "Matches 100% availability across all invitees and optimally satisfies the group's soft preferences with a score of " + score + ".";
        } else if (score < 0) {
            return "Matches 100% availability across all invitees, but compromises on some preferences (score: " + score + ") as it was the only available overlapping time.";
        } else {
            return "Matches 100% availability across all invitees. Selected as a neutral option that did not heavily conflict with or strongly satisfy any preferences.";
        }
    }
}
