package com.recordlogs.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.net.URL;

public class StartHelpController {
    @FXML
    private Button closeButton;

    @FXML
    private VBox helpVBox;

    @FXML
    void closeButtonPushed(ActionEvent event) {
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.close();
    }

    @FXML
    public void initialize() {
        WebView webView = new WebView();
        WebEngine browser = webView.getEngine();
        URL url = this.getClass().getResource("/html/documentation.html");
        browser.load(url.toString());
        helpVBox.getChildren().add(webView);
        helpVBox.setVgrow(webView, Priority.ALWAYS);
    }
}