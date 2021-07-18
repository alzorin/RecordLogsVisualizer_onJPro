package com.recordlogs.model;

public class ChosenColumnNames {
    private final String caseColumn;
    private final String timestampColumn;
    private final String typeColumn;
    private final String activityColumn;

    public ChosenColumnNames(String caseColumn, String activityColumn, String timestampColumn, String typeColumn) {
        this.caseColumn = caseColumn;
        this.activityColumn = activityColumn;
        this.timestampColumn = timestampColumn;
        this.typeColumn = typeColumn;

    }

    public String getCaseColumn() {
        return caseColumn;
    }

    public String getTypeColumn() {
        return typeColumn;
    }

    public String getTimestampColumn() {
        return timestampColumn;
    }

    public String getActivityColumn() {
        return activityColumn;
    }
}
