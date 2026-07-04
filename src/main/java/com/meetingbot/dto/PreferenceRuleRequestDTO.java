package com.meetingbot.dto;

import com.meetingbot.model.PreferenceRule.PreferenceType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PreferenceRuleRequestDTO {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Meeting ID is required")
    private String meetingId;

    @NotNull(message = "Preference type is required")
    private PreferenceType preferenceType;

    @NotBlank(message = "Value is required")
    private String value;

    @NotNull(message = "Weight is required")
    private Integer weight;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMeetingId() { return meetingId; }
    public void setMeetingId(String meetingId) { this.meetingId = meetingId; }

    public PreferenceType getPreferenceType() { return preferenceType; }
    public void setPreferenceType(PreferenceType preferenceType) { this.preferenceType = preferenceType; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public Integer getWeight() { return weight; }
    public void setWeight(Integer weight) { this.weight = weight; }
}
