/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.ppap;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDTransparencyGroup;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.jfree.fx.FXGraphics2D;


/**
 *
 * @author akira
 */
public class PDFWorker {
    
    PenCase penCase;
    
    String fileName;
    File currentFile;
    PDDocument document;
    PDPageContentStream contentStream;
    
    //float screenRatio=1;
    
    public PDFWorker(PenCase penCase){
        this.penCase = penCase;
    }
    
    public ArrayList<PDFPage> readPDF2(File file) throws IOException{
        
        try (final PDDocument document = PDDocument.load(file)){
            List<COSBase> arguments = new ArrayList<>();
            PDPageTree list = document.getPages();
            System.out.println("list length(pages): " + list.getCount());
            for (PDPage page : list) {
                PDFStreamParser parser = new PDFStreamParser(page);
                parser.parse();
                List<Object> tokens = parser.getTokens();
                System.out.println("tokens: " + tokens.size());
                int j =0;
                COSObject co;
                Operator op;
                COSBase cb;
                for(Iterator i = tokens.iterator(); i.hasNext();){
                    Object token = i.next();
                    if(token instanceof COSObject){
                        //co = (COSObject)token;
                        arguments.add(((COSObject)token).getObject());
                        //System.out.println("No. " + j++ + " : " + co.toString());
                    }else if(token instanceof Operator){
                        //op = (Operator)token;
                       
                        //System.out.println("No. " + j++ + " : " + op.toString());
                        arguments = new ArrayList<>();
                    }else{
                        arguments.add((COSBase)token);
                        //cb = (COSBase)token;
                        //System.out.println("No. " + j++ + " : " + cb.toString());
                    }
                }
                
                //ArrayList<PDFPath> pdfPathList = PDF2FXConverter.pdf2Fx(page);
                //if(pdfPathList != null){
                //}
          
            }
        } catch (IOException e){
            System.err.println("Exception while trying to create pdf document - " + e);
        }
        return null;
    }
    
    public ArrayList<PDFPage> readPDF(File file) throws IOException{
        //try {
            //String current = new java.io.File( "." ).getCanonicalPath();
            //System.out.println("Current dir:"+current);
        //} catch( java.io.IOException e ) {
          //  e.printStackTrace();
        //}
        PDPage page;
        String currentDir = System.getProperty("user.dir");
        System.out.println("Current dir using System:" +currentDir);
        
        String fileName = file.getName();

        document = PDDocument.load(file);
        System.out.println("総ページ数：" + document.getNumberOfPages());

//        document.save("2a.pdf");
        final PDFRenderer renderer = new PDFRenderer(document);
        
        int pages = document.getNumberOfPages();
        ArrayList<PDFPage> newPages = new ArrayList<PDFPage>();
        for(int i=0; i < pages; i++){
            page = document.getPage(i);
            int rotation = page.getRotation();
            int width = (int)page.getMediaBox().getWidth();
            int height = (int)page.getMediaBox().getHeight();
            if(rotation == 90 || rotation == 270){
                System.out.println("page" + i + ": " + "rotated");
                int tt = width;
                width = height;
                height = tt;
            }
            PDFPage tempPDFPage = new PDFPage(width, height, penCase);
            ArrayList<PDFPath> pdfPathList = PDF2FXConverter.pdf2Fx(page);
            if(pdfPathList != null){
                for(PDFPath newPath: pdfPathList){
                    System.out.println("add path");
                    tempPDFPage.shapeField.getChildren().add(newPath);
                }
            }
            //FXGraphics2D g = new FXGraphics2D(tempPDFPage.backCanvas.getGraphicsContext2D());
            //tempPDFPage.page = page;
            //g.clearRect(0, 0, tempPDFPage.canvasWidth, tempPDFPage.canvasHeight);
            //renderer.renderPageToGraphics(i, g, 1);
            newPages.add(tempPDFPage);
        }
        document.close();
        page = null;
        currentFile = file;
        return newPages;
    }
    
    public String writePDF(File saveFile, ObservableList<Node> writedPDF){
        PDPage page;
        try{
            if(saveFile == null)return "error";
            document = new PDDocument();
            //document = new PDDocument();
            System.out.println("総ページ数：" + writedPDF.size());
            
            int pageNumber = writedPDF.size();
            for(int j=0; j < pageNumber; j++){
                page = new PDPage();
                //document.addPage(page);
                PDFPage currentPage = (PDFPage)writedPDF.get(j);
                currentPage.writePage(document, page);
                //document.getPages().add(page);
            }
            document.save(saveFile);
            document.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        return "File save succecced";
    }
}
