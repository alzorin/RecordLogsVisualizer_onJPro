package com.recordlogs.csv;

import com.recordlogs.model.ChosenColumnNames;
import com.recordlogs.model.Event;
import com.recordlogs.model.Measurement;
import com.recordlogs.model.SourceData;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class InputFileParser {
    private static final Logger log = LoggerFactory.getLogger(InputFileParser.class);

    public final SimpleDateFormat FORMAT;

    public final char CSV_DELIMITER ;

    public InputFileParser(String dateFormat, char csvDelimiter)
    {
        FORMAT = new SimpleDateFormat(dateFormat);
        CSV_DELIMITER = csvDelimiter;
    }

    public CSVParser readFile(File csvFile) {
        log.debug("parsing file {}", csvFile);
        CSVParser records = null;
        try {
            Reader input = new FileReader(csvFile);
            records = CSVFormat.newFormat(CSV_DELIMITER)  //We define the CSV_Delimiter in the constructor
                    .withFirstRecordAsHeader()
                    .parse(input);
        } catch (IOException e) {
            log.error("Error when reading file {}", csvFile, e);
            throw new IllegalStateException("Can't read input file " + csvFile.getName());
        }
        return records;
    }

    public SourceData parseRecords(CSVParser records, ChosenColumnNames columnNames) {
        List<Measurement> measurements = new ArrayList<>();
        List<Event> events = new ArrayList<>();
        Set<String> activityTypes = new HashSet<>();
        Set<String> caseIDs = new LinkedHashSet<>();
        List<String> measurementValueNames = new ArrayList<>(
        );
        records.forEach(record -> { //We go through each record in the record log
            String caseID = getCaseID(record, columnNames.getCaseColumn());
            caseIDs.add(caseID); // We store all possible caseIDs to the set caseIDs
            if (isEvent(record, columnNames)) {
                String activity = record.get(columnNames.getActivityColumn());
                Date timestamp = parseTimestamp(record, columnNames.getTimestampColumn());
                events.add(new Event(activity, timestamp, caseID)); // We store each event object to the list of events
                activityTypes.add(activity);
            } else {
                if(measurementValueNames.isEmpty()) {
                    parseMeasurementValueNames(measurementValueNames, record, columnNames.getCaseColumn()); // We store all possible measurement variables to measurementVariableNames
                }
                Measurement measurement = new Measurement(record, parseTimestamp(record, columnNames.getTimestampColumn()), caseID);
                measurements.add(measurement); // We store each measurement object to the list of the measurements
            }
        });
        ArrayList<String> sortedCaseIDList = new ArrayList<>(caseIDs); // Create a list containing sorted caseIDs by a numeric value
        sortedCaseIDList.sort(Comparator.comparing(this::safeParseInt)); // Sort cases by a numeric value
        log.info("Parsed {} measurements, {} events, {} types of an activity, {} measurement value names, {} cases",
                measurements.size(),
                events.size(),
                activityTypes.size(),
                measurementValueNames.size(),
                caseIDs.size());
        return new SourceData(measurements, events, activityTypes, measurementValueNames, sortedCaseIDList);
    }

    private String getCaseID(CSVRecord record, String caseColumnName) {
        try {
            return record.get(caseColumnName); //returns a CaseID form the current record
        } catch (Exception e) {
            log.error("There is no case id for record {}", record);
            throw new IllegalStateException("no case id for record:" + record);
        }
    }


    private int safeParseInt(String str) {    // If we have non-numeric case IDs, then the program will not be interrupted. This case IDs will not be sorted.
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void parseMeasurementValueNames(List<String> measurementValueNames, CSVRecord record, String caseColumnName) {
        record.toMap()
                .forEach((columnName, value) -> {
                    if(isNumeric(value) && !columnName.equalsIgnoreCase(caseColumnName)) {
                        measurementValueNames.add(columnName);
                    }
                });
    }

    private Date parseTimestamp(CSVRecord record, String timestampColumn) {
        String timestamp = record.get(timestampColumn);
        try {
            return FORMAT.parse(timestamp);
        } catch (ParseException e) {
            log.error("Error when trying to parse timestamp", e);
            throw new IllegalStateException("incorrect input date " + timestamp);
        }
    }

    private boolean isEvent(CSVRecord record,ChosenColumnNames columnNames) {
        return record.get(columnNames.getTypeColumn()).equalsIgnoreCase("Event");
    }

    private boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        try {
            Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
