package com.meetingbot.repository;

import com.meetingbot.model.ScheduleRule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRuleRepository extends MongoRepository<ScheduleRule, String> {
    List<ScheduleRule> findByMeetingId(String meetingId);
    List<ScheduleRule> findByEmailAndMeetingId(String email, String meetingId);
}
