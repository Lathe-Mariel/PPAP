/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.ppap;

import java.time.LocalDateTime;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;

/**
 *
 * @author akira
 */
public class PDFPath extends Path {
    String editer;
    LocalDateTime time;
    Color color;
    
    public PDFPath(){
        super();
        //color = Color.BLACK;
    }
    public PDFPath(String editer){
        this();
        this.editer = editer;
    }
    
    public Color getColor(){
        return color;
    }
    
    public void setColor(Color color){
        this.color = color;
        setStroke(color);
    }
    
    public void writeToPDStream(){
        
    }
}
