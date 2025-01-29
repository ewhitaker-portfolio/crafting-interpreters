package io.portfolio.ewhitaker.lox.parser.ast;

import java.util.List;

import io.portfolio.ewhitaker.lox.lexer.token.Token;

public sealed interface Expr extends Node {
    record Literal(Token kind, Object value) implements Expr {
    }

    record Ternary(Expr left, Expr middle, Expr right) implements Expr {
    }

    record Binary(Expr left, Token operator, Expr right) implements Expr {
    }

    record Unary(Token operator, Expr right) implements Expr {
    }

    public record Illegal(Token from, Token to) implements Expr {
    }
}
