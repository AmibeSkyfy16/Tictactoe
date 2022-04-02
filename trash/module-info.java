module ch.skyfy.tictactoe {
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.media;
    requires java.sql;
    requires java.desktop;
    requires AnimateFX;

    exports ch.skyfy.tictactoe to
            javafx.graphics;

    opens ch.skyfy.tictactoe;

    opens ch.skyfy.tictactoe.views to
            javafx.fxml;
}