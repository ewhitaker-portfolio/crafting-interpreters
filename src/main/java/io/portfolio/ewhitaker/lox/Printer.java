package io.portfolio.ewhitaker.lox;

import io.portfolio.ewhitaker.lox.Expr.Binary;
import io.portfolio.ewhitaker.lox.Expr.Grouping;
import io.portfolio.ewhitaker.lox.Expr.Literal;
import io.portfolio.ewhitaker.lox.Expr.Unary;

public class Printer implements Expr.Visitor<String> {

    public String print(Expr expression) {
        return expression.accept(this);
    }

    @Override
    public String visitBinaryExpr(Binary expr) {
        return parenthesize(expr.operator().lexeme(), expr.left(), expr.right());
    }

    @Override
    public String visitGroupingExpr(Grouping expr) {
        return parenthesize("group", expr.expression());
    }

    @Override
    public String visitLiteralExpr(Literal expr) {
        if (expr.value() == null) {
            return "nil";
        }
        return expr.value().toString();
    }

    @Override
    public String visitUnaryExpr(Unary expr) {
        return parenthesize(expr.operator().lexeme(), expr.right());
    }

    public String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

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

        System.out.println(new Printer().print(expression));
    }
}
