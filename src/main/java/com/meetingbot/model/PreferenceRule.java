package com.meetingbot.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "preference_rules")
public class PreferenceRule {

    @Id
    private String id;
    
    @Field("email")
    private String email;
    
    @Field("meeting_id")
    private String meetingId;
    
    @Field("preference_type")
    private PreferenceType preferenceType;
    
    @Field("value")
    private String value;
    
    @Field("weight")
    private int weight;

    public enum PreferenceType {
        PREFERRED_DAY, PREFERRED_TIME_RANGE, AVOID_BEFORE, AVOID_AFTER
    }

    public PreferenceRule() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMeetingId() { return meetingId; }
    public void setMeetingId(String meetingId) { this.meetingId = meetingId; }

    public PreferenceType getPreferenceType() { return preferenceType; }
    public void setPreferenceType(PreferenceType preferenceType) { this.preferenceType = preferenceType; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }
}
