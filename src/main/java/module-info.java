module com.mycompany.ppap {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires org.jfree.fxgraphics2d;

    opens com.mycompany.ppap to javafx.fxml;
    exports com.mycompany.ppap;
    requires pdfbox;
}