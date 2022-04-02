package ch.skyfy.tictactoe.models;

import ch.skyfy.tictactoe.utils.TicTacToeUtils;
import ch.skyfy.tictactoe.views.TicTacToeGameView;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.Arrays;

public class PlayerAI extends Player {

    public PlayerAI(int identifier, String name, boolean currentPlayer, SimpleIntegerProperty[][] board) {
        super(identifier, name, currentPlayer, board);
    }

    public void start() {
        if (Thread.currentThread().getName().equals("Thread Executor")) { // Si on est déjà dans le thread
            startImpl();
        } else {
            animationQueue.getExecutor().submit(this::startImpl);
        }
    }

    private void startImpl() {
        int currentDepth = GetActualDepth(board);
        if (currentDepth == 0) return; // game is end


        // PHASE BEGIN
        begin();

        // PHASE PLAY
        final Move bestMove = FindBestMove(TicTacToeUtils.getBoard(board), currentDepth);
        animationQueue.executeAwait(() -> {
            play(bestMove.row, bestMove.column);
            animationQueue.get(TicTacToeGameView.PLAY).accept(bestMove.row, bestMove.column);
        });

        // PHASE END
        if (TicTacToeUtils.checkVictory(TicTacToeUtils.getBoard(board), getIdentifier())) {
            wonProperty.set(true);
            return;
        }
        animationQueue.executeAwait(() -> animationQueue.get(TicTacToeGameView.END).accept(name));
        end();
    }

    private int GetActualDepth(SimpleIntegerProperty[][] board) {
        return (int) Arrays.stream(TicTacToeUtils.getBoard(board)).flatMapToInt(Arrays::stream).filter(value -> value == -1).count();
    }

    private Move FindBestMove(int[][] board, int depth) {
        int x = 0, y = 0;
        int maxValue = -100;
        for (byte i = 0; i < board[0].length; i++) {
            for (byte j = 0; j < board[0].length; j++) {
                if (board[i][j] == -1) {
                    board[i][j] = Identifier;
                    int value = Minimax(board, (depth - 1), false);
                    board[i][j] = -1;
                    if (maxValue < value) {
                        maxValue = value;
                        x = i;
                        y = j;
                    }
                }
            }
        }
        return new Move(x, y);
    }

    private int Minimax(int[][] board, int depth, boolean maximizingPlayer) {
        if (depth == 0 || TicTacToeUtils.checkVictory(board, 0) || TicTacToeUtils.checkVictory(board, 1)) {
            if (TicTacToeUtils.checkVictory(board, Identifier)) { // IA WIN
                return 100;
            } else if (TicTacToeUtils.checkVictory(board, Opponent.Identifier)) { // RIVAL WIN
                return -100;
            } else {
                return 0;
            }
        }
        if (maximizingPlayer) {
            int maxValue = -100;
            for (int i = 0; i < board[0].length; i++) {
                for (int j = 0; j < board[0].length; j++) {
                    if (board[i][j] == -1) {
                        board[i][j] = Identifier;
                        int value = Minimax(board, (depth - 1), false);
                        board[i][j] = -1;
                        maxValue = Math.max(maxValue, value);
                    }
                }
            }
            return maxValue;
        } else {
            int minValue = 100;
            for (int i = 0; i < board[0].length; i++) {
                for (int j = 0; j < board[0].length; j++) {
                    if (board[i][j] == -1) {
                        board[i][j] = Opponent.Identifier;
                        int value = Minimax(board, depth - 1, true);
                        board[i][j] = -1;
                        minValue = Math.min(minValue, value);
                    }
                }
            }
            return minValue;
        }
    }

    private record Move(int row, int column) {
    }

}
