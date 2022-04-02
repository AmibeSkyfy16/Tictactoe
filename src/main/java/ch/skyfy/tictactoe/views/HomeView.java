package ch.skyfy.tictactoe.views;

import ch.skyfy.tictactoe.TicTacToe;
import ch.skyfy.tictactoe.utils.FXMLUtils;
import ch.skyfy.tictactoe.views.customcontrols.SButton;
import ch.skyfy.tictactoe.views.customcontrols.SLabel;
import ch.skyfy.tictactoe.views.customcontrols.VXLabel;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class HomeView extends StackPane implements Initializable {

    @FXML
    private GridPane bottom_gridPane;

    @FXML
    private VBox center_vbox;

    public HomeView() {
        loadFont();
        FXMLUtils.loadFXML(TicTacToe.class, "fxml/home.fxml", this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createGUI();
        Platform.runLater(this::requestFocus); // Par défaut, au lancement, le premier button prend la focus automatiquement
    }

    private void createGUI() {
        createBaseGUI();
        createButton();
    }

    /**
     * Background and responsive margin for the vbox
     */
    private void createBaseGUI() {
        final Image image = new Image(TicTacToe.class.getResourceAsStream("image/bc.png"));
        this.setBackground(new Background(new BackgroundImage(
                image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(1920, 1080, true, true, false, true)
        )));
        center_vbox.spacingProperty().bind(Bindings.createDoubleBinding(() -> this.heightProperty().multiply(0.05).get(), this.heightProperty()));


        // CECI EST UN TEST --------------------------
        final Pane pane = new Pane();
        pane.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(10), new Insets(0))));
        pane.setPrefSize(100, 100);
        pane.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        StackPane.setAlignment(pane, Pos.CENTER);
        this.getChildren().addAll(pane);
        pane.setTranslateX(-(this.getPrefWidth() / 2) - (pane.getPrefWidth() / 2));
        final TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(6));
        translateTransition.setNode(pane);
        translateTransition.setToX(this.getPrefWidth() / 2);
        translateTransition.playFromStart();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                translateTransition.stop();
                TranslateTransition translateTransition1 = new TranslateTransition(Duration.seconds(2));
                translateTransition1.setNode(pane);
                translateTransition1.setToX(HomeView.this.getPrefWidth() / 2);
                translateTransition1.playFromStart();
            }
        }, 1200);
        // FIN DU TEST --------------------------

    }

    @FXML
    private StackPane test;

    private void createButton() {

//        VXLabel label = new VXLabel("HADDA", center_vbox);
//        center_vbox.getChildren().add(label);
//TODO enlver sa
//        SLabel sLabel = new SLabel("HADDA", Font.font("Sriracha", 8), center_vbox);
////        sLabel.makeResponsive(40, 20, 3.5, 3.5, 3.5, 3.5);
//        sLabel.makeResponsive(40, 20, 0,0,0,0);
//
//        center_vbox.getChildren().add(sLabel);
//
//        center_vbox.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 0, 0.5), new CornerRadii(0), new Insets(0))));
//
//        if(0 == 0)return;

        final SButton sButton = new SButton("Singleplayer", Font.font("Sriracha", 8), center_vbox);
        sButton.setId("singleplayer");
        sButton.makeResponsive(40, 20, 3.5, 3.5, 3.5, 3.5);
        sButton.setOnMouseClicked(event -> new SinglePlayerWindows().show((Stage) this.getScene().getWindow()));

        final SButton sButton2 = new SButton("Multiplayer Online", Font.font("Sriracha", 8), center_vbox);
        sButton2.setId("multiplayer-local");
        sButton2.makeResponsive(40, 20, 3.5, 3.5, 3.5, 3.5);

        final SButton sButton3 = new SButton("Multiplayer Local", Font.font("Sriracha", 8), center_vbox);
        sButton3.setId("multiplayer-online");
        sButton3.makeResponsive(40, 20, 3.5, 3.5, 3.5, 3.5);

        center_vbox.getChildren().addAll(sButton, sButton2, sButton3);

        new SLabel.STextGroup(sButton, sButton2, sButton3); // Pour que les 3 textes aient la même taille !

        // TODO
//        SButton sButton1 = new SButton("TEST", Font.font("Sriracha", 4), test);
//        sButton1.makeResponsive(40, 20, 3.5, 3.5, 3.5, 3.5);
//        test.getChildren().add(sButton1);

        // ------------------- BOTTOM -------------------\\

        final SButton options = new SButton("Options", Font.font("Sriracha", 8), bottom_gridPane);
        options.setId("options");
        options.makeResponsive(30, 60, 5.5, 5.5, 5.5, 5.5);

        final SButton quitter = new SButton("Quitter", Font.font("Sriracha", 8), bottom_gridPane);
        quitter.setId("leave");
        quitter.makeResponsive(30, 60, 5.5, 5.5, 5.5, 5.5);

        bottom_gridPane.add(options, 1, 0);
        bottom_gridPane.add(quitter, 2, 0);
    }

    private void loadFont() {
        Font.loadFont(TicTacToe.class.getResource("font/Sriracha-Regular.ttf").toExternalForm(), 12);
        Font.loadFont(TicTacToe.class.getResource("font/Sen-Bold.ttf").toExternalForm(), 12);
    }

}
