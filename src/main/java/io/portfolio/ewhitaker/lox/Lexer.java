package io.portfolio.ewhitaker.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lexer {
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and", TokenType.AND);
        keywords.put("class", TokenType.CLASS);
        keywords.put("else", TokenType.ELSE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("for", TokenType.FOR);
        keywords.put("fun", TokenType.FUN);
        keywords.put("if", TokenType.IF);
        keywords.put("nil", TokenType.NIL);
        keywords.put("or", TokenType.OR);
        keywords.put("print", TokenType.PRINT);
        keywords.put("return", TokenType.RETURN);
        keywords.put("super", TokenType.SUPER);
        keywords.put("this", TokenType.THIS);
        keywords.put("true", TokenType.TRUE);
        keywords.put("var", TokenType.VAR);
        keywords.put("while", TokenType.WHILE);
    }

    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    public Lexer(String source) {
        this.source = source;
    }

    public List<Token> ScanTokens() {
        while (!this.isAtEnd()) {
            // We are at the beginning of the next lexeme.
            this.start = this.current;
            this.scanToken();
        }

        this.tokens.add(new Token(TokenType.EOF, "", null, line));
        return this.tokens;
    }

    private void scanToken() {
        char c = this.advance();
        switch (c) {
            case '(' -> this.addToken(TokenType.LEFT_PAREN);
            case ')' -> this.addToken(TokenType.RIGHT_PAREN);
            case '{' -> this.addToken(TokenType.LEFT_BRACE);
            case '}' -> this.addToken(TokenType.RIGHT_BRACE);
            case ',' -> this.addToken(TokenType.COMMA);
            case '.' -> this.addToken(TokenType.DOT);
            case '-' -> this.addToken(TokenType.MINUS);
            case '+' -> this.addToken(TokenType.PLUS);
            case ';' -> this.addToken(TokenType.SEMICOLON);
            case '*' -> this.addToken(TokenType.STAR);
            case '!' -> this.addToken(this.match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
            case '=' -> this.addToken(this.match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
            case '<' -> this.addToken(this.match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
            case '>' -> this.addToken(this.match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
            case '/' -> {
                if (this.match('/')) {
                    // A comment goes until the end of the line.
                    while (this.peek() != '\n' && !this.isAtEnd()) {
                        this.advance();
                    }
                } else {
                    this.addToken(TokenType.SLASH);
                }
            }

            case ' ', '\r', '\t' -> {
                // Ignore whitespace.
            }

            case '\n' -> ++this.line;

            case '"' -> this.string();

            default -> {
                if (this.isDigit(c)) {
                    this.number();
                } else if (this.isAlpha(c)) {
                    this.identifier();
                } else {
                    Lox.Error(this.line, "Unexpected character.");
                }
            }
        }
    }

    private void identifier() {
        while (this.isAlphaNumeric(this.peek())) {
            this.advance();
        }

        String text = this.source.substring(this.start, this.current);
        TokenType type = keywords.get(text);
        if (type == null) {
            type = TokenType.IDENTIFIER;
        }
        this.addToken(type);
    }

    private void number() {
        while (this.isDigit(this.peek())) {
            this.advance();
        }

        // Look for a fractional part.
        if (this.peek() == '.' && this.isDigit(this.peekNext())) {
            // Consume the "."
            this.advance();

            while (this.isDigit(this.peek())) {
                this.advance();
            }
        }

        this.addToken(TokenType.NUMBER, Double.parseDouble(this.source.substring(this.start, this.current)));
    }

    private void string() {
        while (this.peek() != '"' && !this.isAtEnd()) {
            if (this.peek() == '\n') {
                ++this.line;
            }
            this.advance();
        }

        if (this.isAtEnd()) {
            Lox.Error(this.line, "Unterminated string.");
            return;
        }

        // The closing ".
        this.advance();

        // Trim the surrounding quotes.
        String value = this.source.substring(this.start + 1, this.current - 1);
        this.addToken(TokenType.STRING, value);
    }

    private boolean match(char expected) {
        if (this.isAtEnd()) {
            return false;
        }

        if (this.source.charAt(this.current) != expected) {
            return false;
        }

        ++this.current;
        return true;
    }

    private char peek() {
        if (this.isAtEnd()) {
            return '\0';
        }
        return this.source.charAt(this.current);
    }

    public char peekNext() {
        if (this.current + 1 >= this.source.length()) {
            return '\0';
        }
        return this.source.charAt(this.current + 1);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return this.isAlpha(c) || this.isDigit(c);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAtEnd() {
        return this.current >= this.source.length();
    }

    private char advance() {
        return this.source.charAt(this.current++);
    }

    private void addToken(TokenType type) {
        this.addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = this.source.substring(this.start, this.current);
        this.tokens.add(new Token(type, text, literal, line));
    }
}
