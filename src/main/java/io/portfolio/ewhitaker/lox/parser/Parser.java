package io.portfolio.ewhitaker.lox.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.portfolio.ewhitaker.lox.Position;
import io.portfolio.ewhitaker.lox.Source;
import io.portfolio.ewhitaker.lox.lexer.Lexer;
import io.portfolio.ewhitaker.lox.lexer.token.Token;
import io.portfolio.ewhitaker.lox.lexer.token.TokenType;
import io.portfolio.ewhitaker.lox.parser.ast.Expr;

public class Parser {
    public record Error(Position position, String message) {
    }

    public final Source source;
    public final Lexer lexer;

    public final List<Error> errors = new ArrayList<>();

    public Token tok = null;

    public int current = 0;

    public int indent = 0;

    public Parser(Source source) {
        this.source = Objects.requireNonNull(source);
        this.lexer = new Lexer(source, (Position position, String message) -> {
            this.errors.add(new Error(position, message));
        });
    }

    public Expr parse() {
        this.trace("parse");
        try {
            return this.expression();
        } finally {
            this.untrace("parse");
        }
    }

    public Expr expression() {
        this.trace("expression");
        try {
            return this.comma();
        } finally {
            this.untrace("expression");
        }
    }

    public Expr comma() {
        this.trace("comma");
        try {
            return this.binary(this::ternary, TokenType.COMMA);
        } finally {
            this.untrace("comma");
        }
    }

    public Expr ternary() {
        this.trace("ternary");
        try {
            Expr expr = this.equality();

            if (this.matches(TokenType.QUESTION)) {
                final Expr consequence = this.expression();
                this.expect(TokenType.COLON, "Expect ':' after expression.");
                expr = new Expr.Ternary(expr, consequence, this.ternary());
            }

            return expr;
        } finally {
            this.untrace("ternary");
        }
    }

    public Expr equality() {
        this.trace("equality");
        try {
            return this.binary(this::comparison, TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL);
        } finally {
            this.untrace("equality");
        }
    }

    public Expr comparison() {
        this.trace("comparison");
        try {
            return this.binary(
                    this::term,
                    TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL
            );
        } finally {
            this.untrace("comparison");
        }
    }

    public Expr term() {
        this.trace("term");
        try {
            return this.binary(this::factor, TokenType.MINUS, TokenType.PLUS);
        } finally {
            this.untrace("term");
        }
    }

    public Expr factor() {
        this.trace("factor");
        try {
            return this.binary(this::unary, TokenType.STAR, TokenType.SLASH);
        } finally {
            this.untrace("factor");
        }
    }

    @FunctionalInterface
    public interface BinaryRule {
        Expr parse();
    }

    public Expr binary(BinaryRule rule, TokenType... types) {
        Expr expr = rule.parse();

        while (this.matches(types)) {
            expr = new Expr.Binary(expr, this.tok, rule.parse());
        }

        return expr;
    }

    public Expr unary() {
        this.trace("unary");
        try {
            if (this.matches(TokenType.BANG, TokenType.MINUS)) {
                return new Expr.Unary(this.tok, this.unary());
            }

            return this.primary();
        } finally {
            this.untrace("unary");
        }
    }

    public Expr primary() {
        if (this.matches(TokenType.FALSE)) {
            return new Expr.Literal(this.tok, false);
        }

        if (this.matches(TokenType.TRUE)) {
            return new Expr.Literal(this.tok, true);
        }

        if (this.matches(TokenType.NIL)) {
            return new Expr.Literal(this.tok, null);
        }

        if (this.matches(TokenType.NUMBER)) {
            return new Expr.Literal(this.tok, Double.parseDouble(this.tok.lexeme()));
        }

        if (this.matches(TokenType.STRING)) {
            return new Expr.Literal(this.tok, this.tok.lexeme());
        }

        if (this.matches(TokenType.LEFT_PAREN)) {
            Expr expr = expression();
            this.expect(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            return expr;
        }

        if (this.matches(
                TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL,
                TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL,
                TokenType.PLUS,
                TokenType.STAR, TokenType.SLASH
        )) {
            return this.panic(this.tok, "Missing left-hand operand.", delimiters);
        }

        return this.panic(this.peek(), "Expect expression.", statements);
    }

    public boolean matches(TokenType... types) {
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
        return this.lexer.tokenAt(this.current++);
    }

    public Token peek() {
        return this.lexer.tokenAt(this.current);
    }

    public boolean eof() {
        return this.peek().type() == TokenType.EOF;
    }

    public void expect(TokenType type, String message) {
        this.tok = this.advance();
        if (this.tok.type() != type) {
            this.error(this.tok, message);
        }
    }

    // TODO: revisit
    public Expr panic(Token from, String message, TokenType... to) {
        this.error(from, message);
        this.synchronize(to);
        return new Expr.Illegal(from, this.tok);
    }

    public void error(Token token, String message) {
        this.errors.add(new Error(this.source.position(token.offset()), message));
    }

    public static final TokenType[] statements = new TokenType[] {
            TokenType.CLASS,
            TokenType.FUN,
            TokenType.VAR,
            TokenType.FOR,
            TokenType.IF,
            TokenType.WHILE,
            TokenType.PRINT,
            TokenType.RETURN,
    };

    public static final TokenType[] delimiters = new TokenType[] {
            TokenType.SEMICOLON,
            TokenType.COLON,
            TokenType.LEFT_PAREN,
            TokenType.RIGHT_PAREN,
            TokenType.COMMA,
    };

    public void synchronize(TokenType... types) {
        this.tok = this.advance();

        final Map<TokenType, Boolean> to = new HashMap<>();
        for (TokenType type : types) {
            to.put(type, true);
        }
        while (!this.eof()) {
            if (to.getOrDefault(this.tok.type(), false)) {
                return;
            }

            this.tok = this.advance();
        }
    }

    public void trace(String message) {
        if (this.source.trace) {
            System.out.println("   ".repeat(this.indent++) + "Begin " + message);
        }
    }

    public void untrace(String message) {
        if (this.source.trace) {
            System.out.println("   ".repeat(--this.indent) + "End " + message);
        }
    }
}
