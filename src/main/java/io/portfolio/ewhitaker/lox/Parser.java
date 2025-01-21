package io.portfolio.ewhitaker.lox;

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

    public Token matched = null;
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
        return equality();
    }

    // TODO: abstract
    public Expr equality() {
        Expr expr = this.comparison();

        while (this.match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            expr = new Expr.Binary(expr, this.matched, this.comparison());
        }

        return expr;
    }

    public Expr comparison() {
        Expr expr = this.term();

        while (this.match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            expr = new Expr.Binary(expr, this.matched, this.term());
        }

        return expr;
    }

    public Expr term() {
        Expr expr = this.factor();

        while (this.match(TokenType.MINUS, TokenType.PLUS)) {
            expr = new Expr.Binary(expr, this.matched, this.factor());
        }

        return expr;
    }

    public Expr factor() {
        Expr expr = this.unary();

        while (this.match(TokenType.STAR, TokenType.SLASH)) {
            expr = new Expr.Binary(expr, this.matched, this.unary());
        }

        return expr;
    }

    public Expr unary() {
        if (this.match(TokenType.BANG, TokenType.MINUS)) {
            return new Expr.Unary(this.matched, this.unary());
        }

        return this.primary();
    }

    public Expr primary() {
        if (this.match(TokenType.FALSE)) {
            return new Expr.Literal(false);
        } else if (this.match(TokenType.TRUE)) {
            return new Expr.Literal(true);
        } else if (this.match(TokenType.NIL)) {
            return new Expr.Literal(null);
        } else if (this.match(TokenType.NUMBER)) {
            return new Expr.Literal(Double.parseDouble(this.matched.lexeme()));
        } else if (this.match(TokenType.STRING)) {
            return new Expr.Literal(this.matched.lexeme());
        } else if (this.match(TokenType.LEFT_PAREN)) {
            Expr expr = expression();
            this.consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        throw this.error(this.current, "Expect expression.");
    }

    public boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (this.check(type)) {
                this.matched = this.advance();
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
        Token token = this.advance();

        while (!this.eof()) {
            if (token.type() == TokenType.SEMICOLON) {
                return;
            }

            switch (token.type()) {
                case CLASS, FUN, VAR, FOR, IF, WHILE, PRINT, RETURN -> {
                    return;
                }
                default -> {
                }
            }

            token = this.advance();
        }
    }
}
