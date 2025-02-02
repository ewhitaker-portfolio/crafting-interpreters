package io.portfolio.ewhitaker.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Evaluator implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    public Environment Globals = new Environment();
//@formatter:off Functions
//  private Environment environment = new Environment();
//@formatter:on
    private Environment environment = Globals;
    private final Map<Expr, Integer> locals = new HashMap<>();

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

    public Evaluator() {
        Globals.Define("clock", new LoxCallable() {
            @Override
            public int Arity() {
                return 0;
            }

            @Override
            public Object Call(Evaluator evaluator, List<Object> arguments) {
                return System.currentTimeMillis() / 1000.0;
            }

            @Override
            public String toString() {
                return "<native fn>";
            }
        });
    }

    public void Evaluate(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                this.evaluate(statement);
            }
        } catch (RuntimeError error) {
            Lox.RuntimeError(error);
        }
    }

    private void evaluate(Stmt stmt) {
        stmt.accept(this);
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    public void Resolve(Expr expr, int depth) {
        this.locals.put(expr, depth);
    }

    public void ExecuteBlock(List<Stmt> statements, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;

            for (Stmt statement : statements) {
                this.evaluate(statement);
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
    public Void VisitClassStmt(Stmt.Class stmt) {
        Object superclass = null;
        if (stmt.superclass() != null) {
            superclass = this.evaluate(stmt.superclass());
            if (!(superclass instanceof LoxClass)) {
                throw new RuntimeError(stmt.superclass().name(), "Superclass must be a class.");
            }
        }

        this.environment.Define(stmt.name().lexeme(), null);

        if (stmt.superclass() != null) {
            this.environment = new Environment(this.environment);
            this.environment.Define("super", superclass);
        }

        Map<String, LoxFunction> methods = new HashMap<>();
        for (Stmt.Function method : stmt.methods()) {
            LoxFunction function = new LoxFunction(method, this.environment, method.name().lexeme().equals("init"));
            methods.put(method.name().lexeme(), function);
        }

//@formatter:off Inheritance
//      LoxClass klass = new LoxClass(stmt.name().lexeme(), methods);
//@formatter:on
        LoxClass klass = new LoxClass(stmt.name().lexeme(), (LoxClass) superclass, methods);

        if (superclass != null) {
            this.environment = this.environment.Enclosing;
        }

        this.environment.Assign(stmt.name(), klass);
        return null;
    }

    @Override
    public Void VisitExpressionStmt(Stmt.Expression stmt) {
        this.evaluate(stmt.expression());
        return null;
    }

    @Override
    public Void VisitFunctionStmt(Stmt.Function stmt) {
//@formatter:off Classes
//      LoxFunction function = new LoxFunction(stmt, this.environment);
//@formatter:on
        LoxFunction function = new LoxFunction(stmt, this.environment, false);
        this.environment.Define(stmt.name().lexeme(), function);
        return null;
    }

    @Override
    public Void VisitIfStmt(Stmt.If stmt) {
        if (this.isTruthy(this.evaluate(stmt.condition()))) {
            this.evaluate(stmt.thenBranch());
        } else if (stmt.elseBranch() != null) {
            this.evaluate(stmt.elseBranch());
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
    public Void VisitReturnStmt(Stmt.Return stmt) {
        Object value = null;
        if (stmt.value() != null) {
            value = this.evaluate(stmt.value());
        }

        throw new Return(value);
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
            this.evaluate(stmt.body());
        }
        return null;
    }

    @Override
    public Object VisitAssignExpr(Expr.Assign expr) {
        Object value = this.evaluate(expr.value());
//@formatter:off Resolving and Binding
//      this.environment.Assign(expr.name(), value);
//@formatter:on

        Integer distance = this.locals.get(expr);
        if (distance != null) {
            this.environment.AssignAt(distance, expr.name(), value);
        } else {
            this.Globals.Assign(expr.name(), value);
        }

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
    public Object VisitCallExpr(Expr.Call expr) {
        Object callee = this.evaluate(expr.callee());

        List<Object> arguments = new ArrayList<>();
        for (Expr argument : expr.arguments()) {
            arguments.add(this.evaluate(argument));
        }

        if (!(callee instanceof LoxCallable function)) {
            throw new RuntimeError(expr.paren(), "Can only call functions and classes.");
        }

        if (arguments.size() != function.Arity()) {
            throw new RuntimeError(
                    expr.paren(), "Expected " + function.Arity() + "  arguments but got " + arguments.size() + "."
            );
        }

        return function.Call(this, arguments);
    }

    @Override
    public Object VisitGetExpr(Expr.Get expr) {
        Object object = this.evaluate(expr.object());
        if (object instanceof LoxInstance instance) {
            return instance.Get(expr.name());
        }

        throw new RuntimeError(expr.name(), "Only instances have properties.");
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
    public Object VisitSetExpr(Expr.Set expr) {
        Object object = this.evaluate(expr.object());

        if (!(object instanceof LoxInstance instance)) {
            throw new RuntimeError(expr.name(), "Only instances have fields.");
        }

        Object value = this.evaluate(expr.value());
        instance.Set(expr.name(), value);
        return value;
    }

    @Override
    public Object VisitSuperExpr(Expr.Super expr) {
        int distance = this.locals.get(expr);
        LoxClass superclass = (LoxClass) this.environment.GetAt(distance, "super");

        LoxInstance object = (LoxInstance) this.environment.GetAt(distance - 1, "this");

        LoxFunction method = superclass.FindMethod(expr.method().lexeme());

        if (method == null) {
            throw new RuntimeError(expr.method(), "Undefined property '" + expr.method().lexeme() + "'.");
        }

        return method.Bind(object);
    }

    @Override
    public Object VisitThisExpr(Expr.This expr) {
        return lookUpVariable(expr.keyword(), expr);
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
//@formatter:off Resolving and Binding
//      return this.environment.Get(expr.name());
//@formatter:on
        return lookUpVariable(expr.name(), expr);
    }

    private Object lookUpVariable(Token name, Expr expr) {
        Integer distance = this.locals.get(expr);
        if (distance != null) {
            return this.environment.GetAt(distance, name.lexeme());
        } else {
            return this.Globals.Get(name);
        }
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
