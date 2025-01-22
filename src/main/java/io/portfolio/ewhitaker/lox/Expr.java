package io.portfolio.ewhitaker.lox;

import java.util.List;

public sealed interface Expr extends Node {
    public interface Visitor<R> {
        R visitLiteralExpr(Literal expr);

        R visitCommaExpr(Comma expr);

        R visitTernaryExpr(Ternary expr);

        R visitBinaryExpr(Binary expr);

        R visitUnaryExpr(Unary expr);

        R visitGroupingExpr(Grouping expr);

        R visitIllegalExpr(Illegal expr);
    }

    record Literal(Token value) implements Expr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }
    }

    record Comma(Expr left, Expr right) implements Expr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitCommaExpr(this);
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

    record Grouping(Expr expression) implements Expr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
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
