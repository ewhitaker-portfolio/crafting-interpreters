package io.portfolio.ewhitaker.lox;

public record Token(TokenType type, String lexeme, int offset) {
    @Override
    public String toString() {
        return "Token" + "{" +
                "type=" + this.type + "," +
                "lexeme='" + this.lexeme + "'," +
                "offset=" + this.offset +
                "}";
    }
}
