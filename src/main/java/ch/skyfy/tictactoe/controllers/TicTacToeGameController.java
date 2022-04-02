package ch.skyfy.tictactoe.controllers;

import ch.skyfy.tictactoe.models.Player;
import ch.skyfy.tictactoe.models.PlayerAI;
import ch.skyfy.tictactoe.models.PlayerHuman;
import ch.skyfy.tictactoe.models.TicTacToeGame;
import ch.skyfy.tictactoe.views.animation.AnimationQueue;

public class TicTacToeGameController {

    private final TicTacToeGame tttGame;

    public TicTacToeGameController(AnimationQueue animationQueue, byte[] players) {
        this.tttGame = new TicTacToeGame(players, 3);
        tttGame.getPlayerOne().setAnimationQueue(animationQueue);
        tttGame.getPlayerTwo().setAnimationQueue(animationQueue);
    }

    public void start() {
        if (tttGame.getPlayerOne() instanceof PlayerAI playerAI) playerAI.start();
        else {
            tttGame.getPlayerOne().begin();
        }
    }

    public void play(int rowIndex, int columnIndex) {
        if (tttGame.gameEndProperty().get()) return;
        if (tttGame.getBoard()[rowIndex][columnIndex].get() != -1) return;

        final Player currentPlayer = tttGame.currentPlayerProperty().get();

        if (currentPlayer instanceof PlayerAI playerAI) playerAI.start();
        else if (currentPlayer instanceof PlayerHuman playerHuman) playerHuman.start(rowIndex, columnIndex);
    }

    public TicTacToeGame getTttGame() {
        return tttGame;
    }
}
