package ch.skyfy.tictactoe.views;

import animatefx.animation.Flip;
import animatefx.animation.LightSpeedIn;
import animatefx.animation.SlideInLeft;
import animatefx.animation.SlideInUp;
import ch.skyfy.tictactoe.TicTacToe;
import ch.skyfy.tictactoe.controllers.TicTacToeGameController;
import ch.skyfy.tictactoe.models.Player;
import ch.skyfy.tictactoe.utils.FXMLUtils;
import ch.skyfy.tictactoe.utils.ValueType;
import ch.skyfy.tictactoe.views.animation.AnimationQueue;
import ch.skyfy.tictactoe.views.customcontrols.SButton;
import ch.skyfy.tictactoe.views.customcontrols.SLabel;
import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TicTacToeGameView extends StackPane implements Initializable {

    public static final ValueType<Consumer<String>> BEGIN = new ValueType<>();
    public static final ValueType<BiConsumer<Integer, Integer>> PLAY = new ValueType<>();
    public static final ValueType<Consumer<String>> END = new ValueType<>();

    @FXML
    private GridPane root_gridPane;

    @FXML
    private RowConstraints top_rowConstraints, bottom_rowConstraints;

    @FXML
    private HBox top_hbox, bottom_hbox;

    @FXML
    private ImageView profilPlayer_img;

    @FXML
    private StackPane bottom_stackpane;

    @FXML
    private StackPane top_stackpane;

    private final StackPane gameGridContainer;
    private final GridPane gameGrid;

    private SLabel currentPlayer_SLabel;

    private final Image crossImage;
    private final Image circleImage;

    private final Semaphore semaphore;

    private final AnimationQueue animationQueue;

    private final TicTacToeGameController tttGameController;

    private final ConcurrentMap<String, Animation> currentAnimation;

    final AtomicBoolean accelerate;

    public TicTacToeGameView(final byte[] players) {
        gameGridContainer = new StackPane();
        gameGrid = new GridPane();
        crossImage = new Image(TicTacToe.class.getResource("image/cross.png").toExternalForm());
        circleImage = new Image(TicTacToe.class.getResource("image/circle.png").toExternalForm());

        semaphore = new Semaphore(0);
        animationQueue = new AnimationQueue(semaphore);

        tttGameController = new TicTacToeGameController(animationQueue, players);

        currentAnimation = new ConcurrentHashMap<>();
        accelerate = new AtomicBoolean(false);

        animationQueue.put(BEGIN, this::beginAnimation);
        animationQueue.put(PLAY, this::playAnimation);
        animationQueue.put(END, this::endAnimation);


        FXMLUtils.loadFXML(TicTacToe.class, "fxml/TicTacToe.fxml", this);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        createTop();
        createCenter();
        tttGameController.getTttGame().gameEndProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) animationQueue.executeAwait2(this::playEndAnimation);
        });
        this.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (currentAnimation.get("BEGIN") != null) {
                    final Animation animation = currentAnimation.get("BEGIN");
//                    if (animation.getStatus() == Animation.Status.RUNNING && !accelerate.get()) {
//                        accelerate.set(true);
//                        animation.pause();
//                        animation.stop();
//                    }
                } else if (currentAnimation.get("PLAY") != null) {
                    final Animation animation = currentAnimation.get("PLAY");
                    if (animation.getStatus() == Animation.Status.RUNNING && !accelerate.get()) {
                        accelerate.set(true);
                        animation.pause();
                        animation.stop();
                    }
                } else if (currentAnimation.get("END") != null) {
                   final Animation animation = currentAnimation.get("END");
//                    if (animation.getStatus() == Animation.Status.RUNNING && !accelerate.get()) {
//                        accelerate.set(true);
//                        animation.pause();
//                        animation.stop();
//                    }
                }
            }
        });
        tttGameController.start();
    }

    private void createTop() {
        currentPlayer_SLabel = new SLabel("", Font.font("Sriracha", 8), top_hbox);
        currentPlayer_SLabel.makeResponsive(70, 90, 1.5, 2, 1.5, 2);
        currentPlayer_SLabel.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(0), new Insets(0))));

        // Resize de l'image
        top_hbox.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            double size = Math.min(newValue.getWidth() * 0.22, newValue.getHeight() * 0.98);
            profilPlayer_img.setFitWidth(size);
            profilPlayer_img.setFitHeight(size);
        });

        // Mise en place d'une liaison conditionnel pour le texte
        currentPlayer_SLabel.getText().textProperty().bind(Bindings.when(tttGameController.getTttGame().gameEndProperty().isEqualTo(new SimpleBooleanProperty(false)))
                .then(tttGameController.getTttGame().currentPlayerProperty().asString())
                .otherwise(tttGameController.getTttGame().getWinnerName()));

        // Lorsque le texte change, sa taille est recalculé
        currentPlayer_SLabel.getText().textProperty().addListener((observable, oldValue, newValue) -> {
            currentPlayer_SLabel.resizeText(currentPlayer_SLabel.getLayoutBounds());
        });

        // Même chose pour l'image
        profilPlayer_img.imageProperty().bind(Bindings.when(tttGameController.getTttGame().gameEndProperty().isEqualTo(new SimpleBooleanProperty(false)))
                .then(Bindings.createObjectBinding(() -> tttGameController.getTttGame().currentPlayerProperty().get().getProfilImage(), tttGameController.getTttGame().currentPlayerProperty()))
                .otherwise(tttGameController.getTttGame().getGameEndImage()));

        top_hbox.getChildren().add(currentPlayer_SLabel);
    }

    /**
     * this method create and add ImageView object to gameGrid
     */
    private void createGameBoard() {
        gameGrid.setGridLinesVisible(true);
        gameGrid.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        for (byte i = 0; i < 3; i++) {
            gameGrid.getRowConstraints().add(new RowConstraints() {{
                setPercentHeight(100d / 3d);
            }});
            gameGrid.getColumnConstraints().add(new ColumnConstraints() {{
                setPercentWidth(100d / 3d);
            }});
        }

        for (byte i = 0; i < 3; i++) {
            for (byte j = 0; j < 3; j++) {
                final StackPane cell_stackPane = new StackPane();
                cell_stackPane.setMinSize(10, 10);

                final ImageView imageView = new ImageView();

                final byte finalI = i, finalJ = j;
                cell_stackPane.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        if (currentAnimation.get("BEGIN") != null || currentAnimation.get("PLAY") != null || currentAnimation.get("END") != null || currentAnimation.get("END_GAME") != null) {
                            System.out.println("ATTENTEZ QUE L'ANIMATION SOIT TERMINE AVANT DE JOUER !! ");
                            return;
                        }
                        tttGameController.play(finalI, finalJ);
                    }
                });
                cell_stackPane.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
                    final double minSize = Math.min(newValue.getWidth(), newValue.getHeight());
                    imageView.setFitWidth(minSize * 0.9);
                    imageView.setFitHeight(minSize * 0.9);
                });
                StackPane.setMargin(imageView, new Insets(0));
                StackPane.setAlignment(imageView, Pos.CENTER);
                cell_stackPane.getChildren().add(imageView);
                gameGrid.add(cell_stackPane, j, i);
            }
        }

        // Gère le redimensionnement de la grille
        gameGridContainer.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.getHeight() == 0 || newValue.getWidth() == 0) return;
            Insets margin = new Insets(newValue.getHeight() * 0.05, newValue.getWidth() * 0.2, newValue.getHeight() * 0.05, newValue.getWidth() * 0.2);
            StackPane.setMargin(gameGrid, margin);
            double size = Math.min(newValue.getWidth() - margin.getRight() - margin.getLeft(), newValue.getHeight() - margin.getTop() - margin.getTop());
            gameGrid.setPrefSize(size, size);
        });

        StackPane.setMargin(gameGrid, new Insets(0));
        StackPane.setAlignment(gameGrid, Pos.CENTER);
        gameGridContainer.getChildren().add(gameGrid);
    }

    private void createCenter() {
        createGameBoard();
        root_gridPane.add(gameGridContainer, 0, 1);
    }

    private void playEndAnimation() {
        final StackPane root = new StackPane();
        root.setBackground(new Background(new BackgroundFill(Color.valueOf("#7831AA"), new CornerRadii(0), new Insets(0))));
        root.setTranslateX(this.widthProperty().multiply(-1).doubleValue());

        final GridPane gridPane = new GridPane();
        gridPane.getRowConstraints().add(new RowConstraints(10, 30, -1) {{
            setPercentHeight(20);
            setValignment(VPos.CENTER);
            setVgrow(Priority.SOMETIMES);
        }});
        gridPane.getRowConstraints().add(new RowConstraints(10, 30, -1) {{
            setPercentHeight(60);
            setValignment(VPos.CENTER);
            setVgrow(Priority.SOMETIMES);
        }});
        gridPane.getRowConstraints().add(new RowConstraints(10, 30, -1) {{
            setPercentHeight(20);
            setValignment(VPos.CENTER);
            setVgrow(Priority.SOMETIMES);
        }});
        gridPane.getColumnConstraints().add(new ColumnConstraints(10, 30, -1) {{
            setHalignment(HPos.CENTER);
            setHgrow(Priority.SOMETIMES);
        }});

        final StackPane top_stackPane = new StackPane();

        final StackPane bottom_stackPane = new StackPane();

        final HBox bottom_hbox = new HBox();
        bottom_hbox.setAlignment(Pos.CENTER);
        bottom_hbox.setSpacing(10);

        final SButton back = new SButton("Retour", Font.font("Sriracha"), bottom_hbox);
        back.makeResponsive(30, 50, 5, 5, 5, 5);
        back.setOnMouseClicked(event -> ((Stage) this.getScene().getWindow()).setScene(new Scene(new HomeView(), this.getScene().getWidth(), this.getScene().getHeight())));

        final SButton replay = new SButton("rejouer", Font.font("Sriracha"), bottom_hbox);
        replay.makeResponsive(30, 50, 5, 5, 5, 5);
        replay.setOnMouseClicked(event -> new SinglePlayerWindows().show((Stage) this.getScene().getWindow()));

        new SLabel.STextGroup(replay, back); // Permet au deux buttons d'avoir la même taille de police

        bottom_hbox.getChildren().addAll(back, replay);

        bottom_stackPane.getChildren().add(bottom_hbox);

        gridPane.add(top_stackPane, 0, 0);
        gridPane.add(bottom_stackPane, 0, 2);

        StackPane.setMargin(gridPane, new Insets(0));
        StackPane.setAlignment(gridPane, Pos.CENTER);
        root.getChildren().add(gridPane);
        this.getChildren().add(root);

        final WritableImage gameGridImage = gameGrid.snapshot(new SnapshotParameters(), null);
        final ImageView imageView = new ImageView(gameGridImage);

        final StackPane imageViewContainer = new StackPane();
        imageViewContainer.setMinSize(10, 10);
        StackPane.setAlignment(imageView, Pos.CENTER);
        imageViewContainer.getChildren().add(imageView);
        gridPane.add(imageViewContainer, 0, 1);
        imageViewContainer.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            final double size = Math.min(newValue.getWidth(), newValue.getHeight());
            imageView.setFitWidth(size);
            imageView.setFitHeight(size);
        });

        final SLabel sLabel = new SLabel(tttGameController.getTttGame().getWinnerName().get(), Font.font("Sriracha", 8), top_stackPane);
        sLabel.getText().setVisible(false);
        sLabel.makeResponsive(80, 70, 1.5, 1.5, 1.5, 1.5);

        final TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(1.6), root);
        translateTransition.byXProperty().bind(this.widthProperty());
        translateTransition.play();

        translateTransition.setOnFinished(event -> {
            this.getChildren().remove(root_gridPane);
            StackPane.setAlignment(sLabel, Pos.CENTER);
            StackPane.setMargin(sLabel, new Insets(0, 0, 0, 0));
            top_stackPane.getChildren().add(sLabel);
            final LightSpeedIn lightSpeedIn = new LightSpeedIn(sLabel.getText());
            final Flip flip = new Flip(sLabel.getText());
            flip.getTimeline().getKeyFrames().add(new KeyFrame(Duration.millis(1), new KeyValue(sLabel.getText().visibleProperty(), true)));
            lightSpeedIn.play();
            flip.play();
            currentAnimation.remove("END_GAME");
        });
        currentAnimation.put("END_GAME", translateTransition);
    }

    public void beginAnimation(String name) {
        final StackPane container = new StackPane();
        this.getChildren().add(container);
        container.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(0), new Insets(0))));
        container.setOpacity(0.5);
        container.setMinSize(10, 10);
        container.setOnMouseClicked(event2 -> {
            Event.fireEvent(this, new MouseEvent(
                    MouseEvent.MOUSE_CLICKED,
                    event2.getX(),
                    event2.getY(),
                    getScene().getX(), getScene().getY(),
                    MouseButton.PRIMARY, 1,
                    true, true, true, true, true, true, true, true, true, true, null
            ));
        });
        StackPane.setMargin(container, new Insets(10));
        this.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            Insets margin = new Insets(
                    newValue.getHeight() * 0.3,
                    newValue.getWidth() * 0.3,
                    newValue.getHeight() * 0.3,
                    newValue.getWidth() * 0.3);
            StackPane.setMargin(container, margin);
        });

        final SLabel sLabel = new SLabel(name + " commence son tour !", Font.font("Sriracha", 8), container);
        sLabel.makeResponsive(80, 60, 5, 5, 5, 5);
        container.getChildren().addAll(sLabel);

        final SlideInUp slideInUp = new SlideInUp(sLabel.getText());
        final SlideInLeft slideInLeft = new SlideInLeft(sLabel.getText());
        final TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(1.5));
        translateTransition.setNode(container);
        translateTransition.setToX(300);
        final ParallelTransition pt = new ParallelTransition();
        pt.getChildren().addAll(slideInUp.getTimeline(), slideInLeft.getTimeline(), translateTransition);
        pt.setOnFinished(event2 -> {
            this.getChildren().remove(container);
            currentAnimation.remove("BEGIN");
            semaphore.release(1);
        });
        currentAnimation.put("BEGIN", pt);
        pt.play();
    }

    public void playAnimation(final int row, final int column) {
        for (Node child : gameGrid.getChildrenUnmodifiable()) {
            if (child instanceof StackPane theChild && GridPane.getColumnIndex(child) == column && GridPane.getRowIndex(child) == row) {
                final var childs2 = theChild.getChildrenUnmodifiable();
                if (childs2.get(0) instanceof ImageView imageView) {
                    final Player curr = tttGameController.getTttGame().currentPlayerProperty().get();
                    if (curr.getIdentifier() == 0) {
                        imageView.setImage(crossImage);
                        playerTwoPlayAnimation(imageView);
                    } else {
                        imageView.setImage(circleImage);
                        playerOnePlayAnimation(imageView);
                    }
                }
            }
        }
    }

    private void playerOnePlayAnimation(final ImageView imageView) {
        imageView.setOpacity(0);
        imageView.setTranslateX(-imageView.getFitWidth());
        final StackPane parent = (StackPane) imageView.getParent();

        final FadeTransition ft = new FadeTransition(Duration.millis(3000), imageView);
        ft.setToValue(100);

        final TranslateTransition tt = new TranslateTransition(Duration.millis(3000), imageView);
        tt.setToX(0);

        // Le clip permet de faire apparaître le cercle seulement quand il entre dans la case
        final Rectangle clip = new Rectangle();
        clip.heightProperty().bind(parent.heightProperty());
        clip.widthProperty().bind(parent.widthProperty());
        parent.setClip(clip);

        final ParallelTransition pt = new ParallelTransition();
        pt.getChildren().addAll(ft, tt);
        pt.statusProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Animation.Status.PAUSED) {
                acceleratePlayerOnePlayAnimation(pt);
            }
        });
        pt.setOnFinished(event -> {
            currentAnimation.remove("PLAY");
            semaphore.release(1);
        });
        currentAnimation.put("PLAY", pt);

        final Media sound = new Media(TicTacToe.class.getResource("sound/sound1.mp3").toExternalForm());
        final MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.setVolume(0.5);
        
        pt.play();
        mediaPlayer.play();
    }

    /**
     * Reprend l'animation "playerOnePlayAnimation" et en créée une nouvelle beaucoup plus rapide
     *
     * @param parallelTransition
     */
    private void acceleratePlayerOnePlayAnimation(ParallelTransition parallelTransition) {
        final FadeTransition fadeTransition = (FadeTransition) parallelTransition.getChildren().get(0);
        final TranslateTransition translateTransition = (TranslateTransition) parallelTransition.getChildren().get(1);
        FadeTransition fadeTransition1 = new FadeTransition(Duration.millis(300));
        fadeTransition1.setNode(fadeTransition.getNode());
        fadeTransition1.setToValue(100);

        TranslateTransition translateTransition1 = new TranslateTransition(Duration.millis(300));
        translateTransition1.setNode(translateTransition.getNode());
        translateTransition1.setToX(0);

        ParallelTransition parallelTransition1 = new ParallelTransition();
        parallelTransition1.getChildren().addAll(fadeTransition1, translateTransition1);
        System.out.println("THGREAD " + Thread.currentThread().getName());
        parallelTransition1.setOnFinished(event -> {
            currentAnimation.remove("PLAY");
            accelerate.set(false);
            semaphore.release(1);
        });
        parallelTransition1.playFromStart();
    }

    private void playerTwoPlayAnimation(final ImageView imageView) {
        imageView.setScaleX(0);
        imageView.setScaleY(0);
        final RotateTransition rt = new RotateTransition(Duration.millis(3000), imageView);
        rt.setToAngle(270);
        rt.setInterpolator(Interpolator.LINEAR);

        final ScaleTransition st = new ScaleTransition(Duration.millis(3000), imageView);
        st.setToX(1);
        st.setToY(1);
        st.setToZ(1);

        final ParallelTransition pt = new ParallelTransition();
        pt.getChildren().addAll(rt, st);
        pt.statusProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Animation.Status.PAUSED) {
                acceleratePlayerTwoPlayAnimation(pt);
            }
        });
        pt.setOnFinished(event -> {
            currentAnimation.remove("PLAY");
            semaphore.release(1);
        });
        currentAnimation.put("PLAY", pt);
        pt.play();
    }

    /**
     * Reprend l'animation "playerTwoPlayAnimation" et en créée une nouvelle beaucoup plus rapide
     *
     * @param parallelTransition
     */
    private void acceleratePlayerTwoPlayAnimation(ParallelTransition parallelTransition) {
        final RotateTransition rotateTransition = (RotateTransition) parallelTransition.getChildren().get(0);
        final ScaleTransition scaleTransition = (ScaleTransition) parallelTransition.getChildren().get(1);
        ImageView imageView = (ImageView) rotateTransition.getNode();

        RotateTransition rotateTransition1 = new RotateTransition(Duration.millis(300), rotateTransition.getNode());
        rotateTransition1.setNode(imageView);
        rotateTransition1.setToAngle(270);
        rotateTransition1.setInterpolator(Interpolator.LINEAR);

        ScaleTransition scaleTransition1 = new ScaleTransition(Duration.millis(200), scaleTransition.getNode());
        scaleTransition1.setNode(imageView);
        scaleTransition1.setFromX(imageView.getScaleX());
        scaleTransition1.setFromY(imageView.getScaleY());
        scaleTransition1.setFromZ(imageView.getScaleZ());
        scaleTransition1.setToX(1);
        scaleTransition1.setToY(1);
        scaleTransition1.setToZ(1);

        ParallelTransition parallelTransition1 = new ParallelTransition();
        parallelTransition1.getChildren().addAll(rotateTransition1, scaleTransition1);
        parallelTransition1.setOnFinished(event -> {
            currentAnimation.remove("PLAY");
            accelerate.set(false);
            semaphore.release(1);
        });
        parallelTransition1.play();
    }

    public void endAnimation(String s) {

        final StackPane container = new StackPane();
        this.getChildren().add(container);
        container.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(0), new Insets(0))));
        container.setOpacity(0.5);
        container.setMinSize(10, 10);
        container.setOnMouseClicked(event2 -> {
            Event.fireEvent(this, new MouseEvent(
                    MouseEvent.MOUSE_CLICKED,
                    event2.getX(),
                    event2.getY(),
                    getScene().getX(), getScene().getY(),
                    MouseButton.PRIMARY, 1,
                    true, true, true, true, true, true, true, true, true, true, null
            ));
        });
        StackPane.setMargin(container, new Insets(10));
        this.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            Insets margin = new Insets(
                    newValue.getHeight() * 0.3,
                    newValue.getWidth() * 0.3,
                    newValue.getHeight() * 0.3,
                    newValue.getWidth() * 0.3);
            StackPane.setMargin(container, margin);
        });

        SLabel sLabel = new SLabel(s + " termine son tour !", Font.font("Sriracha", 8), container);
        sLabel.makeResponsive(80, 60, 5, 5, 5, 5);
        container.getChildren().addAll(sLabel);

        final SlideInUp slideInUp = new SlideInUp(sLabel.getText());
        final SlideInLeft slideInLeft = new SlideInLeft(sLabel.getText());
        final ParallelTransition pt = new ParallelTransition();
        final TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(1.5));
        translateTransition.setNode(container);
        translateTransition.setToX(300);
        pt.getChildren().addAll(slideInUp.getTimeline(), slideInLeft.getTimeline(), translateTransition);
        pt.setOnFinished(event2 -> {
            this.getChildren().remove(container);
            currentAnimation.remove("END");
            semaphore.release(1);
        });
        currentAnimation.put("END", pt);
        pt.play();
    }
}
