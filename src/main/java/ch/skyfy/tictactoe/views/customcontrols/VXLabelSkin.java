package ch.skyfy.tictactoe.views.customcontrols;

import javafx.geometry.Insets;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

public class VXLabelSkin extends SkinBase<VXLabel> {

    private final StackPane container;

    private final Text text;

    private final Region region;

    /**
     * Constructor for all SkinBase instances.
     *
     * @param control The control for which this Skin should attach to.
     */
    protected VXLabelSkin(VXLabel control) {
        super(control);

        region = control.getRegion();

        container = new StackPane();

        text = new Text();
        text.setFont(Font.font("Sriracha", 160));
        text.setBoundsType(TextBoundsType.VISUAL);
        text.textProperty().bind(control.textProperty());

        this.getSkinnable().setBackground(new Background(new BackgroundFill(Color.ROSYBROWN, new CornerRadii(0), new Insets(0))));

        getChildren().add(text);
    }

//    private double truc(){
////        region.layout();
//    }

    @Override protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return text.minWidth(height);
    }
    @Override protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return text.minHeight(width);
    }
    @Override protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        region.layout();
        return 200;
//        return region.widthProperty().get() * (50d / 100d);
//        return text.prefWidth(height) + leftInset + rightInset;
    }
    @Override protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        region.layout();
        return 200;
//        return region.heightProperty().get() * (100d / 100d);
//        return text.prefHeight(width) + topInset + bottomInset;
    }

    @Override protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
    }
    @Override protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
    }
    @Override protected void layoutChildren(double x, double y, double w, double h) {
        text.resizeRelocate(x, y, w, h);
    }

    public Text getText() {
        return text;
    }
}
