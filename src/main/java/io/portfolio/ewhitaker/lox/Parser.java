package io.portfolio.ewhitaker.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {
    public record Error(Source.Position position, String message) {
    }

    public final List<Error> errors;
    public final Lexer lexer;
    public final Source source;
    public final List<Token> tokens;

    public Token tok = null;

    public int current = 0;

    public Parser(Source source) {
        this.errors = new ArrayList<>();
        this.lexer = new Lexer(source, (Source.Position position, String message) -> {
            this.errors.add(new Error(position, message));
        });
        this.source = source;
        this.tokens = lexer.tokens();
    }

    public Expr parse() {
        return this.expression();
    }

    public Expr expression() {
        return this.comma();
    }

    public Expr comma() {
        return this.binary(this::ternary, TokenType.COMMA);
    }

    public Expr ternary() {
        Expr expr = this.equality();

        if (this.match(TokenType.QUESTION)) {
            Expr consequence = this.expression();
            this.expect(TokenType.COLON, "Expect ':' after expression.");
            expr = new Expr.Ternary(expr, consequence, this.ternary());
        }

        return expr;
    }

    public Expr equality() {
        return this.binary(this::comparison, TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL);
    }

    public Expr comparison() {
        return this.binary(
                this::term,
                TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL
        );
    }

    public Expr term() {
        return this.binary(this::factor, TokenType.MINUS, TokenType.PLUS);
    }

    public Expr factor() {
        return this.binary(this::unary, TokenType.STAR, TokenType.SLASH);
    }

    @FunctionalInterface
    public interface BinaryRule {
        Expr parse();
    }

    public Expr binary(BinaryRule rule, TokenType... types) {
        Expr expr = rule.parse();

        while (this.match(types)) {
            expr = new Expr.Binary(expr, this.tok, rule.parse());
        }

        return expr;
    }

    public Expr unary() {
        if (this.match(TokenType.BANG, TokenType.MINUS)) {
            return new Expr.Unary(this.tok, this.unary());
        }

        return this.primary();
    }

    public Expr primary() {
        if (this.match(TokenType.FALSE, TokenType.TRUE, TokenType.NIL, TokenType.NUMBER, TokenType.STRING)) {
            return new Expr.Literal(this.tok);
        }

        if (this.match(TokenType.LEFT_PAREN)) {
            Expr expr = expression();
            this.expect(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            return expr;
        }

        if (this.match(
                TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL, TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS,
                TokenType.LESS_EQUAL, TokenType.PLUS, TokenType.STAR, TokenType.SLASH
        )) {
            return this.error(
                    this.current - 1, "Expect left operand before binary operator '" + this.tok.lexeme() + "'",
                    delimiters
            );
        }

        return this.error(this.current, "Expect expression.", statements);
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

    public boolean eof() {
        return this.peek().type() == TokenType.EOF;
    }

    public void expect(TokenType type, String message) {
        if (!this.check(type)) {
            this.tok = this.advance();
        }

        this.error(this.current, message, statements);
    }

    public Expr error(int offset, String message, Map<TokenType, Boolean> sync) {
        final Token from = this.tokens.get(offset);
        this.errors.add(new Error(this.source.position(from.offset()), message));
        this.synchronize(sync);
        return new Expr.Illegal(from, this.tok);
    }

    public static final Map<TokenType, Boolean> statements = new HashMap<>();

    static {
        statements.put(TokenType.CLASS, true);
        statements.put(TokenType.FUN, true);
        statements.put(TokenType.VAR, true);
        statements.put(TokenType.FOR, true);
        statements.put(TokenType.IF, true);
        statements.put(TokenType.WHILE, true);
        statements.put(TokenType.PRINT, true);
        statements.put(TokenType.RETURN, true);
    }

    public static final Map<TokenType, Boolean> delimiters = new HashMap<>();

    static {
        delimiters.put(TokenType.SEMICOLON, true);
        delimiters.put(TokenType.LEFT_PAREN, true);
        delimiters.put(TokenType.RIGHT_PAREN, true);
        delimiters.put(TokenType.COMMA, true);
    }

    public void synchronize(Map<TokenType, Boolean> to) {
        this.tok = this.advance();

        while (!this.eof()) {
            if (to.get(this.tok.type())) {
                return;
            }

            this.tok = this.advance();
        }
    }
}
