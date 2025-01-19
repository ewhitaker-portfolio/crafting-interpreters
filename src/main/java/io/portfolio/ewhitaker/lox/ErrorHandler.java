package io.portfolio.ewhitaker.lox;

@FunctionalInterface
public interface ErrorHandler {
    void report(int offset, String message);
}
