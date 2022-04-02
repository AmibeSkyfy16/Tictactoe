package ch.skyfy.tictactoe;

import ch.skyfy.tictactoe.views.HomeView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TicTacToe extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setScene(new Scene(new HomeView()));
        stage.show();
    }

}
