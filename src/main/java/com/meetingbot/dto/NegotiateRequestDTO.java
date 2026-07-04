package com.meetingbot.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class NegotiateRequestDTO {
    @NotNull(message = "Search start time is required")
    private LocalDateTime searchStart;

    @NotNull(message = "Search end time is required")
    private LocalDateTime searchEnd;

    public LocalDateTime getSearchStart() { return searchStart; }
    public void setSearchStart(LocalDateTime searchStart) { this.searchStart = searchStart; }

    public LocalDateTime getSearchEnd() { return searchEnd; }
    public void setSearchEnd(LocalDateTime searchEnd) { this.searchEnd = searchEnd; }
}
