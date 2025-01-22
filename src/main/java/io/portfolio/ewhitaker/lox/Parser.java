package io.portfolio.ewhitaker.lox;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    public static class ParserError extends RuntimeException {
        public final Source.Position position;
        public final String message;

        public ParserError(Source.Position position, String message) {
            this.position = position;
            this.message = message;
        }
    }

    public final Lexer lexer;
    public final Source source;
    public final List<Token> tokens;

    public Token tok = null;

    public int current = 0;

    public Parser(Source source) {
        this.lexer = new Lexer(source, this::report);

        this.source = source;
        this.tokens = lexer.tokens();
    }

    public Expr parse() {
        try {
            return this.expression();
        } catch (ParserError e) {
            Lox.report(this.source, e.position, e.message);
            return null;
        }
    }

    public Expr expression() {
        return comma();
    }

    public Expr comma() {
        Expr expr = this.conditional();

        while (this.match(TokenType.COMMA)) {
            expr = new Expr.Comma(expr, this.conditional());
        }

        return expr;
    }

    public Expr conditional() {
        Expr expr = this.equality();

        if (this.match(TokenType.QUESTION)) {
            Expr consequence = this.expression();
            this.consume(TokenType.COLON, "Expect ':' after expression.");
            expr = new Expr.Ternary(expr, consequence, this.conditional());
        }

        return expr;
    }

    // TODO: abstract
    public Expr equality() {
        Expr expr = this.comparison();

        while (this.match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            expr = new Expr.Binary(expr, this.tok, this.comparison());
        }

        return expr;
    }

    public Expr comparison() {
        Expr expr = this.term();

        while (this.match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            expr = new Expr.Binary(expr, this.tok, this.term());
        }

        return expr;
    }

    public Expr term() {
        Expr expr = this.factor();

        while (this.match(TokenType.MINUS, TokenType.PLUS)) {
            expr = new Expr.Binary(expr, this.tok, this.factor());
        }

        return expr;
    }

    public Expr factor() {
        Expr expr = this.unary();

        while (this.match(TokenType.STAR, TokenType.SLASH)) {
            expr = new Expr.Binary(expr, this.tok, this.unary());
        }

        return expr;
    }

    public Expr unary() {
        if (this.match(TokenType.BANG, TokenType.MINUS)) {
            return new Expr.Unary(this.tok, this.unary());
        }

        if (this.match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            Token from = this.tok;
            this.equality();
            return new Expr.Illegal(from, this.tok);
        }

        if (this.match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            Token from = this.tok;
            this.comparison();
            return new Expr.Illegal(from, this.tok);
        }

        if (this.match(TokenType.PLUS)) {
            Token from = this.tok;
            this.term();
            return new Expr.Illegal(from, this.tok);
        }

        if (this.match(TokenType.STAR, TokenType.SLASH)) {
            Token from = this.tok;
            this.factor();
            return new Expr.Illegal(from, this.tok);
        }

        return this.primary();
    }

    public Expr primary() {
        if (this.match(TokenType.FALSE, TokenType.TRUE, TokenType.NIL, TokenType.NUMBER, TokenType.STRING)) {
            return new Expr.Literal(this.tok);
        }

        if (this.match(TokenType.LEFT_PAREN)) {
            Expr expr = expression();
            this.consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        throw this.error(this.current, "Expect expression.");
    }

    public boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (this.check(type)) {
                this.tok = this.advance();
                return true;
            }
        }

        return false;
    }

    public boolean check(TokenType type) {
        return this.peek().type() == type;
    }

    public Token advance() {
        return this.tokens.get(this.current++);
    }

    public Token peek() {
        if (this.current >= this.tokens.size()) {
            return this.tokens.get(this.tokens.size() - 1);
        }
        return this.tokens.get(this.current);
    }

    public Token consume(TokenType type, String message) {
        if (this.check(type)) {
            return this.advance();
        }

        throw this.error(this.current, message);
    }

    public boolean eof() {
        return this.peek().type() == TokenType.EOF;
    }

    public void report(Source.Position position, String message) {
        Lox.report(this.source, position, message);
    }

    // TODO: reconsider throwing error; record error and return 'illegal' node
    public ParserError error(int offset, String message) {
        return new ParserError(this.source.position(this.tokens.get(offset).offset()), message);
    }

    public void synchronize() {
        this.tok = this.advance();

        while (!this.eof()) {
            if (this.tok.type() == TokenType.SEMICOLON) {
                return;
            }

            switch (this.tok.type()) {
                case CLASS, FUN, VAR, FOR, IF, WHILE, PRINT, RETURN -> {
                    return;
                }
                default -> {
                }
            }

            this.tok = this.advance();
        }
    }
}
