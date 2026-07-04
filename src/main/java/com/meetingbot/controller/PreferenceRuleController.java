package com.meetingbot.controller;

import com.meetingbot.dto.PreferenceRuleRequestDTO;
import com.meetingbot.model.PreferenceRule;
import com.meetingbot.repository.PreferenceRuleRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/preference-rules")
public class PreferenceRuleController {

    private final PreferenceRuleRepository preferenceRuleRepository;

    @Autowired
    public PreferenceRuleController(PreferenceRuleRepository preferenceRuleRepository) {
        this.preferenceRuleRepository = preferenceRuleRepository;
    }

    @PostMapping
    public ResponseEntity<PreferenceRule> addPreferenceRule(@Valid @RequestBody PreferenceRuleRequestDTO request) {
        PreferenceRule rule = new PreferenceRule();
        rule.setEmail(request.getEmail());
        rule.setMeetingId(request.getMeetingId());
        rule.setPreferenceType(request.getPreferenceType());
        rule.setValue(request.getValue());
        rule.setWeight(request.getWeight());

        PreferenceRule saved = preferenceRuleRepository.save(rule);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PreferenceRule>> getPreferenceRules(
            @RequestParam String email,
            @RequestParam String meetingId) {
        List<PreferenceRule> rules = preferenceRuleRepository.findByEmailAndMeetingId(email, meetingId);
        return ResponseEntity.ok(rules);
    }
}
