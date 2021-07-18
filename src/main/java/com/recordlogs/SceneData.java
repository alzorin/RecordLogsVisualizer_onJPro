package com.recordlogs;

import com.jpro.webapi.WebAPI;
import com.recordlogs.model.SourceData;
import org.apache.commons.csv.CSVParser;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class SceneData {

    public static CSVParser csvParser;
    public static SourceData sourceData;
    public static Set<String> selectedMeasurements = new LinkedHashSet<>();
    public static String selectedCaseColumn;
    public static String selectedTimestampColumn;
    public static String selectedActivityColumn;
    public static String selectedTypeColumn;
    public static long loadTime;
    public static WebAPI webAPI;

}
