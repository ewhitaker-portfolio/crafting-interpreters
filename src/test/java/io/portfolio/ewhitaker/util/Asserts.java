package io.portfolio.ewhitaker.util;

import java.util.Objects;

public class Asserts {
    public static void isEqual(Object actual, Object expected, String message) {
        assert Objects.equals(expected, actual) : isEqualMessage(actual, expected, message);
    }

    public static void fail(String message) {
        assert false : message;
    }

    public static String isEqualMessage(Object actual, Object expected, String message) {
        return message.isBlank() ? "expected: " + expected + " actual: " + actual
                : message + " ===> " + "expected: " + expected + " actual: " + actual;
    }
}
