package com.recordlogs.charts;

import com.recordlogs.model.Event;
import com.recordlogs.model.SourceData;
import de.gsi.chart.XYChart;
import de.gsi.chart.axes.AxisLabelOverlapPolicy;
import de.gsi.chart.axes.spi.CategoryAxis;
import de.gsi.chart.axes.spi.DefaultNumericAxis;
import de.gsi.chart.marker.DefaultMarker;
import de.gsi.chart.plugins.DataPointTooltip;
import de.gsi.chart.plugins.Panner;
import de.gsi.chart.plugins.ParameterMeasurements;
import de.gsi.chart.plugins.Zoomer;
import de.gsi.chart.renderer.ErrorStyle;
import de.gsi.chart.renderer.LineStyle;
import de.gsi.chart.renderer.spi.ErrorDataSetRenderer;
import de.gsi.dataset.spi.DefaultDataSet;
import javafx.collections.FXCollections;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DottedChart {

    public static XYChart getDottedChart(SourceData sourceData, List<String> activeCases,List<String> activeDataSets) {
        XYChart chart = getDefaultChart("", activeCases);

        final ErrorDataSetRenderer errorRenderer2 = getDataset(sourceData, activeCases, activeDataSets);

        chart.getRenderers().setAll(errorRenderer2);
        return chart;
    }

    public static ErrorDataSetRenderer getDataset(SourceData sourceData, List<String> activeCases, List<String> activeDataSets) {
        Map<String, DefaultDataSet> activityTypeTimeSeries = new LinkedHashMap<>(); // We create a map containing time series data for each type of an activity
        sourceData.getActivityTypes()
                .forEach(type -> activityTypeTimeSeries.put(type, new DefaultDataSet(type))); // We have a DataSet for each type of an activity
        for (Event event : sourceData.getEvents()) {
            if (activeCases.contains(event.getCaseID())) {
                int caseOrdinate = parseCaseOrdinate(event, activeCases); //event ordinate is the case number
                activityTypeTimeSeries.get(event.getActivity()) // We add data points to each DataSet. X: is the timestamp and Y: is the case number
                        .add(getSeconds(event), caseOrdinate, getPointLabel(event));
            }
        }

        final ErrorDataSetRenderer errorRenderer2 = new ErrorDataSetRenderer();
        errorRenderer2.setMarkerSize(7);
        errorRenderer2.setPolyLineStyle(LineStyle.NONE);
        errorRenderer2.setErrorType(ErrorStyle.NONE);
        errorRenderer2.setDrawMarker(true);
        errorRenderer2.setAssumeSortedData(false); // !! important since DS is likely unsorted
        // or via global default, this also allows to set custom marker implementing the 'Marker' interface
        errorRenderer2.setMarker(DefaultMarker.DIAMOND);

        activityTypeTimeSeries.values().forEach(datapoint -> datapoint.setStyle("markerType=circle;"));
        for (String activity : sourceData.getActivityTypes()) {
            if (!(activeDataSets.contains(activity))) {
                activityTypeTimeSeries.remove(activity);
            }
        }
        errorRenderer2.getDatasets().addAll(activityTypeTimeSeries.values());
        return errorRenderer2;
    }

     private static XYChart getDefaultChart(final String title, List<String> activeCases) {
        final DefaultNumericAxis xAxis = new DefaultNumericAxis("time");
        xAxis.setOverlapPolicy(AxisLabelOverlapPolicy.SKIP_ALT);
        final CategoryAxis yAxis = new CategoryAxis("case", FXCollections.observableArrayList(activeCases));
         //yAxis.setOverlapPolicy(AxisLabelOverlapPolicy.SHIFT_ALT);
        //yAxis.setSide(Side.RIGHT);
        //yAxis.setAutoRangePadding(0.5);
        yAxis.setCategories(activeCases);
        final XYChart chart = new XYChart(xAxis, yAxis);
        chart.legendVisibleProperty().set(true);
        chart.getPlugins().add(new Zoomer());
        chart.getPlugins().add(new Panner());
        //chart.setLegendSide(Side.RIGHT);
        //chart.getPlugins().add(new EditAxis());
        chart.getPlugins().add(new DataPointTooltip());
        //chart.getPlugins().add(new ParameterMeasurements());
        // set them false to make the plot faster
        chart.setAnimated(true);

        xAxis.setAutoRangeRounding(false);
        xAxis.setTimeAxis(true);

        yAxis.setAutoRangeRounding(true);
        chart.setLegend(new CustomLegend());

        return chart;
    }

    private static int parseCaseOrdinate(Event event, List<String> activeCases) {
        for (int i = 0; i < activeCases.size(); i++) {
            if (activeCases.get(i).equalsIgnoreCase(event.getCaseID())) {
                return i;
            }
        }
        return 0;
    }
    private static String getPointLabel(Event event) {
        SimpleDateFormat utcDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        utcDateFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        String pointLabel = event.getCaseID() + " " + event.getActivity() + " " + utcDateFormat.format(event.getTimestamp());
        return pointLabel;
    }
    private static long getSeconds(Event event) {
        return event.getTimestamp().getTime() / 1000;
    }
}