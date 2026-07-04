package com.meetingbot.dto;

import java.time.LocalDateTime;

public class SlotProposalResponseDTO {
    private LocalDateTime proposedStart;
    private LocalDateTime proposedEnd;
    private String justificationString;

    public SlotProposalResponseDTO(LocalDateTime proposedStart, LocalDateTime proposedEnd, String justificationString) {
        this.proposedStart = proposedStart;
        this.proposedEnd = proposedEnd;
        this.justificationString = justificationString;
    }

    public LocalDateTime getProposedStart() { return proposedStart; }
    public void setProposedStart(LocalDateTime proposedStart) { this.proposedStart = proposedStart; }

    public LocalDateTime getProposedEnd() { return proposedEnd; }
    public void setProposedEnd(LocalDateTime proposedEnd) { this.proposedEnd = proposedEnd; }

    public String getJustificationString() { return justificationString; }
    public void setJustificationString(String justificationString) { this.justificationString = justificationString; }
}
