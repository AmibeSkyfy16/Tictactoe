package ch.skyfy.tictactoe.models;

import ch.skyfy.tictactoe.TicTacToe;
import javafx.beans.Observable;
import javafx.beans.binding.*;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import java.util.Arrays;
import java.util.stream.Collectors;
public class TicTacToeGame {

    private final SimpleIntegerProperty[][] board;
    private final Image gameEndImage;

    private final BooleanBinding gameEnd;
    private final ObjectBinding<Player> currentPlayer;
    private Player playerOne, playerTwo;


    public TicTacToeGame(byte[] players, int boardSize) {
        board = new SimpleIntegerProperty[boardSize][boardSize];

        gameEndImage = new Image(TicTacToe.class.getResource("image/end.png").toExternalForm());

        Reset(players);

        final ObservableList<SimpleIntegerProperty> boardAsList = Arrays.stream(board)
                .flatMap(Arrays::stream)
                .collect(Collectors.toCollection(() -> FXCollections.observableArrayList((p) -> new Observable[]{p})));
        gameEnd = Bindings.when(playerOne.wonProperty.isEqualTo(new SimpleBooleanProperty(true)).or(playerTwo.wonProperty.isEqualTo(new SimpleBooleanProperty(true))))
                .then(true)
                .otherwise(Bindings.createBooleanBinding(() -> boardAsList.stream().allMatch(simpleIntegerProperty -> simpleIntegerProperty.get() != -1), boardAsList));

        currentPlayer = new When(playerOne.playingProperty.isEqualTo(new SimpleBooleanProperty(true))).then(playerOne).otherwise(playerTwo);
    }

    public void ResetBoard() {
        for (int i = 0; i < board[0].length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                board[i][j] = new SimpleIntegerProperty(-1);
            }
        }
    }

    public void Reset(byte[] players) {
        ResetBoard();
        resetPlayer(players);
    }

    private void resetPlayer(byte[] players) {
        playerOne = players[0] == 1 ? new PlayerHuman(0, "Jean", true, board) : new PlayerAI(0, "Bichot", true, board);
        playerTwo = players[1] == 1 ? new PlayerHuman(1, "Jean", false, board) : new PlayerAI(1, "Bichot", false, board);
        playerOne.Opponent = playerTwo;
        playerTwo.Opponent = playerOne;

    }

    public StringBinding getWinnerName() {
        return Bindings.when(playerOne.wonProperty).then("Victoire de " + playerOne.name)
                .otherwise(Bindings.when(playerTwo.wonProperty).then("Victoire de " + playerTwo.name).otherwise("Egalité !"));
    }

    public SimpleIntegerProperty[][] getBoard() {
        return board;
    }

    public Image getGameEndImage() {
        return gameEndImage;
    }

    public BooleanBinding gameEndProperty() {
        return gameEnd;
    }

    public ObjectBinding<Player> currentPlayerProperty() {
        return currentPlayer;
    }

    public Player getPlayerOne() {
        return playerOne;
    }

    public Player getPlayerTwo() {
        return playerTwo;
    }
}
