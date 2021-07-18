package com.recordlogs.model;

import javafx.fxml.FXML;
import org.apache.commons.csv.CSVRecord;

import java.util.Date;

public class Measurement extends Record{

    private final CSVRecord measurementValue;

    public Measurement(CSVRecord measurementValue, Date timestamp, String caseID) {
        this.measurementValue = measurementValue;
        this.timestamp = timestamp;
        this.caseID = caseID;
        this.type= "measurement";
    }

    public CSVRecord getMeasurementValue() {
        return measurementValue;
    }
}
