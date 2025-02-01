package io.portfolio.ewhitaker.lox;

public record Token(TokenType type, String lexeme, Object literal, int line) {
    public String toString() {
        return this.type + " " + this.lexeme + " " + this.literal;
    }
}
