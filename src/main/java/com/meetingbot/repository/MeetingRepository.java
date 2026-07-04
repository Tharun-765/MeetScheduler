package com.meetingbot.repository;

import com.meetingbot.model.Meeting;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingRepository extends MongoRepository<Meeting, String> {
}
