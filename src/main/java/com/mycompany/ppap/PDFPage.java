/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.ppap;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Iterator;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

/**
 *
 * @author akira
 */
public class PDFPage extends StackPane{
/**
 * Document direction
 */
    int ratation = 0;
    
    protected int canvasWidth, canvasHeight;
    private Canvas drawingCanvas;
    protected Canvas backCanvas;
    //protected PDPage page;
    
/**
 * This field is added path
 */    
    Pane shapeField;
    /**
     * This path is beeing drew now
     */
    PDFPath currentPath;
    GraphicsContext g;
    
    /**
     * Refference to the PenCase Object
     */
    PenCase penCaseRef;
    
    public PDFPage(PenCase penCase){
        this(100,100, penCase);
    }
    
    public PDFPage(int width, int height, PenCase penCase){
        drawingCanvas = new Canvas(width, height);
        backCanvas = new Canvas(width, height);
        this.canvasWidth = width;
        this.canvasHeight = height;
        this.penCaseRef = penCase;
        shapeField = new Pane();
        shapeField.setMouseTransparent(true);
        this.getChildren().add(backCanvas);
        this.getChildren().add(drawingCanvas);
        this.getChildren().add(shapeField);
        registerEventhandler();          
    }
    
    private void registerEventhandler(){
        EventHandler<MouseEvent> mouseClick = (event) -> this.startDraw(event);
        addEventHandler(MouseEvent.MOUSE_PRESSED , mouseClick);
        EventHandler<MouseEvent> mouseDragged = (event) -> this.drawing(event);
        addEventHandler(MouseEvent.MOUSE_DRAGGED , mouseDragged);
        EventHandler<MouseEvent> mouseReleased = ( event ) -> this.stopDraw(event);
        addEventHandler( MouseEvent.MOUSE_RELEASED , mouseReleased);
    }
    
     private void drawing(MouseEvent e){
        //System.out.println("mouseDragged");
        if(g == null)return;
        double x = e.getX();
        double y = e.getY();
        if(x < 0 || x > canvasWidth-1)return;
        if(y < 0 || y > canvasHeight-1)return;
        g.clearRect(0, 0, canvasWidth, canvasHeight);
        g.lineTo(x,y);
        g.stroke();
        g.moveTo(x, y);
        currentPath.getElements().add(new LineTo(x, y));
        //g.stroke();
    }
    
    private void startDraw(MouseEvent e){
        //System.out.println("mousePressed");
        g = drawingCanvas.getGraphicsContext2D();
        g.setStroke(penCaseRef.getCurrentPen().color);
        g.setLineWidth(penCaseRef.getCurrentPen().width);
        g.setGlobalAlpha(penCaseRef.getCurrentPen().alpha);
        g.setLineCap(penCaseRef.getCurrentPen().lineCap);
        g.beginPath();
        currentPath = new PDFPath("Nagai");
        currentPath.setColor(penCaseRef.getCurrentPen().color);
        MoveTo moveto = new MoveTo();
        double x = e.getX();
        double y = e.getY();
        moveto.setX(x);
        moveto.setY(y);
        currentPath.getElements().add(moveto);
        g.moveTo(x, y);
    }
    
    private void stopDraw(MouseEvent e){
        if(g !=null){
            g.clearRect(0, 0, canvasWidth, canvasHeight);
            currentPath.setStroke(g.getStroke());
            currentPath.setStrokeWidth(g.getLineWidth());
            currentPath.setOpacity(g.getGlobalAlpha());
            currentPath.setStrokeLineCap(g.getLineCap());
            currentPath.time = LocalDateTime.now();
            shapeField.getChildren().add(currentPath);
            //pathList.add(currentPath);
            g = null;
        }
    }
    
    public PDPage writePage(PDDocument targetDocument, PDPage page) throws IOException {
        targetDocument.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(targetDocument, page, PDPageContentStream.AppendMode.APPEND, false, true);
        float height = page.getMediaBox().getHeight();

        if (contentStream == null) {
            System.out.println("This Page didn't saved");
            return null;
        }
        ObservableList<Node> currentPathes = shapeField.getChildren();
        for (Iterator<Node> i = currentPathes.iterator(); i.hasNext();) {
            Color color = Color.BLACK;
            Path path = (Path) i.next();
            if (path instanceof PDFPath) {
                color = ((PDFPath) path).getColor();
                //System.out.println("setColor: " + color.toString());
            }
            ObservableList<PathElement> op = path.getElements();
            Iterator<PathElement> element = op.iterator();
            MoveTo mt = (MoveTo) element.next();
            System.out.println("moveto");
            try {
                contentStream.moveTo((float) (mt.getX()), (float) (height - mt.getY()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (; element.hasNext();) {
                LineTo drawLine = (LineTo) element.next();
                try {
                    contentStream.lineTo((float) (drawLine.getX()), (float) (height - drawLine.getY()));
                    //System.out.println("lineto");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                contentStream.setStrokingColor((int)(color.getRed()*255), (int)(color.getGreen()*255), (int)(color.getBlue()*255));
                contentStream.stroke();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        contentStream.close();
        return page;
    }
}