package com.meetingbot.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "schedule_rules")
public class ScheduleRule {

    @Id
    private String id;
    
    @Field("email")
    private String email;
    
    @Field("meeting_id")
    private String meetingId;
    
    @Field("busy_start")
    private LocalDateTime busyStart;
    
    @Field("busy_end")
    private LocalDateTime busyEnd;

    public ScheduleRule() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMeetingId() { return meetingId; }
    public void setMeetingId(String meetingId) { this.meetingId = meetingId; }

    public LocalDateTime getBusyStart() { return busyStart; }
    public void setBusyStart(LocalDateTime busyStart) { this.busyStart = busyStart; }

    public LocalDateTime getBusyEnd() { return busyEnd; }
    public void setBusyEnd(LocalDateTime busyEnd) { this.busyEnd = busyEnd; }
}
