package com.recordlogs.model;

import java.util.List;
import java.util.Set;

public class SourceData {
    private final List<Measurement> measurements;
    private final List<Event> events;
    private final Set<String> activityTypes;
    private final List<String> measurementValueNames;
    private List<String> caseIDs;

    public SourceData(List<Measurement> measurements, List<Event> events, Set<String> activityTypes, List<String> measurementValueNames, List<String> caseIDs) {
        this.measurements = measurements;
        this.events = events;
        this.activityTypes = activityTypes;
        this.measurementValueNames = measurementValueNames;
        this.caseIDs = caseIDs;
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public List<Event> getEvents() {
        return events;
    }

    public Set<String> getActivityTypes() {
        return activityTypes;
    }

    public List<String> getMeasurementValueNames() {
        return measurementValueNames;
    }

    public List<String> getCaseIDs() {
        return caseIDs;
    }
}
