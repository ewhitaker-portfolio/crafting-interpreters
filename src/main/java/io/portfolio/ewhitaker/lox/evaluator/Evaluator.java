package io.portfolio.ewhitaker.lox.evaluator;

import io.portfolio.ewhitaker.lox.Lox;
import io.portfolio.ewhitaker.lox.Position;
import io.portfolio.ewhitaker.lox.Source;
import io.portfolio.ewhitaker.lox.lexer.token.Token;
import io.portfolio.ewhitaker.lox.parser.ast.Expr;

public class Evaluator {
    public static class RuntimeError extends RuntimeException {
        public final Position position;
        public final String message;

        public RuntimeError(Position position, String message) {
            this.position = position;
            this.message = message;
        }
    }

    public Source source;

    public void evaluate(Source source, Expr expression) {
        this.source = source;
        try {
            Object value = this.evaluate(expression);
            System.out.println(stringify(value));
        } catch (RuntimeError error) {
            Lox.runtimeError(this.source, error.position, error.message);
        }
    }

    public Object evaluate(Expr expression) {
        return switch (expression) {
            case Expr.Literal expr -> this.evaluateLiteralExpr(expr);
            case Expr.Ternary expr -> this.evaluateTernaryExpr(expr);
            case Expr.Binary expr -> this.evaluateBinaryExpr(expr);
            case Expr.Unary expr -> this.evaluateUnaryExpr(expr);
            case Expr.Illegal expr -> this.evaluateIllegalExpr(expr);
        };
    }

    public Object evaluateLiteralExpr(Expr.Literal expr) {
        return expr.value();
    }

    public Object evaluateTernaryExpr(Expr.Ternary expr) {
        if (isTruthy(this.evaluate(expr.left()))) {
            return this.evaluate(expr.middle());
        }
        return this.evaluate(expr.right());
    }

    public Object evaluateBinaryExpr(Expr.Binary expr) {
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

                if (left instanceof String || right instanceof String) {
                    yield stringify(left) + stringify(right);
                }

                throw new RuntimeError(
                        this.source.position(operator.offset()), "Operands must be two numbers or two strings."
                );
            }
            case SLASH -> this.expectNumberOperand(operator, left) / this.expectNumberOperand(operator, right);
            case STAR -> this.expectNumberOperand(operator, left) * this.expectNumberOperand(operator, right);
            default -> null; // Unreachable
        };
    }

    public Object evaluateUnaryExpr(Expr.Unary expr) {
        Object right = this.evaluate(expr.right());

        final Token operator = expr.operator();
        return switch (operator.type()) {
            case BANG -> !isTruthy(right);
            case MINUS -> -expectNumberOperand(operator, right);
            default -> null; // Unreachable
        };
    }

    public Object evaluateIllegalExpr(Expr.Illegal expr) {
        throw new RuntimeError(this.source.position(expr.from().offset()), "");
    }

    public Double expectNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double o) {
            return o;
        }
        throw new RuntimeError(this.source.position(operator.offset()), "Operand must be a number.");
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
