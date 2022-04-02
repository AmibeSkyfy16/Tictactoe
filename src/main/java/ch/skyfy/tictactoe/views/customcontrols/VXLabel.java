package ch.skyfy.tictactoe.views.customcontrols;

import ch.skyfy.tictactoe.utils.UIUtils;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.function.Supplier;

public class VXLabel extends Control {

    private final StringProperty textProperty;

    private final Region region;

    private Supplier<String> longestString; // permet de récupérer le chaine de caractère la plus longue (voir STextGroup)


    public VXLabel(String text, Region region){
        textProperty = new SimpleStringProperty(text);
        this.region = region;

        //----------- SIZE (WIDTH AND HEIGHT) -----------\\
        this.setMinWidth(USE_PREF_SIZE);
        this.setMaxWidth(USE_PREF_SIZE);
        this.setMinHeight(USE_PREF_SIZE);
        this.setMaxHeight(USE_PREF_SIZE);

//        region.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
//            if (newValue.getWidth() == 0 || newValue.getHeight() == 0) return;
//            this.setPrefSize(newValue.getWidth() * (50d / 100d), newValue.getHeight() * (50d / 100d));
//        });

        this.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            resizeText(newValue);
        });
        VBox.setVgrow(this, Priority.NEVER);


//        this.setPrefSize(50, 50);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new VXLabelSkin(this);
    }

    public void resizeText(Bounds bounds) {
        final double width = bounds.getWidth() - paddingProperty().get().getLeft() - paddingProperty().get().getRight();
        final double height = bounds.getHeight() - paddingProperty().get().getTop() - paddingProperty().get().getBottom();
        final double size = UIUtils.getPerkektSize(height, width, (longestString == null) ? getText() : longestString.get(), 8); // SI higherString est null, ça veut dire que notre sbutton n'est membre d'aucun STextGroup
        if (size != -1) ((VXLabelSkin)getSkin()).getText().setFont(Font.font("Sriracha", size));
    }

    public StringProperty textProperty() {
        return textProperty;
    }

    public String getText() {
        return textProperty.get();
    }

    public void setText(String text){
        textProperty.set(text);
    }

    public Region getRegion() {
        return region;
    }
}
