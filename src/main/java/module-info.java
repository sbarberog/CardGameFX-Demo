module application {
//    requires javafx.controls;
    requires javafx.fxml;
//    requires javafx.web;

    requires org.controlsfx.controls;
//    requires org.kordamp.ikonli.javafx;
////    requires eu.hansolo.tilesfx;
//    requires com.almasb.fxgl.all;
//    requires java.sql;

    opens application to javafx.fxml;
    exports application;
}