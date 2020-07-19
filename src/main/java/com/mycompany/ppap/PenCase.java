/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.ppap;

import java.util.ArrayList;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

/**
 *
 * @author akira
 */
class PenCase {
    private Pen currentPen;
    private ArrayList<Pen> penList;
    PenCase(){
        penList = new ArrayList<Pen>();
        initializePens();
    }
    
    private void initializePens(){
        penList.add(new Pen("Red", 1, 1, Color.RED));
        penList.add(new Pen("Gray", 1, 1, Color.GRAY));
        penList.add(new Pen("Aqua", 1, 0.6, Color.AQUA));
        
        currentPen = penList.get(0);
    }
    
    public void selectPen(int number){
        if(number >= penList.size()){
            System.out.println("There isn't that number of pen.");
            return;
        }
        currentPen = penList.get(number);
    }
    
    public Pen getCurrentPen(){
        return currentPen;
    }
    
    public Pen getPen(int index){
        if(index < 0 || index >= penList.size())return penList.get(0);
        return penList.get(index);
    }
}
