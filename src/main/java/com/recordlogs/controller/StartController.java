package com.recordlogs.controller;

import com.recordlogs.SceneData;
import com.recordlogs.csv.InputFileParser;
import com.recordlogs.model.ChosenColumnNames;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.csv.CSVParser;
import org.controlsfx.control.CheckComboBox;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;

import com.jpro.webapi.WebAPI;

public class StartController {
    @FXML
    private Button BrowseFile;

    @FXML
    private MenuBar menuBar;

    @FXML
    private Button NextScreen;

    @FXML
    private CheckComboBox<String> measurementsCheckComboBox;

    @FXML
    private ComboBox<String> caseComboBox;
    @FXML
    private ComboBox<String> activityComboBox;
    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private ComboBox<String> timestampComboBox;

    @FXML
    private AnchorPane StartAnchorPane;

    @FXML
    private TextField DateFormatInput;

    @FXML
    private CheckBox DateFormatCheckbox;

    @FXML
    private TextField CSVDelimiterInput;

    @FXML
    private CheckBox CSVDelimiterCheckbox;


    private String dateFormat = "yyyy-MM-dd HH:mm:ssXXX"; //2018-01-18 09:00:00+00:00

    private char csvDelimiter = ';';

    private WebAPI.FileUploader fileHandler = null;

    @FXML
    void closeButtonPushed(ActionEvent event) {
        Stage window = (Stage) menuBar.getScene().getWindow();
        window.close();
    }

    @FXML
    void NextScreenButtonPushed(ActionEvent event) throws IOException { //After a click on the "next" button we go to the "load" screen
        SceneData.loadTime = System.currentTimeMillis();
        StartAnchorPane.getChildren().removeAll(StartAnchorPane.getChildren());
        StartAnchorPane.getChildren().add(new ProgressIndicator());
        StartAnchorPane.getChildren().add(new Text("Error is occurred if here are no charts displayed. Please ask the programmer or consider the documentation."));
        InputFileParser parser = new InputFileParser((DateFormatInput.getText().equals(""))? dateFormat: DateFormatInput.getText(),(CSVDelimiterInput.getText().equals(""))? csvDelimiter: CSVDelimiterInput.getText().charAt(0));
        SceneData.sourceData = parser.parseRecords(SceneData.csvParser, new ChosenColumnNames(SceneData.selectedCaseColumn, SceneData.selectedActivityColumn, SceneData.selectedTimestampColumn, SceneData.selectedTypeColumn));
        System.out.println("============ SOURCE DATA LOADED =========");
        System.out.println("Source data loading time:" +((double) (System.currentTimeMillis() - SceneData.loadTime) / (1000))+"seconds");
        nextWindow();
    }

