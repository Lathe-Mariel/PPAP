package com.mycompany.ppap;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.rendering.PDFRenderer;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Region;

public class PdfView extends Region {

	private ObjectProperty<PDDocument> documentProperty
		= new SimpleObjectProperty<PDDocument>(this, "document");
	
	public ObjectProperty<PDDocument> documentProperty() {
		return documentProperty;
	}
	public final PDDocument getDocument() {
		return documentProperty.get();
	}
	public final void setDocument(PDDocument value) {
		documentProperty.set(value);
	}

	
	private IntegerProperty pageIndexProperty
		= new SimpleIntegerProperty(this, "pageIndex");
	
	public IntegerProperty pageIndexProperty() {
		return pageIndexProperty;
	}
	public final int getPageIndex() {
		return pageIndexProperty.get();
	}
	public final void setPageIndex(int value) {
		pageIndexProperty.set(value);
	}
	
	
	private IntegerProperty maxPageIndexProperty
		= new SimpleIntegerProperty(this, "maxPageIndex");

	public ReadOnlyIntegerProperty maxPageIndexProperty() {
		return maxPageIndexProperty;
	}
	public final int getMaxPageIndex() {
		return maxPageIndexProperty.get();
	}
	public final void setMaxPageIndex(int value) {
		maxPageIndexProperty.set(value);
	}

	private Canvas canvas;
	private boolean isDirty;
	private boolean isBusy;

	private ExecutorService worker;
	private double width;
	private double height;
	private PDDocument document;
	private int pageIndex;
	private BufferedImage bimg;
	private WritableImage wimg;
	
	public PdfView() {
		worker = Executors.newSingleThreadExecutor(r -> {
			Thread t = new Thread(r);
			t.setDaemon(true);
			return t;
		});
		
		canvas = new Canvas();
		canvas.widthProperty().bind(widthProperty());
		canvas.heightProperty().bind(heightProperty());
		getChildren().add(canvas);
		
		documentProperty.addListener((observable, oldValue, newValue) -> {
			pageIndexProperty.set(0);
			if(newValue == null) {
				maxPageIndexProperty.set(0);
			} else {
				maxPageIndexProperty.set(newValue.getNumberOfPages() - 1);
			}
			update();
		});
		
		pageIndexProperty.addListener((observable, oldValue, newValue) -> {
			update();
		});
		widthProperty().addListener((observable, oldValue, newValue) -> {
			update();
		});
		heightProperty().addListener((observable, oldValue, newValue) -> {
			update();
		});
	}
	
	public void update() {
		isDirty = true;
		
		if(!isBusy) {
			isBusy = true;
			isDirty = false;
			
			width = getWidth();
			height = getHeight();
			document = documentProperty.get();
			pageIndex = pageIndexProperty.get();
			
			worker.submit(() -> {
				WritableImage img = prepare();
				Platform.runLater(() -> {
					GraphicsContext gc = canvas.getGraphicsContext2D();
					gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
					if(img != null) {
						double x = (canvas.getWidth() - img.getWidth()) / 2;
						double y = (canvas.getHeight() - img.getHeight()) / 2;
						gc.drawImage(img, x, y);
					}
					isBusy = false;
					if(isDirty) {
						update();
					}
				});
			});
		}
	}
	
	protected WritableImage prepare() {
		if(document == null) {
			return null;
		}
		PDRectangle paper = document.getPage(pageIndex).getMediaBox();
		double w;
		double h;
		if(paper.getWidth() / paper.getHeight() < width / height) {
			w = height * paper.getWidth() / paper.getHeight();
			h = height;
		} else {
			w = width;
			h = width * paper.getHeight() / paper.getWidth();
		}
		float scale = (float)(h / paper.getHeight());
		
		if(bimg == null || bimg.getWidth() != (int)w || bimg.getHeight() != (int)h) {
			bimg = new BufferedImage((int)w, (int)h, BufferedImage.TYPE_INT_RGB);
			wimg = new WritableImage((int)w, (int)h);
		}
		Graphics2D graphics = null;
		try {
			graphics = bimg.createGraphics();
			//graphics.setBackground(Color.WHITE);
			graphics.clearRect(0, 0, (int)w, (int)h);
			
			PDFRenderer renderer = new PDFRenderer(document);
			renderer.renderPageToGraphics(pageIndex, graphics, scale);
			return SwingFXUtils.toFXImage(bimg, wimg);
		} catch(IOException e) {
			throw new RuntimeException(e);
		} finally {
			if(graphics != null) {
				graphics.dispose();
			}
		}
	}
}