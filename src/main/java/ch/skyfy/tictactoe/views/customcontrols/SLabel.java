package ch.skyfy.tictactoe.views.customcontrols;

import ch.skyfy.tictactoe.TicTacToe;
import ch.skyfy.tictactoe.utils.UIUtils;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

import java.util.function.Supplier;

public class SLabel extends StackPane {

    private final Text text;
    private final Region region;

    private final SimpleObjectProperty<Insets> marginProperty;

    private Supplier<String> longestString; // permet de récupérer le chaine de caractère la plus longue (voir STextGroup)

    {
        this.setBackground(new Background(new BackgroundFill(Color.rgb(255, 0, 255, 0.8), new CornerRadii(0), new Insets(0))));
    }

    public SLabel(String str, Font font, Region region) {
        this.text = new Text(str) {{
            setBoundsType(TextBoundsType.VISUAL);
            setFont(font);

        }};
        this.marginProperty = new SimpleObjectProperty<>(new Insets(0));
        this.region = region;
        this.getChildren().add(text);

        String id = (this.getClass() == SLabel.class) ? "slabel" : "sbutton";
        this.getStylesheets().add(TicTacToe.class.getResource("css/control/" + id + ".css").toExternalForm());
        this.getStyleClass().add(id);
    }

//    @Override
//    protected void layoutChildren() {
//        super.layoutChildren();
//        this.requestLayout(); // TODO : RESOUT LE BUG GRAPHIC DE REDIMENSIONNEMENT
//        request(this);
//    }

    public void request(Parent parent) {
        parent.requestLayout();
        Parent parent1 = parent.getParent();
        if (parent1 != null) request(parent1);
    }

    public void makeResponsive(double percentWidth, double percentHeight,
                               double percentMarginTop, double percentMarginRight,
                               double percentMarginBottom, double percentMarginLeft) {

        //----------- SIZE (WIDTH AND HEIGHT) -----------\\
        this.setMinWidth(USE_PREF_SIZE);
        this.setMaxWidth(USE_PREF_SIZE);
        this.setMinHeight(USE_PREF_SIZE);
        this.setMaxHeight(USE_PREF_SIZE);


        region.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.getWidth() == 0 || newValue.getHeight() == 0) return;
            this.setPrefSize(newValue.getWidth() * (percentWidth / 100), newValue.getHeight() * (percentHeight / 100));

            Insets margin = new Insets(
                    newValue.getHeight() * (percentMarginTop / 100),
                    newValue.getWidth() * (percentMarginRight / 100),
                    newValue.getHeight() * (percentMarginBottom / 100),
                    newValue.getWidth() * (percentMarginLeft / 100));

            marginProperty.set(margin);
        });

        //----------- RESIZE TEXT IN SBUTTON -----------\\
        this.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> resizeText(newValue));
    }

    public void resizeText(Bounds bounds) {
        final double width = bounds.getWidth() - marginProperty.get().getLeft() - marginProperty.get().getRight();
        final double height = bounds.getHeight() - marginProperty.get().getTop() - marginProperty.get().getBottom();
        final double size = UIUtils.getPerkektSize(height, width, (longestString == null) ? text.getText() : longestString.get(), 8); // SI higherString est null, ça veut dire que notre sbutton n'est membre d'aucun STextGroup
        if (size != -1) text.setFont(Font.font("Sriracha", size));
    }

    private void setLongestString(Supplier<String> longestString) {
        this.longestString = longestString;
    }

    public Text getText() {
        return text;
    }


    /**
     * Cette classe regroupe plusieurs SLabel ou SButton afin
     * de calculer la taille de police la plus adapté
     */
    public static class STextGroup {

        private int maxLenght = 0;
        private String text;

        private final Supplier<String> longestString = () -> text;

        public STextGroup(SLabel... sLabels) {
            addAll(sLabels);
        }

        public void addAll(SLabel... sLabels) {
            for (SLabel sLabel : sLabels) {
                sLabel.setLongestString(longestString);
                int size = sLabel.getText().getText().length();
                if (size > maxLenght) {
                    maxLenght = size;
                    text = sLabel.getText().getText();
                }
            }
        }

    }

}
