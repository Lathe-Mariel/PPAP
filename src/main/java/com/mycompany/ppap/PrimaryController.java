package com.mycompany.ppap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.ToolBar;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class PrimaryController {
    //@FXML
    //private Canvas mainCanvas;
    @FXML
    private Canvas drawCanvas;
    @FXML
    private ColorPicker colorPicker1;
    @FXML
    private AnchorPane shapePane;
    @FXML
    private Label lowerLabel;
    @FXML
    private StackPane stackPane;
    @FXML
    private AnchorPane baseAnchorPane;
    @FXML
    private VBox existPDFVBox;
    @FXML
    private ScrollPane scrollPane;
    
    //private Polyline polyline;
    private Path currentPath;
    private ArrayList<Path> pathList;
    private ArrayList<PDFPage> pdfPages;
    protected PDFWorker pdfWorkerRef;
    protected Stage mainStage;
    
    @FXML
    private ToolBar penToolBar;
    
    public PrimaryController(){
        super();
        pathList = new ArrayList<Path>();
    }
    
    GraphicsContext g;
    ArrayList<Button> penToolButtons;
    
    @FXML
    void initialize(){
        
    }
    
    void registPenButtons(){
        penToolButtons = new ArrayList<Button>();
        for(int i =0; i < 3; i++){
            Button b = new Button(i + "");    //size23x25
            b.setTextFill(pdfWorkerRef.penCase.getPen(i).color);
            EventHandler<MouseEvent> toolButtonClick = (event) -> this.penButtonAction(event);
            b.addEventHandler(MouseEvent.MOUSE_CLICKED, toolButtonClick);
            penToolButtons.add(b);
            penToolBar.getItems().add(b);
        }
    }
    
    @FXML
    private void mouseClick(MouseEvent e){
        System.out.println("Mouseclicke");
    }
    
    //int oldx, oldy;
    @FXML
    private void mouseDragged(MouseEvent e){
        //System.out.println("mouseDragged");
        double x = e.getX();
        double y = e.getY();
        if(x < 0 || x > drawCanvas.getWidth()-1)return;
        if(y < 0 || y > drawCanvas.getHeight()-1)return;
        g.clearRect(0, 0, drawCanvas.getWidth(), drawCanvas.getHeight());
        g.lineTo(x,y);
        g.stroke();
        //oldx = x;
        //oldy = y;
        //g.stroke();
        //g.beginPath();
        g.moveTo(x, y);
        currentPath.getElements().add(new LineTo(x, y));
        //g.stroke();
    }
    
    @FXML
    private void mousePressed(MouseEvent e){
        //System.out.println("mousePressed");
        g.beginPath();
        currentPath = new Path();
        MoveTo moveto = new MoveTo();
        double x = e.getX();
        double y = e.getY();
        moveto.setX(x);
        moveto.setY(y);
        currentPath.getElements().add(moveto);
        g.moveTo(x, y);
    }
    
    @FXML
    private void mouseReleased(MouseEvent e){
        if(g !=null){
            g.clearRect(0, 0, drawCanvas.getWidth(), drawCanvas.getHeight());
            currentPath.setStroke(g.getStroke());
            currentPath.setStrokeWidth(g.getLineWidth());
            currentPath.setOpacity(g.getGlobalAlpha());
            shapePane.getChildren().add(currentPath);
            pathList.add(currentPath);
//            ((Pane)(drawCanvas.getScene().getRoot())).getChildren().add(currentPath);
        }
    }
    
    @FXML
    private void mouseMoved(MouseEvent e){
        lowerLabel.setText("Scale: " + currentScale + "  Position: " + e.getX() + ", " + e.getY());
    }
    
    @FXML
    private void colorChanged(ActionEvent t){
        g.setStroke(colorPicker1.getValue());
    }
    
    /*
    @FXML
    private void testItem2(){
        File file = new File("3.pdf");
        lowerLabel.setText(pdfWorkerRef.writePDF(file, existPDFVBox.getChildren()));
    }*/
    
    @FXML private void fileSave(){
         File selectedFile;
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save PDF File");
            fileChooser.getExtensionFilters().addAll(
                    new ExtensionFilter("PDF", "*.pdf"),
                    new ExtensionFilter("All", "*"));
            selectedFile = fileChooser.showSaveDialog(mainStage);
            if (selectedFile != null) {    
                if(!selectedFile.exists()){
                    System.out.println("absolute pass: " + selectedFile.getAbsolutePath());
                    selectedFile = new File(selectedFile.getAbsolutePath());
                    try{
                        selectedFile.createNewFile();
                    }catch(IOException e){
                        System.out.println("Selected file couldn't create");
                        e.printStackTrace();
                    }
                }
                lowerLabel.setText(pdfWorkerRef.writePDF(selectedFile, existPDFVBox.getChildren()));
            }
    }
    
    @FXML private void fileOpen(){
         
         File selectedFile;
         try{
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open PDF File");
            fileChooser.getExtensionFilters().addAll(
                    new ExtensionFilter("PDF", "*.pdf"));
            selectedFile = fileChooser.showOpenDialog(mainStage);
            if (selectedFile != null) {            
                pdfPages = pdfWorkerRef.readPDF(selectedFile);
            }
            if(pdfPages == null)return;
            //System.out.println("canvas Size: " + currentCanvasList.getWidth());
            for(Iterator<PDFPage> i = pdfPages.iterator(); i.hasNext();){
                existPDFVBox.getChildren().add(i.next());
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    @FXML
    private void penButtonAction(MouseEvent e){
        try{
            pdfWorkerRef.penCase.selectPen(penToolButtons.indexOf(e.getSource()));
        }catch(Exception ex){
            System.out.println("Wrong pen");
        }
    }

    double currentScale = 1.0;
    
    @FXML
    private void zoomPlus(){
        currentScale = (currentScale < 2.9)?currentScale + 0.1: 3;
        Scale scale = new Scale(currentScale, currentScale, 0, 0);
        existPDFVBox.getTransforms().add(scale);
    }
    @FXML
    private void zoomMinus(){
        currentScale = (currentScale > 0.2)?currentScale - 0.1: 0.1;
        Scale scale = new Scale(currentScale, currentScale, 0, 0);
        existPDFVBox.getTransforms().add(scale);
    }
}
