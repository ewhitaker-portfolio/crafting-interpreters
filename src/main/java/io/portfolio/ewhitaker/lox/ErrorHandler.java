package io.portfolio.ewhitaker.lox;

@FunctionalInterface
public interface ErrorHandler {
    void report(Source.Position position, String message);
}
