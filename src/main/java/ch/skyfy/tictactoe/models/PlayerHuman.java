package ch.skyfy.tictactoe.models;

import ch.skyfy.tictactoe.views.TicTacToeGameView;
import javafx.beans.property.SimpleIntegerProperty;

public class PlayerHuman extends Player {

    public PlayerHuman(int identifier, String name, boolean currentPlayer, SimpleIntegerProperty[][] board) {
        super(identifier, name, currentPlayer, board);
    }

    public void start(final int rowIndex, final int columnIndex) {
        animationQueue.getExecutor().submit(() -> {
            animationQueue.executeAwait(() -> {
                play(rowIndex, columnIndex);
                animationQueue.get(TicTacToeGameView.PLAY).accept(rowIndex, columnIndex);
            });
            animationQueue.executeAwait(() -> animationQueue.get(TicTacToeGameView.END).accept(name));
            end();
        });
    }

}
