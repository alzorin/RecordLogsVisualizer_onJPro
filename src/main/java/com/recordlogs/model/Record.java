package com.recordlogs.model;

import java.util.Date;

public abstract class Record {
    protected Date timestamp;
    protected String caseID;
    protected String type;

    public Date getTimestamp() {
        return timestamp;
    }

    public String getCaseID() {
        return caseID;
    }

    public String getType() {
        return type;
    }
}
