package com.recordlogs;

import com.jpro.webapi.JProApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends JProApplication {

    @Override
    public void start(Stage primaryStage) throws Exception{
        SceneData.webAPI = getWebAPI();
        Parent root = FXMLLoader.load(getClass().getResource("/Start.fxml"));
        primaryStage.setTitle("Record logs visualisation");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}