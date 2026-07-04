package com.meetingbot.controller;

import com.meetingbot.dto.MeetingCreateRequestDTO;
import com.meetingbot.exception.MeetingNotFoundException;
import com.meetingbot.model.Meeting;
import com.meetingbot.repository.MeetingRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/meetings")
public class MeetingController {

    private final MeetingRepository meetingRepository;

    @Autowired
    public MeetingController(MeetingRepository meetingRepository) {
        this.meetingRepository = meetingRepository;
    }

    @PostMapping
    public ResponseEntity<Meeting> createMeeting(@Valid @RequestBody MeetingCreateRequestDTO request) {
        Meeting meeting = new Meeting();
        meeting.setTitle(request.getTitle());
        meeting.setDurationMinutes(request.getDurationMinutes());
        meeting.setOrganizerEmail(request.getOrganizerEmail());
        meeting.setInviteeEmails(request.getInviteeEmails());
        meeting.setStatus(Meeting.Status.PENDING);

        Meeting saved = meetingRepository.save(meeting);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Meeting> getMeetingById(@PathVariable String id) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new MeetingNotFoundException("Meeting not found with id: " + id));
        return ResponseEntity.ok(meeting);
    }

    @GetMapping
    public ResponseEntity<List<Meeting>> getMeetingsByEmail(@RequestParam String email) {
        List<Meeting> allMeetings = meetingRepository.findAll();
        List<Meeting> userMeetings = allMeetings.stream()
                .filter(m -> email.equalsIgnoreCase(m.getOrganizerEmail()) ||
                             (m.getInviteeEmails() != null && m.getInviteeEmails().contains(email)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(userMeetings);
    }
}
