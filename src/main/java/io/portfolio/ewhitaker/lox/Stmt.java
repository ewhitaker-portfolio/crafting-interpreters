package io.portfolio.ewhitaker.lox;

import java.util.List;

public interface Stmt {
    public interface Visitor<R> {
        R VisitBlockStmt(Block stmt);

        R VisitClassStmt(Class stmt);

        R VisitExpressionStmt(Expression stmt);

        R VisitFunctionStmt(Function stmt);

        R VisitIfStmt(If stmt);

        R VisitPrintStmt(Print stmt);

        R VisitReturnStmt(Return stmt);

        R VisitVarStmt(Var stmt);

        R VisitWhileStmt(While stmt);
    }

    public record Block(List<Stmt> statements) implements Stmt {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.VisitBlockStmt(this);
        }
    }

    public record Class(Token name, Expr.Variable superclass, List<Stmt.Function> methods) implements Stmt {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.VisitClassStmt(this);
        }
    }

    public record Expression(Expr expression) implements Stmt {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.VisitExpressionStmt(this);
        }
    }

    public record Function(Token name, List<Token> params, List<Stmt> body) implements Stmt {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.VisitFunctionStmt(this);
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

    public record Return(Token keyword, Expr value) implements Stmt {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.VisitReturnStmt(this);
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
