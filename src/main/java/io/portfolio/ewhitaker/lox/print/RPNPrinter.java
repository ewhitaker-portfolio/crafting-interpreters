package io.portfolio.ewhitaker.lox.print;

import io.portfolio.ewhitaker.lox.Expr;
import io.portfolio.ewhitaker.lox.Token;
import io.portfolio.ewhitaker.lox.TokenType;

public class RPNPrinter implements Expr.Visitor<String> {
    public String print(Expr expression) {
        return expression.accept(this);
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.operator().lexeme(), expr.left(), expr.right());
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return expr.expression().accept(this);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value() == null) {
            return "nil";
        }
        return expr.value().toString();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator().lexeme(), expr.right());
    }

    public String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        for (Expr expr : exprs) {
            builder.append(expr.accept(this));
            builder.append(" ");
        }
        builder.append(name);

        return builder.toString();
    }

    public static void main(String[] args) {
        Expr expression = new Expr.Binary(
                new Expr.Unary(
                        new Token(TokenType.MINUS, "-", 0),
                        new Expr.Literal(123)),
                new Token(TokenType.STAR, "*", 0),
                new Expr.Grouping(
                        new Expr.Literal(45.67)));

        System.out.println(new RPNPrinter().print(expression));
    }
}