    @FXML
    void openStartHelp(ActionEvent event) throws IOException {
        try{
        Parent loadParent = FXMLLoader.load(getClass().getResource("/StartHelp.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Documentation");
        stage.setScene(new Scene(loadParent));
        stage.show();}
    catch(Exception e)
    {
        System.out.println("Can not load the second window");
    }

}
    @FXML
    void chooseFile(ActionEvent event) {
//        FileChooser fileChooser = new FileChooser();
//        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
//        fileChooser.setTitle("Open CSV file");
//        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV file", "*.csv"));
//        File file = fileChooser.showOpenDialog(window); // Open file selection dialog and save selected .csv file to file
//        Object[] headers = getHeaders(file); // We specify an array headers to save objects from all types
//        clearComboBoxes(); // If we select a new file, we have first to clear the previous column selections
//        for (Object header : headers) { //We go through each object header in headers
//            String strHeader = (String) header; //We extract a string from object header
//            fillComboBoxes(strHeader); //We extract strings objects in headers and fill it to the dropdown menus
//        }
    }

    private void fillComboBoxes(String strHeader) {
        caseComboBox.getItems().add(strHeader);
        activityComboBox.getItems().add(strHeader);
        timestampComboBox.getItems().add(strHeader);
        measurementsCheckComboBox.getItems().add(strHeader);
        typeComboBox.getItems().add(strHeader);
    }

    private void clearComboBoxes() {
        caseComboBox.getItems().clear();
        activityComboBox.getItems().clear();
        timestampComboBox.getItems().clear();
        measurementsCheckComboBox.getItems().clear();
        typeComboBox.getItems().clear();
    }

    private Object[] getHeaders(File file) {
        InputFileParser parser = new InputFileParser(dateFormat, (CSVDelimiterInput.getText().equals(""))? csvDelimiter: CSVDelimiterInput.getText().charAt(0)); // dateFormat is not relevant for the extraction of headers
        CSVParser csvRecords = parser.readFile(file); //Parse records from the CSV file into memory
        SceneData.csvParser = csvRecords;  // Save parsed records to the SceneData
        Object[] headers = csvRecords.getHeaderNames().stream().filter(Objects::nonNull).filter(header -> !header.isEmpty()).toArray(); //Filter out the non-empty headers
        return headers;
    }

    private void nextWindow() throws IOException{
        Parent loadParent = FXMLLoader.load(getClass().getResource("/Charts.fxml"));
        Scene load = new Scene(loadParent);
        //This line gets the stage information
        Stage window = (Stage) StartAnchorPane.getScene().getWindow();
        window.setScene(load);
        window.show();
    }

    @FXML
    public void initialize() {
        SceneData.loadTime = 0;
        SimpleBooleanProperty measurementsNotSelected = new SimpleBooleanProperty(true); //Observable boolean value for monitoring if at least one measurement is selected
        NextScreen.disableProperty().bind(
                caseComboBox.valueProperty().isNull()
                .or(activityComboBox.valueProperty().isNull())
                .or(caseComboBox.valueProperty().isNull())
                .or(timestampComboBox.valueProperty().isNull())
                .or(typeComboBox.valueProperty().isNull())
                .or(measurementsNotSelected)
        );
        caseComboBox.setOnAction(event -> SceneData.selectedCaseColumn = caseComboBox.getValue()); //Save selected case column in SceneData
        activityComboBox.setOnAction(event -> SceneData.selectedActivityColumn = activityComboBox.getValue()); //Save selected activity column in SceneData
        timestampComboBox.setOnAction(event -> SceneData.selectedTimestampColumn = timestampComboBox.getValue()); //Save selected timestamp column in SceneData
        typeComboBox.setOnAction(event -> SceneData.selectedTypeColumn = typeComboBox.getValue()); //Save selected type column in SceneData
        measurementsCheckComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) listChangeListener ->
        {SceneData.selectedMeasurements = new LinkedHashSet<>(measurementsCheckComboBox.getCheckModel().getCheckedItems());
        if (SceneData.selectedMeasurements.isEmpty())measurementsNotSelected.set(true); else measurementsNotSelected.set(false); }); // Save selected measurements as a Hashset in SceneData, gray out next button if no measurement is selected
        DateFormatInput.disableProperty().bind(DateFormatCheckbox.selectedProperty().not()); //Activate textfield if checkbox is selected
        CSVDelimiterInput.disableProperty().bind(CSVDelimiterCheckbox.selectedProperty().not());
        DateFormatCheckbox.selectedProperty().addListener( new ChangeListener<Boolean>() //Clear textfield if checkbox is not selected
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean wasSelected, Boolean isNowSelected )
            {
                if (isNowSelected)
                {
                }
                else
                {
                    DateFormatInput.clear();
                }
            }
        } );
        CSVDelimiterCheckbox.selectedProperty().addListener( new ChangeListener<Boolean>() //Clear textfield if checkbox is not selected
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean wasSelected, Boolean isNowSelected )
            {
                if (isNowSelected)
                {
                }
                else
                {
                    CSVDelimiterInput.clear();
                }
            }
        } );
        fileHandler = SceneData.webAPI.makeFileUploadNode(BrowseFile);
        fileHandler.setSelectFileOnClick(true);
        fileHandler.setOnFileSelected((file) -> {
            fileHandler.uploadFile();
        });
        fileHandler.uploadedFileProperty().addListener((p, oldV, newV) -> {
            processFile(newV);
        });
    }

    private void processFile(File file) {
        Object[] headers = getHeaders(file); // We specify an array headers to save objects from all types
        clearComboBoxes(); // If we select a new file, we have first to clear the previous column selections
        for (Object header : headers) { //We go through each object header in headers
            String strHeader = (String) header; //We extract a string from object header
            fillComboBoxes(strHeader); //We extract strings objects in headers and fill it to the dropdown menus
        }
    }
}