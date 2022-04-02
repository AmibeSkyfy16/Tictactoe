package ch.skyfy.tictactoe.utils;

public class ValueType<T> {
    private static byte idCount = 0;
    private final byte id;

    public ValueType() {
        id = idCount++;
    }
}
