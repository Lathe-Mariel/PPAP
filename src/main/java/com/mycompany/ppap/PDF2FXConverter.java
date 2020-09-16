/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.ppap;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import org.apache.pdfbox.contentstream.PDContentStream;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.text.NextLine;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;

/**
 *
 * @author akira
 */
public class PDF2FXConverter {
    public PDF2FXConverter(){
        super();
    }
    
    public static ArrayList<PDFPath> pdf2Fx(PDPage pdPage){
        
        ArrayList<PDFPath> readObjects = new ArrayList<PDFPath>();
        
        parseAndTranslate(pdPage, readObjects);
        
        return readObjects;
    }
    
    static private void parseAndTranslate(PDPage pdPage, ArrayList<PDFPath> readObjects){
        List<COSBase> arguments = new ArrayList<>();
        PDFPath newObject = null;
        
        try{
            PDFStreamParser parser = new PDFStreamParser(pdPage);
            Object token = parser.parseNextToken();
            while (token != null){
                if (token instanceof COSObject){
                    System.out.println("add COSObject");
                    arguments.add(((COSObject) token).getObject());
                }else if (token instanceof Operator){
                    //PDFPath newObject = new PDFPath();
                    
                    System.out.println("pdf2Fx: " + ((Operator)token).getName() + " (" + token.getClass() + ")");
                    String operator = ((Operator)token).getName();
                    
                    if(operator.equals("l")){    //lineTo
                        COSInteger cosx = (COSInteger)arguments.get(0);
                        COSInteger cosy = (COSInteger)arguments.get(1);
                        if(newObject != null){
                            newObject.getElements().add(new LineTo(cosx.intValue(), cosy.intValue()));
                        }
                        arguments.clear();

                    }else if(operator.equals("m")){    //moveTo
                        newObject = new PDFPath();
                        COSInteger cosx = (COSInteger)arguments.get(0);
                        COSInteger cosy = (COSInteger)arguments.get(1);
                        newObject.getElements().add(new MoveTo(cosx.intValue(), cosy.intValue()));
                        arguments.clear();
                    }else if(operator.equals("S")){    //Object end?
                        System.out.println("S");
                        readObjects.add(newObject);
                        arguments.clear();
                        newObject = null;
                    }else if(operator.equals("RG")){    //set color for the path
                        System.out.println("setting color for the path [RG]");
                        COSInteger cosR = (COSInteger)arguments.get(0);
                        COSInteger cosG = (COSInteger)arguments.get(1);
                        COSInteger cosB = (COSInteger)arguments.get(2);
                        //System.out.println("cosR: " + arguments.get(0));
                        //System.out.println("cosR: " + arguments.get(1)); 
                        //System.out.println("cosR: " + arguments.get(2));                        
                        newObject.setColor(Color.color(cosR.floatValue(), cosG.floatValue(), cosB.floatValue()));
                    }
                }else{
                    System.out.println("add COSBase: " + (COSBase)token);
                    arguments.add((COSBase) token);
                }
                token = parser.parseNextToken();
            }
        }catch(IOException e ){
            
        }
    }
}
