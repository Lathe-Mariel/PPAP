package com.mycompany.ppap;

import java.io.File;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private PrimaryController primaryController;
    
    PenCase penCase;
    PDFWorker pdfWorker;
    
    @Override
    public void start(Stage stage) throws IOException {
        penCase = new PenCase();
        pdfWorker = new PDFWorker(penCase);
        
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("primary.fxml"));
        scene = new Scene(fxmlLoader.load(), 1200, 800);
        primaryController = (PrimaryController)fxmlLoader.getController();
        primaryController.pdfWorkerRef = pdfWorker;
        primaryController.mainStage = stage;
        primaryController.registPenButtons();

        stage.setScene(scene);
        stage.setTitle("PPAP");
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}