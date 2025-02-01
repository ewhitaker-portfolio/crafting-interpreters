package io.portfolio.ewhitaker.lox;

import java.beans.Encoder;
import java.util.List;

public class Evaluator implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    private Environment environment = new Environment();

//@formatter:off Statements and State
//  public void Evaluate(Expr expression) {
//      try {
//          Object value = this.evaluate(expression);
//          System.out.println(this.stringify(value));
//      } catch (RuntimeError error) {
//          Lox.RuntimeError(error);
//      }
//  }
//@formatter:on
    public void Evaluate(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                this.execute(statement);
            }
        } catch (RuntimeError error) {
            Lox.RuntimeError(error);
        }
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    public void ExecuteBlock(List<Stmt> statements, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;

            for (Stmt statement : statements) {
                this.execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }

    @Override
    public Void VisitBlockStmt(Stmt.Block stmt) {
        this.ExecuteBlock(stmt.statements(), new Environment(this.environment));
        return null;
    }

    @Override
    public Void VisitExpressionStmt(Stmt.Expression stmt) {
        this.evaluate(stmt.expression());
        return null;
    }

    @Override
    public Void VisitIfStmt(Stmt.If stmt) {
        if (this.isTruthy(this.evaluate(stmt.condition()))) {
            this.execute(stmt.thenBranch());
        } else if (stmt.elseBranch() != null) {
            this.execute(stmt.elseBranch());
        }
        return null;
    }

    @Override
    public Void VisitPrintStmt(Stmt.Print stmt) {
        Object value = this.evaluate(stmt.expression());
        System.out.println(this.stringify(value));
        return null;
    }

    @Override
    public Void VisitVarStmt(Stmt.Var stmt) {
        Object value = null;
        if (stmt.initializer() != null) {
            value = this.evaluate(stmt.initializer());
        }

        this.environment.Define(stmt.name().lexeme(), value);
        return null;
    }

    @Override
    public Void VisitWhileStmt(Stmt.While stmt) {
        while (this.isTruthy(this.evaluate(stmt.condition()))) {
            this.execute(stmt.body());
        }
        return null;
    }

    @Override
    public Object VisitAssignExpr(Expr.Assign expr) {
        Object value = this.evaluate(expr.value());
        this.environment.Assign(expr.name(), value);
        return value;
    }

    @Override
    public Object VisitBinaryExpr(Expr.Binary expr) {
        Object left = this.evaluate(expr.left());
        Object right = this.evaluate(expr.right());

        return switch (expr.operator().type()) {
            case BANG_EQUAL -> !isEqual(left, right);
            case EQUAL_EQUAL -> isEqual(left, right);
            case GREATER -> {
                checkNumberOperands(expr.operator(), left, right);
                yield (double) left > (double) right;
            }
            case GREATER_EQUAL -> {
                checkNumberOperands(expr.operator(), left, right);
                yield (double) left >= (double) right;
            }
            case LESS -> {
                checkNumberOperands(expr.operator(), left, right);
                yield (double) left < (double) right;
            }
            case LESS_EQUAL -> {
                checkNumberOperands(expr.operator(), left, right);
                yield (double) left <= (double) right;
            }
            case MINUS -> {
                checkNumberOperands(expr.operator(), left, right);
                yield (double) left - (double) right;
            }
            case PLUS -> {
                if (left instanceof Double l && right instanceof Double r) {
                    yield l + r;
                }

                if (left instanceof String l && right instanceof String r) {
                    yield l + r;
                }

                throw new RuntimeError(expr.operator(), "Operands must be two numbers or two strings.");
            }
            case SLASH -> {
                checkNumberOperands(expr.operator(), left, right);
                yield (double) left / (double) right;
            }
            case STAR -> {
                checkNumberOperands(expr.operator(), left, right);
                yield (double) left * (double) right;
            }
            default -> null; // Unreachable.
        };
    }

    @Override
    public Object VisitGroupingExpr(Expr.Grouping expr) {
        return this.evaluate(expr.expression());
    }

    @Override
    public Object VisitLiteralExpr(Expr.Literal expr) {
        return expr.value();
    }

    @Override
    public Object VisitLogicalExpr(Expr.Logical expr) {
        Object left = this.evaluate(expr.left());

        if (expr.operator().type() == TokenType.OR) {
            if (this.isTruthy(left)) {
                return left;
            }
        } else {
            if (!this.isTruthy(left)) {
                return left;
            }
        }

        return this.evaluate(expr.right());
    }

    @Override
    public Object VisitUnaryExpr(Expr.Unary expr) {
        Object right = this.evaluate(expr.right());

        return switch (expr.operator().type()) {
            case BANG -> !this.isTruthy(right);
            case MINUS -> {
                checkNumberOperand(expr.operator(), right);
                yield -((double) right);
            }
            default -> null; // Unreachable.
        };
    }

    @Override
    public Object VisitVariableExpr(Expr.Variable expr) {
        return this.environment.Get(expr.name());
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) {
            return;
        }

        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) {
            return;
        }

        throw new RuntimeError(operator, "Operands must be a numbers.");
    }

    private boolean isTruthy(Object object) {
        if (object == null) {
            return false;
        }

        if (object instanceof Boolean) {
            return (boolean) object;
        }

        return true;
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) {
            return true;
        }

        if (a == null) {
            return false;
        }

        return a.equals(b);
    }

    private String stringify(Object object) {
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
}
