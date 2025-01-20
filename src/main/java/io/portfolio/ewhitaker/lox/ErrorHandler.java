package io.portfolio.ewhitaker.lox;

@FunctionalInterface
public interface ErrorHandler {
    void report(Position position, String message);
}
