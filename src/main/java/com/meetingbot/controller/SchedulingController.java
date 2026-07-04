package com.meetingbot.controller;

import com.meetingbot.dto.NegotiateRequestDTO;
import com.meetingbot.dto.SlotProposalResponseDTO;
import com.meetingbot.exception.MeetingNotFoundException;
import com.meetingbot.exception.NoValidSlotFoundException;
import com.meetingbot.model.Meeting;
import com.meetingbot.repository.MeetingRepository;
import com.meetingbot.service.SchedulingEngineService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/meetings")
public class SchedulingController {

    private final SchedulingEngineService schedulingEngineService;
    private final MeetingRepository meetingRepository;

    @Autowired
    public SchedulingController(SchedulingEngineService schedulingEngineService, MeetingRepository meetingRepository) {
        this.schedulingEngineService = schedulingEngineService;
        this.meetingRepository = meetingRepository;
    }

    @PostMapping("/{meetingId}/negotiate")
    public ResponseEntity<SlotProposalResponseDTO> negotiateMeeting(
            @PathVariable String meetingId,
            @Valid @RequestBody NegotiateRequestDTO request) {

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new MeetingNotFoundException("Meeting not found with id: " + meetingId));

        SchedulingEngineService.SchedulingResult result = schedulingEngineService.negotiateMeeting(
                meetingId, request.getSearchStart(), request.getSearchEnd());

        if (result.getFinalProposedTime() == null) {
            throw new NoValidSlotFoundException(result.getJustificationString());
        }

        SlotProposalResponseDTO response = new SlotProposalResponseDTO(
                result.getFinalProposedTime(),
                result.getFinalProposedTime().plusMinutes(meeting.getDurationMinutes()),
                result.getJustificationString()
        );

        return ResponseEntity.ok(response);
    }
}
