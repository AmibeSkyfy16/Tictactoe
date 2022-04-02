package ch.skyfy.tictactoe.views;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SinglePlayerWindows extends Stage {

    public void show(Stage parent) {
        // Calcules pour centrer correctement la fenêtre par rapport à la fenêtre principale
        final double centerXPosition = parent.getX() + parent.getWidth() / 2d;
        final double centerYPosition = parent.getY() + parent.getHeight() / 2d;

        final double[] primaryStageSize = new double[]{parent.getWidth(), parent.getHeight()}; // Dim. de la fenêtre principale
        final WhoPlayFirstView whoPlayFirstView = new WhoPlayFirstView(primaryStageSize, parent); // LA GUI pour choisir qui commence la game
        this.setResizable(false);
        this.initOwner(parent);
        this.initModality(Modality.WINDOW_MODAL);
        this.setScene(new Scene(whoPlayFirstView));

        this.setOnShowing(ev -> this.hide()); // Hide the pop-up stage before it is shown and becomes relocated
        this.setOnShown(ev -> { // Relocate the pop-up Stage
            this.setX(centerXPosition - this.getWidth() / 2d);
            this.setY(centerYPosition - this.getHeight() / 2d);
            this.show();
        });
        this.showAndWait(); // Bloque la suite du code temps que cette fenêtre n'est pas fermé

        if (!whoPlayFirstView.isCanceled())
            parent.setScene(new Scene(new TicTacToeGameView(whoPlayFirstView.isPlayFirst() ? new byte[]{1, 2} : new byte[]{2, 1}),
                    parent.getScene().getWidth(), parent.getScene().getHeight()));
    }

}
