package com.recordlogs.model;

import java.util.Date;

public class Event extends Record{

    private final String activity;

    public Event(String activity, Date timestamp, String caseID) {
        this.activity = activity;
        this.timestamp = timestamp;
        this.caseID = caseID;
        this.type = "event";
    }

    public String getActivity() {
        return activity;
    }
}