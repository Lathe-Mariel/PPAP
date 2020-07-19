/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.ppap;

import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

/**
 *
 * @author akira
 */
class Pen{
    String name;
    Color color;
    double alpha = 1.0f;
    double width;
    StrokeLineCap lineCap = StrokeLineCap.ROUND;
    StrokeLineJoin lineJoin = StrokeLineJoin.BEVEL;

    Pen(String name, double width, double alpha, Color color){
        this.width = width;
        this.alpha = alpha;
        this.color = color;
    }
}
