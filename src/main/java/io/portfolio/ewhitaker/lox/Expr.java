package io.portfolio.ewhitaker.lox;

import java.util.List;

public sealed interface Expr extends Node {
    public interface Visitor<R> {
        R visitLiteralExpr(Literal expr);

        R visitTernaryExpr(Ternary expr);

        R visitBinaryExpr(Binary expr);

        R visitUnaryExpr(Unary expr);

        R visitIllegalExpr(Illegal expr);
    }

    record Literal(Token token, Object value) implements Expr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }
    }

    record Ternary(Expr condition, Expr consequence, Expr alternative) implements Expr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitTernaryExpr(this);
        }
    }

    record Binary(Expr left, Token operator, Expr right) implements Expr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }
    }

    record Unary(Token operator, Expr right) implements Expr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }
    }

    record Illegal(Token from, Token to) implements Expr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitIllegalExpr(this);
        }
    }

    <R> R accept(Visitor<R> visitor);
}
