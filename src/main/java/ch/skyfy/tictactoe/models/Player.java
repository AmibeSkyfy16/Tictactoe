package ch.skyfy.tictactoe.models;

import ch.skyfy.tictactoe.TicTacToe;
import ch.skyfy.tictactoe.utils.TicTacToeUtils;
import ch.skyfy.tictactoe.views.TicTacToeGameView;
import ch.skyfy.tictactoe.views.animation.AnimationQueue;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;

import java.util.concurrent.CountDownLatch;

/**
 * Le tour d'un joueur se déroule en 3 étapes. C'est étapes peuvent prendre du temps
 * begin : Dans cette étape, nous affichons une animation ("Le joueur xxx commence son tour !") avec le nom du joueur qui s'apprête a jouer
 * play : le joueur joue, une animation avec le pion qui arrive dans la case
 * end : une animation qui dit "Le joueur xxx a ternimé son tour !"
 * <p>
 * Ses étapes son exécutées les unes après les autres  via l'objet ExecutorService
 */
public abstract class Player {

    protected final int Identifier;

    protected String name;

    protected final SimpleBooleanProperty playingProperty;

    protected final SimpleIntegerProperty[][] board;

    protected final SimpleBooleanProperty wonProperty;

    protected final Image profilImage;

    protected Player Opponent;

    protected AnimationQueue animationQueue;

    public Player(int identifier, String name, boolean currentPlayer, SimpleIntegerProperty[][] board) {
        this.Identifier = identifier;
        this.name = name;
        playingProperty = new SimpleBooleanProperty(currentPlayer);
        this.board = board;
        wonProperty = new SimpleBooleanProperty(false);
        profilImage = new Image(getClass().getName().contains("AI") ?
                TicTacToe.class.getResource("image/AI.png").toExternalForm() :
                TicTacToe.class.getResource("image/user.png").toExternalForm());
    }

    public void begin() {
        if (Thread.currentThread().getName().equals("Thread Executor")) { // Si on est déjà dans le thread
            animationQueue.executeAwait(() -> animationQueue.get(TicTacToeGameView.BEGIN).accept(name));
        } else {
            animationQueue.getExecutor().submit(() -> animationQueue.executeAwait(() -> animationQueue.get(TicTacToeGameView.BEGIN).accept(name)));
        }
    }

    public void play(final int rowIndex, final int columnIndex) {
        board[rowIndex][columnIndex].set(Identifier);
    }

    public void end() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            playingProperty.set(false);
            Opponent.playingProperty.set(true);
            countDownLatch.countDown();
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (Opponent instanceof PlayerAI playerAI) playerAI.start();
        else Opponent.begin();
    }

    @Override
    public String toString() {
        return name + ((getClass().getName().contains("AI")) ? " (AI) " : " ") + "est en train de jouer";
    }


    // ------------------------------ GETTERS ------------------------------\\

    public int getIdentifier() {
        return Identifier;
    }

    public Image getProfilImage() {
        return profilImage;
    }


    // ------------------------------ SETTERS ------------------------------\\

    public void setAnimationQueue(AnimationQueue animationQueue) {
        this.animationQueue = animationQueue;
    }

}
