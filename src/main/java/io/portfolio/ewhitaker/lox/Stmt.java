package io.portfolio.ewhitaker.lox;

import java.util.List;

public interface Stmt {
    public interface Visitor<R> {
        R VisitBlockStmt(Block stmt);

        R VisitExpressionStmt(Expression stmt);

        R VisitIfStmt(If stmt);

        R VisitPrintStmt(Print stmt);

        R VisitVarStmt(Var stmt);

        R VisitWhileStmt(While stmt);
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

    public record If(Expr condition, Stmt thenBranch, Stmt elseBranch) implements Stmt {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.VisitIfStmt(this);
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

    public record While(Expr condition, Stmt body) implements Stmt {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.VisitWhileStmt(this);
        }
    }

    <R> R accept(Visitor<R> visitor);
}
