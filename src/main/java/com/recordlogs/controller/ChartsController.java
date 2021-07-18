package com.recordlogs.controller;

import com.recordlogs.SceneData;
import com.recordlogs.charts.DottedChart;
import com.recordlogs.charts.TimeSeriesChart;
import com.recordlogs.model.SourceData;
import de.gsi.chart.XYChart;
import de.gsi.chart.axes.spi.CategoryAxis;
import de.gsi.chart.axes.spi.DefaultNumericAxis;
import de.gsi.chart.ui.geometry.Side;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;


public class ChartsController {

    @FXML
    private VBox charts;

    @FXML
    private MenuBar menuBar;

    @FXML
    private Menu measurementsMenu;

    @FXML
    private Menu casesMenu;

    @FXML
    private MenuItem selectAllCases;

    @FXML
    private MenuItem deselectAllCases;

    @FXML
    private MenuItem selectAllDatasets;

    @FXML
    private MenuItem deselectAllDatasets;

    @FXML
    private Menu datasetsMenu;

    @FXML
    private StackPane ChartsPane;

    private List<String> activeMeasurements;
    private List<String> activeCases;
    private List<String> activeDataSets;
    private XYChart dottedChart;
    private XYChart timeSeriesChart;

    private SourceData sourceData = SceneData.sourceData;
    private Set<String> selectedMeasurements = SceneData.selectedMeasurements;

    @FXML
    void closeButtonPushed(ActionEvent event) {
        Stage window = (Stage) menuBar.getScene().getWindow();
        window.close();
    }

    @FXML
    void goToStart(ActionEvent event) throws IOException {
        Parent loadParent = FXMLLoader.load(getClass().getResource("/Start.fxml"));
        Scene load = new Scene(loadParent);
        Stage window = (Stage) menuBar.getScene().getWindow();
        window.setScene(load);
        window.show();
    }

