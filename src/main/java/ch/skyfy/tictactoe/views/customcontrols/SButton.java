package ch.skyfy.tictactoe.views.customcontrols;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;

public class SButton extends SLabel {

    private static final PseudoClass PSEUDO_CLASS_ARMED = PseudoClass.getPseudoClass("armed");

    private final BooleanProperty armed;

    public SButton(String str, Font font, Region region) {
        super(str, font, region);

        armed = new SimpleBooleanProperty(false);

        initFocus();
    }

    /**
     * Gère le focus
     * https://static.rainfocus.com/oracle/oow16/sess/1462484351438001p6a1/ppt/Custom%20Controls.pdf$
     * https://guigarage.com/2016/02/javafx-and-css-pseudo-classes/
     */
    private void initFocus() {
        armed.addListener(observable -> pseudoClassStateChanged(PSEUDO_CLASS_ARMED, armed.get()));
        setFocusTraversable(true);
        addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            armed.set(true);
            requestFocus();
        });
        addEventHandler(MouseEvent.MOUSE_RELEASED, event -> armed.set(false));
    }

}
