package ch.skyfy.tictactoe.utils;

import java.util.HashMap;
import java.util.Map;

public class TypedMap {

    private final Map<ValueType<?>, Object> map = new HashMap<>();

    public <T> T get(ValueType<T> key) {
        return generify(map.get(key));
    }

    public <T> void put(ValueType<T> key, T value) {
        map.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T generify(Object cls) {
        return (T) cls;
    }

}
