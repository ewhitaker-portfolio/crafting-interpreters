package io.portfolio.ewhitaker.lox;

import io.portfolio.ewhitaker.lox.Expr.Binary;
import io.portfolio.ewhitaker.lox.Expr.Illegal;
import io.portfolio.ewhitaker.lox.Expr.Literal;
import io.portfolio.ewhitaker.lox.Expr.Ternary;
import io.portfolio.ewhitaker.lox.Expr.Unary;

public class Interpreter implements Expr.Visitor<Object> {
    public static class Error extends RuntimeException {
        public final Source.Position position;
        public final String message;

        public Error(Source.Position position, String message) {
            super(message);
            this.position = position;
            this.message = message;
        }
    }

    public Source source;

    public void interpret(Source source, Expr expression) {
        this.source = source;
        try {
            Object value = this.evaluate(expression);
            System.out.println(stringify(value));
        } catch (Error error) {
            Lox.runtimeError(this.source, error);
        }
    }

    public Object evaluate(Expr expression) {
        return switch (expression) {
            case Expr.Literal expr -> this.visitLiteralExpr(expr);
            case Expr.Ternary expr -> this.visitTernaryExpr(expr);
            case Expr.Binary expr -> this.visitBinaryExpr(expr);
            case Expr.Unary expr -> this.visitUnaryExpr(expr);
            case Expr.Illegal expr -> this.visitIllegalExpr(expr);
        };
    }

    @Override
    public Object visitLiteralExpr(Literal expr) {
        return expr.value();
    }

    @Override
    public Object visitTernaryExpr(Ternary expr) {
        if (isTruthy(this.evaluate(expr.condition()))) {
            return this.evaluate(expr.consequence());
        }
        return this.evaluate(expr.alternative());
    }

    @Override
    public Object visitBinaryExpr(Binary expr) {
        Object left = this.evaluate(expr.left());
        Object right = this.evaluate(expr.right());

        final Token operator = expr.operator();
        return switch (operator.type()) {
            case BANG_EQUAL -> !isEqual(left, right);
            case EQUAL_EQUAL -> isEqual(left, right);
            case GREATER -> this.expectNumberOperand(operator, left) > this.expectNumberOperand(operator, right);
            case GREATER_EQUAL -> this.expectNumberOperand(operator, left) >= this.expectNumberOperand(operator, right);
            case LESS -> this.expectNumberOperand(operator, left) < this.expectNumberOperand(operator, right);
            case LESS_EQUAL -> this.expectNumberOperand(operator, left) <= this.expectNumberOperand(operator, right);
            case MINUS -> this.expectNumberOperand(operator, left) - this.expectNumberOperand(operator, right);
            case PLUS -> {
                if (left instanceof Double lDouble && right instanceof Double rDouble) {
                    yield lDouble + rDouble;
                }

                if (left instanceof String lString && right instanceof String rString) {
                    yield lString + rString;
                }

                throw new Error(
                        this.source.position(operator.offset()), "Operands must be two numbers or two strings."
                );
            }
            case SLASH -> this.expectNumberOperand(operator, left) / this.expectNumberOperand(operator, right);
            case STAR -> this.expectNumberOperand(operator, left) * this.expectNumberOperand(operator, right);
            default -> null; // Unreachable
        };
    }

    @Override
    public Object visitUnaryExpr(Unary expr) {
        Object right = this.evaluate(expr.right());

        final Token operator = expr.operator();
        return switch (operator.type()) {
            case BANG -> !isTruthy(right);
            case MINUS -> -expectNumberOperand(operator, right);
            default -> null; // Unreachable
        };
    }

    @Override
    public Object visitIllegalExpr(Illegal expr) {
        throw new Error(this.source.position(expr.from().offset()), "");
    }

    public static String stringify(Object object) {
        if (object == null) {
            return "nil";
        }

        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        return object.toString();
    }

    public Double expectNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double o) {
            return o;
        }
        throw new Error(this.source.position(operator.offset()), "Operand must be a number.");
    }

    public static boolean isTruthy(Object object) {
        return switch (object) {
            case null -> false;
            case String o -> o != "";
            case Boolean o -> o;
            case Double o -> o != 0;
            default -> true;
        };
    }

    public static boolean isEqual(Object left, Object right) {
        if (left == null) {
            return right == null;
        }
        return left.equals(right);
    }
}
