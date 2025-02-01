package io.portfolio.ewhitaker.lox;

import java.util.List;

public interface Expr {
    public interface Visitor<R> {
        R VisitAssignExpr(Assign expr);

        R VisitBinaryExpr(Binary expr);

        R VisitGroupingExpr(Grouping expr);

        R VisitLiteralExpr(Literal expr);

        R VisitLogicalExpr(Logical expr);

        R VisitUnaryExpr(Unary expr);

        R VisitVariableExpr(Variable expr);
    }

    public record Assign(Token name, Expr value) implements Expr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.VisitAssignExpr(this);
        }
    }

    public record Binary(Expr left, Token operator, Expr right) implements Expr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.VisitBinaryExpr(this);
        }
    }

    public record Grouping(Expr expression) implements Expr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.VisitGroupingExpr(this);
        }
    }

    public record Literal(Object value) implements Expr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.VisitLiteralExpr(this);
        }
    }

    public record Logical(Expr left, Token operator, Expr right) implements Expr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.VisitLogicalExpr(this);
        }
    }

    public record Unary(Token operator, Expr right) implements Expr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.VisitUnaryExpr(this);
        }
    }

    public record Variable(Token name) implements Expr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.VisitVariableExpr(this);
        }
    }

    <R> R accept(Visitor<R> visitor);
}
