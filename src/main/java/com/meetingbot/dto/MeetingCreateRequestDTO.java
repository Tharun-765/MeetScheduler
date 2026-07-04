package com.meetingbot.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class MeetingCreateRequestDTO {
    @NotBlank(message = "Title is required")
    private String title;

    @Min(value = 15, message = "Duration must be at least 15 minutes")
    private int durationMinutes;

    @NotBlank(message = "Organizer email is required")
    @Email(message = "Invalid organizer email")
    private String organizerEmail;

    @NotEmpty(message = "At least one invitee email is required")
    private List<String> inviteeEmails;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getOrganizerEmail() { return organizerEmail; }
    public void setOrganizerEmail(String organizerEmail) { this.organizerEmail = organizerEmail; }

    public List<String> getInviteeEmails() { return inviteeEmails; }
    public void setInviteeEmails(List<String> inviteeEmails) { this.inviteeEmails = inviteeEmails; }
}
