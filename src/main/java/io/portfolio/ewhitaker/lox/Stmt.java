package io.portfolio.ewhitaker.lox;

import java.util.List;

public interface Stmt {
    public interface Visitor<R> {
        R VisitBlockStmt(Block stmt);

        R VisitExpressionStmt(Expression stmt);

        R VisitPrintStmt(Print stmt);

        R VisitVarStmt(Var stmt);
    }

    public record Block(List<Stmt> statements) implements Stmt {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.VisitBlockStmt(this);
        }
    }

    public record Expression(Expr expression) implements Stmt {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.VisitExpressionStmt(this);
        }
    }

    public record Print(Expr expression) implements Stmt {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.VisitPrintStmt(this);
        }
    }

    public record Var(Token name, Expr initializer) implements Stmt {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.VisitVarStmt(this);
        }
    }

    <R> R accept(Visitor<R> visitor);
}
