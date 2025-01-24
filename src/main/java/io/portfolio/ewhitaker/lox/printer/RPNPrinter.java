package io.portfolio.ewhitaker.lox.printer;

import io.portfolio.ewhitaker.lox.Expr;
import io.portfolio.ewhitaker.lox.Token;
import io.portfolio.ewhitaker.lox.TokenType;

public class RPNPrinter implements Expr.Visitor<String> {
    public String print(Expr expression) {
        return this.accept(expression);
    }

    public String accept(Expr expression) {
        return switch (expression) {
            case Expr.Literal expr -> this.visitLiteralExpr(expr);
            case Expr.Ternary expr -> this.visitTernaryExpr(expr);
            case Expr.Binary expr -> this.visitBinaryExpr(expr);
            case Expr.Unary expr -> this.visitUnaryExpr(expr);
            case Expr.Illegal expr -> this.visitIllegalExpr(expr);
        };
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value().type() == TokenType.NIL) {
            return "nil";
        }
        return expr.value().lexeme();
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
        if (expr.operator().type() == TokenType.MINUS) {
            return notate("~", expr.right());
        }
        return notate(expr.operator().lexeme(), expr.right());
    }

    @Override
    public String visitIllegalExpr(Expr.Illegal expr) {
        return "";
    }

    public String notate(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        for (Expr expr : exprs) {
            builder.append(this.accept(expr));
            builder.append(" ");
        }
        builder.append(name);

        return builder.toString();
    }

    public static void main(String[] args) {
        Expr expression = new Expr.Binary(
                new Expr.Unary(
                        new Token(TokenType.MINUS, "-", 0),
                        new Expr.Literal(new Token(TokenType.NUMBER, "123", 0))
                ),
                new Token(TokenType.STAR, "*", 0),
                new Expr.Literal(new Token(TokenType.NUMBER, "45.67", 0))
        );
        System.out.println(new RPNPrinter().print(expression));
    }
}
