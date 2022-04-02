package ch.skyfy.tictactoe.utils;

import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

public class UIUtils {

    /**
     * This method returns the largest font so that the text can be contained in the rectangle (width, height)
     *
     * @param height
     * @param width
     * @param string
     * @param actual
     * @return
     */
    public static double getPerkektSize(double height, double width, String string, double actual) {
        if (height <= 0 || width <= 0) return -1;
        Text text;
        double textWidth, textHeight;
        double temp;
        double size = actual;
        do {
            text = new Text(string);
            text.setBoundsType(TextBoundsType.VISUAL);
            text.setFont(Font.font("Sriracha", size));
            textWidth = text.getLayoutBounds().getWidth();
            textHeight = text.getLayoutBounds().getHeight();
            temp = size;
            size++;
        } while ((height > width) ? (textWidth < width && textHeight < height) : (textHeight < height && textWidth < width));
        return temp;
    }

}
