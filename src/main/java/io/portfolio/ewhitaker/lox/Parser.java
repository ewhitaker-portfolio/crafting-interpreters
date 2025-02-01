package io.portfolio.ewhitaker.lox;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private static class ParserError extends RuntimeException {
    }

    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

//@formatter:off Statements and State
//  public Expr parse() {
//      try {
//          return this.expression();
//      } catch (ParserError error) {
//          return null;
//      }
//  }
//@formatter:on

    public List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!this.isAtEnd()) {
            statements.add(this.declaration());
        }

        return statements;
    }

    private Expr expression() {
//@formatter:off Statements and State
//      return this.equality();
//@formatter:on
        return this.assignment();
    }

    private Stmt declaration() {
        try {
            if (this.match(TokenType.VAR)) {
                return this.varDeclaration();
            }

            return this.statement();
        } catch (ParserError error) {
            this.synchronize();
            return null;
        }
    }

    private Stmt statement() {
        if (this.match(TokenType.PRINT)) {
            return this.printStatement();
        }

        if (this.match(TokenType.LEFT_BRACE)) {
            return new Stmt.Block(this.block());
        }

        return this.expressionStatement();
    }

    private Stmt printStatement() {
        Expr value = this.expression();
        this.consume(TokenType.SEMICOLON, "Expect ';' after value.");
        return new Stmt.Print(value);
    }

    private Stmt varDeclaration() {
        Token name = this.consume(TokenType.IDENTIFIER, "Expect variable name.");

        Expr initializer = null;
        if (this.match(TokenType.EQUAL)) {
            initializer = this.expression();
        }

        this.consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.");
        return new Stmt.Var(name, initializer);
    }

    private Stmt expressionStatement() {
        Expr expr = this.expression();
        this.consume(TokenType.SEMICOLON, "Expect ';' after expression.");
        return new Stmt.Expression(expr);
    }

    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();

        while (!this.check(TokenType.RIGHT_BRACE) && !this.isAtEnd()) {
            statements.add(this.declaration());
        }

        this.consume(TokenType.RIGHT_BRACE, "Expect '}' after block.");
        return statements;
    }

    private Expr assignment() {
        Expr expr = this.equality();

        if (this.match(TokenType.EQUAL)) {
            Token equals = this.previous();
            Expr value = this.assignment();

            if (expr instanceof Expr.Variable var) {
                Token name = var.name();
                return new Expr.Assign(name, value);
            }

            error(equals, "Invalid assignment target.");
        }

        return expr;
    }

    private Expr equality() {
        Expr expr = this.comparison();

        while (this.match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            Token operator = this.previous();
            Expr right = this.comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr comparison() {
        Expr expr = this.term();

        while (this.match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            Token operator = this.previous();
            Expr right = this.term();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr term() {
        Expr expr = this.factor();

        while (this.match(TokenType.MINUS, TokenType.PLUS)) {
            Token operator = this.previous();
            Expr right = this.factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr factor() {
        Expr expr = this.unary();

        while (this.match(TokenType.SLASH, TokenType.STAR)) {
            Token operator = this.previous();
            Expr right = this.unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() {
        if (this.match(TokenType.BANG, TokenType.MINUS)) {
            Token operator = this.previous();
            Expr right = this.unary();
            return new Expr.Unary(operator, right);
        }

        return this.primary();
    }

    private Expr primary() {
        if (this.match(TokenType.FALSE)) {
            return new Expr.Literal(false);
        }

        if (this.match(TokenType.TRUE)) {
            return new Expr.Literal(true);
        }

        if (this.match(TokenType.NIL)) {
            return new Expr.Literal(null);
        }

        if (this.match(TokenType.NUMBER, TokenType.STRING)) {
            return new Expr.Literal(this.previous().literal());
        }

        if (this.match(TokenType.IDENTIFIER)) {
            return new Expr.Variable(this.previous());
        }

        if (this.match(TokenType.LEFT_PAREN)) {
            Expr expr = this.expression();
            this.consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        throw this.error(this.peek(), "Expect expression.");
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (this.check(type)) {
                this.advance();
                return true;
            }
        }
        return false;
    }

    private Token consume(TokenType type, String message) {
        if (this.check(type)) {
            return this.advance();
        }

        throw this.error(this.peek(), message);
    }

    private boolean check(TokenType type) {
        if (this.isAtEnd()) {
            return false;
        }
        return this.peek().type() == type;
    }

    private Token advance() {
        if (!this.isAtEnd()) {
            this.current++;
        }
        return this.previous();
    }

    private boolean isAtEnd() {
        return this.peek().type() == TokenType.EOF;
    }

    private Token peek() {
        return this.tokens.get(this.current);
    }

    private Token previous() {
        return this.tokens.get(this.current - 1);
    }

    private ParserError error(Token token, String message) {
        Lox.Error(token, message);
        return new ParserError();
    }

    private void synchronize() {
        this.advance();

        while (!this.isAtEnd()) {
            if (this.previous().type() == TokenType.SEMICOLON) {
                return;
            }

            switch (this.peek().type()) {
                case CLASS, FUN, VAR, FOR, IF, WHILE, PRINT, RETURN -> {
                    return;
                }
                default -> {
                }
            }

            this.advance();
        }
    }
}
