package com.meetingbot.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "meetings")
public class Meeting {

    @Id
    private String id;
    
    @Field("title")
    private String title;
    
    @Field("duration_minutes")
    private int durationMinutes;
    
    @Field("organizer_email")
    private String organizerEmail;
    
    @Field("invitee_emails")
    private List<String> inviteeEmails;
    
    @Field("status")
    private Status status;
    
    @Field("final_proposed_time")
    private LocalDateTime finalProposedTime;

    public enum Status {
        PENDING, NEGOTIATED
    }

    public Meeting() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getOrganizerEmail() { return organizerEmail; }
    public void setOrganizerEmail(String organizerEmail) { this.organizerEmail = organizerEmail; }

    public List<String> getInviteeEmails() { return inviteeEmails; }
    public void setInviteeEmails(List<String> inviteeEmails) { this.inviteeEmails = inviteeEmails; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDateTime getFinalProposedTime() { return finalProposedTime; }
    public void setFinalProposedTime(LocalDateTime finalProposedTime) { this.finalProposedTime = finalProposedTime; }
}
