package com.meetingbot.repository;

import com.meetingbot.model.PreferenceRule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreferenceRuleRepository extends MongoRepository<PreferenceRule, String> {
    List<PreferenceRule> findByMeetingId(String meetingId);
    List<PreferenceRule> findByEmailAndMeetingId(String email, String meetingId);
}
