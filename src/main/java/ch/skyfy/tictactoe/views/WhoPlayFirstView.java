package ch.skyfy.tictactoe.views;

import ch.skyfy.tictactoe.TicTacToe;
import ch.skyfy.tictactoe.utils.FXMLUtils;
import ch.skyfy.tictactoe.views.customcontrols.SButton;
import ch.skyfy.tictactoe.views.customcontrols.SLabel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class WhoPlayFirstView extends StackPane implements Initializable {

    @FXML
    private StackPane top_stackPane, bottom_stackPane;

    @FXML
    private HBox center_hbox;

    @FXML
    private RowConstraints top_rowContraints, center_rowConstraints, bottom_rowContraints;

    final double[] primaryStageSize; // 0 : la largeur, 1: la hauteur

    private boolean playFirst;

    private boolean canceled;

    final Stage primaryStage;

    SButton first, second;

    public WhoPlayFirstView(double[] primaryStageSize, Stage primaryStage) {
        this.primaryStageSize = primaryStageSize;
        this.primaryStage = primaryStage;
        canceled = true;
        FXMLUtils.loadFXML(TicTacToe.class, "fxml/WhoPlayFirst2.fxml", this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setPrefWidth(primaryStageSize[0] * 0.8);
        this.setPrefHeight(primaryStageSize[1] * 0.6);

        createTopUI();
        createCenterUI();
        createBottomUI();

        Platform.runLater(this::requestFocus); // Par défaut, au lancement, le premier button prend la focus automatiquement et on ne veut qu'uncun soit focus au départ
    }

    private void createTopUI() {
        final SLabel sLabel = new SLabel("Choisissez qui joue en premier !", Font.font("Sriracha", 8), top_stackPane);
        sLabel.makeResponsive(80, 80, 5, 5, 5, 5);
        top_stackPane.getChildren().add(sLabel);
    }

    private void createCenterUI() {
        first = new SButton("Je joue en premier", Font.font("Sriracha", 8), center_hbox);
        first.makeResponsive(40, 40, 4, 4, 4, 4);
        first.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1 && event.getButton() == MouseButton.PRIMARY) {
                closeStage(event);
            }
        });

        second = new SButton("Je joue en deuxième", Font.font("Sriracha", 8), center_hbox);
        second.makeResponsive(40, 40, 4, 4, 4, 4);
        second.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1 && event.getButton() == MouseButton.PRIMARY) {
                closeStage(event);
            }
        });
        center_hbox.getChildren().addAll(first, second);
    }

    private void createBottomUI() {
        final SButton leave = new SButton("Retour", Font.font("Sriracha", 8), bottom_stackPane);
        leave.makeResponsive(35, 75, 12, 8, 12, 8);
        leave.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1 && event.getButton() == MouseButton.PRIMARY) {
                final Stage stage = (Stage) this.getScene().getWindow();
                stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
            }
        });
        bottom_stackPane.getChildren().add(leave);
    }

    private void closeStage(MouseEvent event) {
        canceled = false;
        final Node node = (Node) event.getSource();
        if (node.equals(first)) playFirst = true;
        ((Stage) node.getScene().getWindow()).close();
    }

    public boolean isPlayFirst() {
        return playFirst;
    }

    public boolean isCanceled() {
        return canceled;
    }
}
