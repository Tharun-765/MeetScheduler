package com.meetingbot.controller;

import com.meetingbot.dto.ScheduleRuleRequestDTO;
import com.meetingbot.model.ScheduleRule;
import com.meetingbot.repository.ScheduleRuleRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedule-rules")
public class ScheduleRuleController {

    private final ScheduleRuleRepository scheduleRuleRepository;

    @Autowired
    public ScheduleRuleController(ScheduleRuleRepository scheduleRuleRepository) {
        this.scheduleRuleRepository = scheduleRuleRepository;
    }

    @PostMapping
    public ResponseEntity<ScheduleRule> addScheduleRule(@Valid @RequestBody ScheduleRuleRequestDTO request) {
        ScheduleRule rule = new ScheduleRule();
        rule.setEmail(request.getEmail());
        rule.setMeetingId(request.getMeetingId());
        rule.setBusyStart(request.getBusyStart());
        rule.setBusyEnd(request.getBusyEnd());

        ScheduleRule saved = scheduleRuleRepository.save(rule);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ScheduleRule>> getScheduleRules(
            @RequestParam String email,
            @RequestParam String meetingId) {
        List<ScheduleRule> rules = scheduleRuleRepository.findByEmailAndMeetingId(email, meetingId);
        return ResponseEntity.ok(rules);
    }
}
