package io.portfolio.ewhitaker.lox.printer;

import io.portfolio.ewhitaker.lox.Expr;
import io.portfolio.ewhitaker.lox.Token;
import io.portfolio.ewhitaker.lox.TokenType;

public class RPNPrinter implements Expr.Visitor<String> {
    public String print(Expr expression) {
        return expression.accept(this);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value().type() == TokenType.NIL) {
            return "nil";
        }
        return expr.value().lexeme();
    }

    @Override
    public String visitCommaExpr(Expr.Comma expr) {
        return notate(",", expr.left(), expr.right());
    }

    @Override
    public String visitTernaryExpr(Expr.Ternary expr) {
        return notate("?:", expr.condition(), expr.consequence(), expr.alternative());
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return notate(expr.operator().lexeme(), expr.left(), expr.right());
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return notate(expr.operator().lexeme(), expr.right());
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return expr.expression().accept(this);
    }

    @Override
    public String visitIllegalExpr(Expr.Illegal expr) {
        return "";
    }

    public String notate(String name, Expr... exprs) {
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
                        new Expr.Literal(new Token(TokenType.NUMBER, "123", 0))),
                new Token(TokenType.STAR, "*", 0),
                new Expr.Grouping(
                        new Expr.Literal(new Token(TokenType.NUMBER, "45.67", 0))));
        System.out.println(new RPNPrinter().print(expression));
    }
}
