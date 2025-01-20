package io.portfolio.ewhitaker.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lexer {
    public static final Map<String, TokenType> keywords = new HashMap<>();

    static {
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

    public final String source;
    public final ErrorHandler handler;

    public int start = 0;
    public int current = 0;

    public List<Integer> lines = new ArrayList<>();

    public Lexer(String source, ErrorHandler handler) {
        this.source = source;
        this.handler = handler;

        this.lines.add(0);
    }

    public List<Token> tokens() {
        List<Token> tokens = new ArrayList<>();
        Token next;
        while ((next = this.next()).type() != TokenType.EOF) {
            tokens.add(next);
        }
        tokens.add(next);
        return tokens;
    }

    public Token next() {
        while (!this.eof()) {
            this.start = this.current; // We are at the beginning of the next lexeme.

            char c = this.advance();
            Token token = switch (c) {
                case '(' -> this.emit(TokenType.LEFT_PAREN);
                case ')' -> this.emit(TokenType.RIGHT_PAREN);
                case '{' -> this.emit(TokenType.LEFT_BRACE);
                case '}' -> this.emit(TokenType.RIGHT_BRACE);
                case ',' -> this.emit(TokenType.COMMA);
                case '.' -> this.emit(TokenType.DOT);
                case '-' -> this.emit(TokenType.MINUS);
                case '+' -> this.emit(TokenType.PLUS);
                case ';' -> this.emit(TokenType.SEMICOLON);
                case '*' -> this.emit(TokenType.STAR);
                case '!' -> {
                    if (this.match('=')) {
                        yield this.emit(TokenType.BANG_EQUAL);
                    } else {
                        yield this.emit(TokenType.BANG);
                    }
                }
                case '=' -> {
                    if (this.match('=')) {
                        yield this.emit(TokenType.EQUAL_EQUAL);
                    } else {
                        yield this.emit(TokenType.EQUAL);
                    }
                }
                case '<' -> {
                    if (this.match('=')) {
                        yield this.emit(TokenType.LESS_EQUAL);
                    } else {
                        yield this.emit(TokenType.LESS);
                    }
                }
                case '>' -> {
                    if (this.match('=')) {
                        yield this.emit(TokenType.GREATER_EQUAL);
                    } else {
                        yield this.emit(TokenType.GREATER);
                    }
                }
                case '/' -> {
                    if (this.match('/')) {
                        // A comment goes until the end of the line.
                        while (this.peek(0) != '\n' && !this.eof()) {
                            this.advance();
                        }
                        yield null;
                    } else if (this.match('*')) {
                        int stack = 1;
                        while (stack != 0 && !this.eof()) {
                            if (this.peek(0) == '/' && this.peek(1) == '*') {
                                this.advance();
                                ++stack;
                            } else if (this.peek(0) == '*' && this.peek(1) == '/') {
                                this.advance();
                                --stack;
                            }
                            if (this.peek(0) == '\n') {
                                this.lines.add(this.current);
                            }
                            this.advance();
                        }
                        yield null;
                    } else {
                        yield this.emit(TokenType.SLASH);
                    }
                }
                case ' ', '\r', '\t' -> null; // Ignore whitespace
                case '\n' -> {
                    this.lines.add(this.current);
                    yield null;
                }
                case '"' -> string();
                default -> {
                    if (isNumeric(c)) {
                        yield this.number();
                    } else if (isAlpha(c)) {
                        yield this.identifier();
                    }

                    yield this.error("Unexpected character.");
                }
            };

            if (token != null) {
                return token;
            }
        }

        return new Token(TokenType.EOF, "", this.current);
    }

    public Token identifier() {
        while (isAlphaNumeric(peek(0))) {
            this.advance();
        }

        final String lexeme = this.source.substring(this.start, this.current);
        TokenType type = keywords.get(lexeme);
        return this.emit(type == null ? TokenType.IDENTIFIER : type, lexeme);
    }

    public Token number() {
        while (isNumeric(this.peek(0))) {
            this.advance();
        }

        if (this.peek(0) == '.' && isNumeric(this.peek(1))) {
            this.advance();

            while (isNumeric(this.peek(0))) {
                this.advance();
            }
        }

        return this.emit(TokenType.NUMBER);
    }

    public Token string() {
        while (this.peek(0) != '"' && this.peek(0) != '\n' && !this.eof()) {
            this.advance();
        }

        if (this.peek(0) != '"') {
            return this.error("Unterminated string.");
        }

        // The closing ".
        this.advance();

        final String lexeme = source.substring(this.start + 1, this.current - 1);
        return this.emit(TokenType.STRING, lexeme);
    }

    public boolean match(char expect) {
        if (this.peek(0) != expect) {
            return false;
        }

        this.advance();
        return true;
    }

    public char peek(int distance) {
        if (this.current + distance >= this.source.length()) {
            return '\0';
        }
        return source.charAt(this.current + distance);
    }

    public char advance() {
        return this.source.charAt(this.current++);
    }

    public boolean eof() {
        return this.current >= this.source.length();
    }

    public Token error(String message) {
        if (this.handler != null) {
            int ln = 0;
            int r = this.lines.size();
            while (ln < r) {
                int i = (ln + r) >> 1;
                if (this.lines.get(i) <= this.start) {
                    ln = i + 1;
                } else {
                    r = i;
                }
            }
            final int col = this.start - this.lines.get(ln - 1) + 1;
            final String info;
            if (this.lines.size() == 1) {
                info = this.source;
            } else if (ln >= this.lines.size()) {
                info = this.source.substring(this.lines.get(ln - 1));
            } else {
                info = this.source.substring(this.lines.get(ln - 1), this.lines.get(ln) - 1);
            }
            final Position position = new Position(this.start, ln, col, info);
            this.handler.report(position, message);
        }
        return this.emit(TokenType.ILLEGAL);
    }

    public Token emit(TokenType type) {
        return this.emit(type, this.source.substring(this.start, this.current));
    }

    public Token emit(TokenType type, String lexeme) {
        return new Token(type, lexeme, this.start);
    }

    public static boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    public static boolean isNumeric(char c) {
        return c >= '0' && c <= '9';
    }

    public static boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isNumeric(c);
    }
}
