package com.recordlogs.charts;

import com.recordlogs.model.Measurement;
import com.recordlogs.model.SourceData;
import de.gsi.chart.XYChart;
import de.gsi.chart.axes.AxisLabelOverlapPolicy;
import de.gsi.chart.axes.spi.DefaultNumericAxis;
import de.gsi.chart.plugins.DataPointTooltip;
import de.gsi.chart.plugins.Panner;
import de.gsi.chart.plugins.ParameterMeasurements;
import de.gsi.chart.plugins.Zoomer;
import de.gsi.chart.renderer.ErrorStyle;
import de.gsi.chart.renderer.LineStyle;
import de.gsi.chart.renderer.spi.ErrorDataSetRenderer;
import de.gsi.chart.ui.geometry.Side;
import de.gsi.dataset.spi.DoubleDataSet;
import javafx.application.Platform;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class TimeSeriesChart {

    public static XYChart getTimeSeriesChart(List<String> activeMeasurements) {
        final DefaultNumericAxis xAxis = new DefaultNumericAxis("time");
        xAxis.setOverlapPolicy(AxisLabelOverlapPolicy.SKIP_ALT);
        final DefaultNumericAxis yAxis = new DefaultNumericAxis("measurement");

        XYChart chart = new XYChart(xAxis, yAxis);
        chart.legendVisibleProperty().set(true);
        //chart.setLegendSide(Side.RIGHT);
        chart.getPlugins().add(new Zoomer());
        //chart.getPlugins().add(new EditAxis());
        chart.getPlugins().add(new DataPointTooltip());
        //chart.getPlugins().add(new ParameterMeasurements());
        chart.getPlugins().add(new Panner());
        chart.setAnimated(false); // set them false to make the plot faster

        xAxis.setAutoRangeRounding(false);
        xAxis.setTimeAxis(true);
        yAxis.setAutoRangeRounding(true);
        chart.setLegend(new CustomLegend());
        return chart;
    }
    public static DoubleDataSet createDatasetForCaseAndMeasurement(String activeCase, String activeMeasurement) {
        final DoubleDataSet dataset = new DoubleDataSet("case " + activeCase + " " + activeMeasurement);
        dataset.setStyle("strokeColor=" + getRandomColor() + "; strokeWidth=2");
        dataset.autoNotification().set(false);
        dataset.clearData();
        dataset.autoNotification().set(true);
        Platform.runLater(() -> dataset.fireInvalidated(null));
        return dataset;
    }
    public static ErrorDataSetRenderer getDatasetsPerCaseAndMeasurement(SourceData sourceData, List<String> activeCasesList, List<String> activeMeasurements) {
        Map<String, DoubleDataSet> datasetsPerCaseAndMeasurement = new HashMap<>();
        Set<String> activeCases = new HashSet<>(activeCasesList);
        activeCases.forEach(activeCase ->
                activeMeasurements.forEach(activeMeasurement ->
                        datasetsPerCaseAndMeasurement.put(getDatasetName(activeCase, activeMeasurement), createDatasetForCaseAndMeasurement(activeCase, activeMeasurement))));
        sourceData.getMeasurements()
                .stream()
                .filter(measurement -> activeCases.contains(measurement.getCaseID()))
                .forEach(measurement ->
                        activeMeasurements.forEach(activeMeasurement ->
                                datasetsPerCaseAndMeasurement.get(getDatasetName(measurement.getCaseID(), activeMeasurement)).add(getSeconds(measurement), getValue(measurement, activeMeasurement), getPointLabel(measurement, activeMeasurement)))
                );
        final ErrorDataSetRenderer errorRenderer = new ErrorDataSetRenderer();
        errorRenderer.errorStyleProperty().set(ErrorStyle.NONE);
        errorRenderer.getDatasets().addAll(datasetsPerCaseAndMeasurement.values());
        return errorRenderer;
    }

    private static String getDatasetName(String activeCase, String activeMeasurement) { // Data set name is the label for the data set that is shown on the legend
        return activeCase + "_" + activeMeasurement;
    }

    public static String getRandomColor() {
        final Random random = new Random();
        final String[] letters = "0123456789ABCDEF".split("");
        String color = "#";
        for (int i = 0; i < 6; i++) {
            color += letters[Math.round(random.nextFloat() * 15)];
        }
        return color;
    }
    private static long getSeconds(Measurement measurement) {
        return measurement.getTimestamp().getTime() / 1000;
    }

    private static double getValue(Measurement measurement, String selectedMeasurement) {
        try {
            return Double.parseDouble(measurement.getMeasurementValue().get(selectedMeasurement));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    private static String getPointLabel(Measurement measurement, String activeMeasurement) {
        SimpleDateFormat utcDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        utcDateFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        String pointLabel = measurement.getCaseID() + " " + activeMeasurement + " " + utcDateFormat.format(measurement.getTimestamp());
        return pointLabel;
    }
}