    @FXML
    void openChartHelp(ActionEvent event) throws IOException {
        try{
            Parent loadParent = FXMLLoader.load(getClass().getResource("/StartHelp.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Help");
            stage.setScene(new Scene(loadParent));
            stage.show();}
        catch(Exception e)
        {
            System.out.println("Can not load the second window");
        }

    }
    @FXML
    void SetLegendOnTheBottom(ActionEvent event) {
        dottedChart.setLegendVisible(true);
        timeSeriesChart.setLegendVisible(true);
        dottedChart.setLegendSide(Side.BOTTOM);
        timeSeriesChart.setLegendSide(Side.BOTTOM);
    }
    @FXML
    void SetLegendNotVisible(ActionEvent event) {
        dottedChart.setLegendVisible(false);
        timeSeriesChart.setLegendVisible(false);
    }

    @FXML
    void initialize() {
        activeCases = new ArrayList();
        activeDataSets = new ArrayList();
        activeMeasurements = new ArrayList<>();
        String firstSelectedMeasurement = selectedMeasurements.stream().findFirst().orElseThrow();
        fillMeasurementsMenu(selectedMeasurements, firstSelectedMeasurement);
        activeMeasurements.add(firstSelectedMeasurement);
        activeCases = fillCasesMenu(sourceData, activeCases);
        activeDataSets = fillDataSetsMenu(sourceData, activeDataSets);

        dottedChart = DottedChart.getDottedChart(sourceData, activeCases,activeDataSets);
        timeSeriesChart = TimeSeriesChart.getTimeSeriesChart(activeMeasurements);

        measurementsMenu.setOnAction(event -> measurementChecked(event, timeSeriesChart, sourceData));
        casesMenu.setOnAction(event -> caseChecked(event, timeSeriesChart, sourceData));
        datasetsMenu.setOnAction(event -> dataSetChecked(event, sourceData));

        refreshCharts(sourceData, timeSeriesChart, dottedChart);
        bindAxis(timeSeriesChart, dottedChart);

        final VBox vbox = new VBox(dottedChart, timeSeriesChart);
        vbox.setVgrow(dottedChart, Priority.ALWAYS);
        vbox.setVgrow(timeSeriesChart, Priority.ALWAYS);
        ChartsPane.getChildren().add(vbox);
        System.out.println("Total loading time:" +((double) (System.currentTimeMillis() - SceneData.loadTime) / (1000))+"seconds");
    }
    private static void bindAxis(final XYChart chartPane1, final XYChart chartPane2) {
        final DefaultNumericAxis xAxis1 = (DefaultNumericAxis) chartPane1.getXAxis();
        final DefaultNumericAxis xAxis2 = (DefaultNumericAxis) chartPane2.getXAxis();
        xAxis1.autoRangingProperty().bindBidirectional(xAxis2.autoRangingProperty());
        xAxis1.maxProperty().bindBidirectional(xAxis2.maxProperty());
        xAxis1.minProperty().bindBidirectional(xAxis2.minProperty());
    }

    private void fillMeasurementsMenu(Set<String> selectedMeasurements, String activeMeasurement) {
        selectedMeasurements.forEach(measurementValueName -> {
            CheckMenuItem item = new CheckMenuItem(measurementValueName);
            measurementsMenu.getItems().add(item);
            if (measurementValueName.equals(activeMeasurement)) {
                item.setSelected(true);
            }
        });
    }
    private List<String> fillCasesMenu(SourceData sourceData, List<String> activeCases) {
        sourceData.getCaseIDs().forEach(caseID -> {
            CheckMenuItem item = new CheckMenuItem(caseID);
            casesMenu.getItems().add(item);
            activeCases.add(caseID);
                item.setSelected(true);
        });
        return activeCases;
    }
    private List<String> fillDataSetsMenu(SourceData sourceData, List<String> activeDatasets) {

        for (String activity : sourceData.getActivityTypes())
        {
            activeDataSets.add(activity);
        }
        Collections.sort(activeDataSets);
        for (String activity : activeDatasets) {
            CheckMenuItem item = new CheckMenuItem(activity);
            datasetsMenu.getItems().add(item);
            item.setSelected(true);
        }

        return activeDatasets;
    }

    private void refreshCharts(SourceData sourceData, XYChart timeSeriesChart, XYChart dottedChart) {
        timeSeriesChart.getDatasets().clear();
        timeSeriesChart.getRenderers().setAll(TimeSeriesChart.getDatasetsPerCaseAndMeasurement(sourceData, activeCases, activeMeasurements));

        dottedChart.getRenderers().setAll(DottedChart.getDataset(sourceData, activeCases, activeDataSets));
    }

    private void measurementChecked(ActionEvent event, XYChart chart, SourceData sourceData) {
        CheckMenuItem target = (CheckMenuItem) event.getTarget();
        if (target.isSelected()) {
            activeMeasurements.add(target.getText());
        } else {
            activeMeasurements.remove(target.getText());
        }
        measurementsMenu
                .getItems()
                .forEach(item -> {
                    CheckMenuItem checkMenuItem = (CheckMenuItem) item;
                    boolean selected = target.getText().equals(checkMenuItem.getText());
                    checkMenuItem.setSelected(selected);
                    checkMenuItem.setSelected(activeMeasurements.stream().anyMatch(text -> text.equalsIgnoreCase(checkMenuItem.getText())));
                });
        refreshCharts(sourceData, chart, dottedChart);
    }

    private void caseChecked(ActionEvent event, XYChart chart, SourceData sourceData) {
        if (!(event.getTarget() instanceof CheckMenuItem))
        {
            if (event.getTarget()==selectAllCases)
            {
                activeCases.clear();
                for (String caseID : sourceData.getCaseIDs())
                {
                    activeCases.add(caseID);
                }
                casesMenu
                        .getItems()
                        .stream().filter(item -> item instanceof CheckMenuItem)
                        .forEach(item -> {
                            CheckMenuItem checkMenuItem = (CheckMenuItem) item;
                            checkMenuItem.setSelected(true);
                        });
            }
            if (event.getTarget()==deselectAllCases)
            {
                activeCases.clear();
                casesMenu
                        .getItems()
                        .stream().filter(item -> item instanceof CheckMenuItem)
                        .forEach(item -> {
                            CheckMenuItem checkMenuItem = (CheckMenuItem) item;
                            checkMenuItem.setSelected(false);
                        });
            }
        }
        else {
            CheckMenuItem target = (CheckMenuItem) event.getTarget();
            if (target.isSelected()) {
                activeCases.add(target.getText());
            } else {
                activeCases.remove(target.getText());
            }
            casesMenu
                    .getItems()
                    .stream().filter(item -> item instanceof CheckMenuItem)
                    .forEach(item -> {
                        CheckMenuItem checkMenuItem = (CheckMenuItem) item;
                        checkMenuItem.setSelected(activeCases.stream().anyMatch(text -> text.equalsIgnoreCase(checkMenuItem.getText())));
                    });
        }
        CategoryAxis yAxis = (CategoryAxis)dottedChart.getYAxis();
        yAxis.getCategories().clear();
        yAxis.setCategories(new ArrayList<>(activeCases));
        refreshCharts(sourceData, chart, dottedChart);
    }
    private void dataSetChecked(ActionEvent event, SourceData sourceData) {
        if (!(event.getTarget() instanceof CheckMenuItem))
        {
            if (event.getTarget()==selectAllDatasets)
            {
                activeDataSets.clear();
                for (String activity : sourceData.getActivityTypes())
                {
                    activeDataSets.add(activity);
                }
                datasetsMenu
                        .getItems()
                        .stream().filter(item -> item instanceof CheckMenuItem)
                        .forEach(item -> {
                            CheckMenuItem checkMenuItem = (CheckMenuItem) item;
                            checkMenuItem.setSelected(true);
                        });
            }
            if (event.getTarget()==deselectAllDatasets)
            {
                activeDataSets.clear();
                datasetsMenu
                        .getItems()
                        .stream().filter(item -> item instanceof CheckMenuItem)
                        .forEach(item -> {
                            CheckMenuItem checkMenuItem = (CheckMenuItem) item;
                            checkMenuItem.setSelected(false);
                        });
            }
        }
        else {
            CheckMenuItem target = (CheckMenuItem) event.getTarget();
            if (target.isSelected()) {
                activeDataSets.add(target.getText());
            } else {
                activeDataSets.remove(target.getText());
            }

            datasetsMenu
                    .getItems()
                    .stream().filter(item -> item instanceof CheckMenuItem)
                    .forEach(item -> {
                        CheckMenuItem checkMenuItem = (CheckMenuItem) item;
                        checkMenuItem.setSelected(activeDataSets.stream().anyMatch(text -> text.equalsIgnoreCase(checkMenuItem.getText())));
                    });
        }
        refreshCharts(sourceData, timeSeriesChart, dottedChart);
    }
}
