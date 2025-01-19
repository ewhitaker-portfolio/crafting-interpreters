package io.portfolio.ewhitaker.lox;

public record Token(TokenType type, String lexeme, Object literal, int line) {
    @Override
    public String toString() {
        return "Token" + "{" + "type=" + this.type + "lexeme=" + this.lexeme + "literal=" + this.literal + "}";
    }
}
