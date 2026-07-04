package com.meetingbot.exception;

public class NoValidSlotFoundException extends RuntimeException {
    public NoValidSlotFoundException(String message) {
        super(message);
    }
}
