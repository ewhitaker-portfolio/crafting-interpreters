package io.portfolio.ewhitaker.lox.printer;

import io.portfolio.ewhitaker.lox.lexer.token.Token;
import io.portfolio.ewhitaker.lox.lexer.token.TokenType;
import io.portfolio.ewhitaker.lox.parser.ast.Expr;

public class Printer {

    public String print(Expr expression) {
        return switch (expression) {
            case Expr.Literal expr -> this.printLiteralExpr(expr);
            case Expr.Ternary expr -> this.printTernaryExpr(expr);
            case Expr.Binary expr -> this.printBinaryExpr(expr);
            case Expr.Unary expr -> this.printUnaryExpr(expr);
            case Expr.Illegal _ -> null;
        };
    }

    public String printLiteralExpr(Expr.Literal expr) {
        if (expr.kind().type() == TokenType.NIL) {
            return "nil";
        }
        return expr.kind().lexeme();
    }

    public String printTernaryExpr(Expr.Ternary expr) {
        return notate("?:", expr.left(), expr.middle(), expr.right());
    }

    public String printBinaryExpr(Expr.Binary expr) {
        return notate(expr.operator().lexeme(), expr.left(), expr.right());
    }

    public String printUnaryExpr(Expr.Unary expr) {
        return notate(expr.operator().lexeme(), expr.right());
    }

    public String notate(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(this.print(expr));
        }
        builder.append(")");

        return builder.toString();
    }

    public static void main(String[] args) {
        Expr expression = new Expr.Binary(
                new Expr.Unary(
                        new Token(TokenType.MINUS, "-", 0),
                        new Expr.Literal(new Token(TokenType.NUMBER, "123", 0), 123)
                ),
                new Token(TokenType.STAR, "*", 0),
                new Expr.Literal(new Token(TokenType.NUMBER, "45.67", 0), 45.67)
        );
        System.out.println(new Printer().print(expression));
    }
}
