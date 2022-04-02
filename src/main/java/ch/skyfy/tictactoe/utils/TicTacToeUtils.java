package ch.skyfy.tictactoe.utils;

import javafx.beans.binding.IntegerExpression;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.Arrays;

public class TicTacToeUtils {
    public static boolean checkVictory(int[][] board, int player) {
        byte v1 = 0;
        byte v2 = 0;
        byte v3 = 0;
        byte v4 = 0;
        for (byte i = 0; i < board[0].length; i++) {
            for (byte j = 0; j < board[0].length; j++) {
                if (board[j][i] == player) v1++;
                if (board[i][j] == player) v2++;
                if (j == i && board[j][i] == player) v3++;
                if (i == 3 - j - 1 && board[j][i] == player) v4++;

                if (v1 == 3 || v2 == 3 || v3 == 3) {
                    return true;
                }
            }
            if (v4 == 3) {
                return true;
            }
            v1 = 0;
            v2 = 0;
        }
        return false;
    }

    public static int[][] getBoard(SimpleIntegerProperty[][] board) {
        return Arrays.stream(board)
                .map(simpleIntegerProperties -> Arrays.stream(simpleIntegerProperties)
                        .mapToInt(IntegerExpression::getValue).toArray()).toArray(int[][]::new);
    }

}
