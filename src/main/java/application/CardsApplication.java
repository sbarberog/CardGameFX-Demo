package application;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CardsApplication extends Application {

    @Override
    @FXML
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CardsApplication.class.getResource("game-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1600, 900);
        stage.setTitle("Card Game FX");
        stage.setScene(scene);
        stage.setMinWidth(1600);
        stage.setMinHeight(900);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}