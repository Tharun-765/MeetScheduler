package com.meetingbot.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class ScheduleRuleRequestDTO {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Meeting ID is required")
    private String meetingId;

    @NotNull(message = "Busy start time is required")
    private LocalDateTime busyStart;

    @NotNull(message = "Busy end time is required")
    private LocalDateTime busyEnd;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMeetingId() { return meetingId; }
    public void setMeetingId(String meetingId) { this.meetingId = meetingId; }

    public LocalDateTime getBusyStart() { return busyStart; }
    public void setBusyStart(LocalDateTime busyStart) { this.busyStart = busyStart; }

    public LocalDateTime getBusyEnd() { return busyEnd; }
    public void setBusyEnd(LocalDateTime busyEnd) { this.busyEnd = busyEnd; }
}
