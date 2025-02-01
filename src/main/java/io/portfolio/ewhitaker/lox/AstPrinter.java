package io.portfolio.ewhitaker.lox;

import java.util.List;

public class AstPrinter implements Expr.Visitor<String>, Stmt.Visitor<String> {
    public String Print(Stmt stmt) {
        return stmt.accept(this);
    }

    public String Print(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public String VisitBlockStmt(Stmt.Block stmt) {
        StringBuilder builder = new StringBuilder();
        builder.append("(block ");

        for (Stmt statement : stmt.statements()) {
            builder.append(statement.accept(this));
        }

        builder.append(")");
        return builder.toString();
    }

    @Override
    public String VisitClassStmt(Stmt.Class stmt) {
        StringBuilder builder = new StringBuilder();
        builder.append("(class " + stmt.name().lexeme());

        if (stmt.superclass() != null) {
            builder.append(" < " + this.Print(stmt.superclass()));
        }

        for (Stmt.Function method : stmt.methods()) {
            builder.append(" " + this.Print(method));
        }

        builder.append(")");
        return builder.toString();
    }

    @Override
    public String VisitExpressionStmt(Stmt.Expression stmt) {
        return this.parenthesize(";", stmt.expression());
    }

    @Override
    public String VisitFunctionStmt(Stmt.Function stmt) {
        StringBuilder builder = new StringBuilder();
        builder.append("(fun " + stmt.name().lexeme() + "(");

        for (Token param : stmt.params()) {
            if (param != stmt.params().get(0)) {
                builder.append(" ");
            }
            builder.append(param.lexeme());
        }

        builder.append(") ");

        for (Stmt body : stmt.body()) {
            builder.append(body.accept(this));
        }

        builder.append(")");
        return builder.toString();
    }

    @Override
    public String VisitIfStmt(Stmt.If stmt) {
        if (stmt.elseBranch() == null) {
            return this.parenthesize2("if", stmt.condition(), stmt.thenBranch());
        }

        return this.parenthesize2("if-else", stmt.condition(), stmt.thenBranch(), stmt.elseBranch());
    }

    @Override
    public String VisitPrintStmt(Stmt.Print stmt) {
        return this.parenthesize("print", stmt.expression());
    }

    @Override
    public String VisitReturnStmt(Stmt.Return stmt) {
        if (stmt.value() == null) {
            return "(return)";
        }
        return parenthesize("return", stmt.value());
    }

    @Override
    public String VisitVarStmt(Stmt.Var stmt) {
        if (stmt.initializer() == null) {
            return this.parenthesize2("var", stmt.name());
        }

        return this.parenthesize2("var", stmt.name(), "=", stmt.initializer());
    }

    @Override
    public String VisitWhileStmt(Stmt.While stmt) {
        return this.parenthesize2("while", stmt.condition(), stmt.body());
    }

    @Override
    public String VisitAssignExpr(Expr.Assign expr) {
        return this.parenthesize2("=", expr.name().lexeme(), expr.value());
    }

    @Override
    public String VisitBinaryExpr(Expr.Binary expr) {
        return this.parenthesize(expr.operator().lexeme(), expr.left(), expr.right());
    }

    @Override
    public String VisitCallExpr(Expr.Call expr) {
        return this.parenthesize2("call", expr.callee(), expr.arguments());
    }

    @Override
    public String VisitGetExpr(Expr.Get expr) {
        return this.parenthesize2(".", expr.object(), expr.name().lexeme());
    }

    @Override
    public String VisitGroupingExpr(Expr.Grouping expr) {
        return this.parenthesize("group", expr.expression());
    }

    @Override
    public String VisitLiteralExpr(Expr.Literal expr) {
        if (expr.value() == null) {
            return "nil";
        }
        return expr.value().toString();
    }

    @Override
    public String VisitLogicalExpr(Expr.Logical expr) {
        return this.parenthesize(expr.operator().lexeme(), expr.left(), expr.right());
    }

    @Override
    public String VisitSetExpr(Expr.Set expr) {
        return this.parenthesize2("=", expr.object(), expr.name().lexeme(), expr.value());
    }

    @Override
    public String VisitSuperExpr(Expr.Super expr) {
        return this.parenthesize2("super", expr.method());
    }

    @Override
    public String VisitThisExpr(Expr.This expr) {
        return "this";
    }

    @Override
    public String VisitUnaryExpr(Expr.Unary expr) {
        return this.parenthesize(expr.operator().lexeme(), expr.right());
    }

    @Override
    public String VisitVariableExpr(Expr.Variable expr) {
        return expr.name().lexeme();
    }

    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }

    private String parenthesize2(String name, Object... parts) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        this.transform(builder, parts);
        builder.append(")");

        return builder.toString();
    }

    private void transform(StringBuilder builder, Object... parts) {
        for (Object part : parts) {
            builder.append(" ");
            if (part instanceof Expr expr) {
                builder.append(expr.accept(this));
            } else if (part instanceof Stmt stmt) {
                builder.append(stmt.accept(this));
            } else if (part instanceof Token token) {
                builder.append(token.lexeme());
            } else if (part instanceof List list) {
                this.transform(builder, list.toArray());
            } else {
                builder.append(part);
            }
        }
    }

    public static void main(String[] args) {
        Expr expression = new Expr.Binary(
                new Expr.Unary(
                        new Token(TokenType.MINUS, "-", null, 1),
                        new Expr.Literal(123)
                ),
                new Token(TokenType.STAR, "*", null, 1),
                new Expr.Grouping(
                        new Expr.Literal(45.67)
                )
        );

        System.out.println(new AstPrinter().Print(expression));
    }
}
