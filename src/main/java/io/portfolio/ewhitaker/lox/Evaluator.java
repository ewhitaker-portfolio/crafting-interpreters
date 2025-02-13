package io.portfolio.ewhitaker.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Evaluator implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    public Environment globals = new Environment();
    public Environment environment = globals;
    public final Map<Expr, Integer> locals = new HashMap<>();

    public Evaluator() {
        globals.define("clock", new LoxCallable() {
            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Evaluator evaluator, List<Object> arguments) {
                return System.currentTimeMillis() / 1000.0;
            }

            @Override
            public String toString() {
                return "<native fn>";
            }
        });
    }

    public void evaluate(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                this.evaluate(statement);
            }
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
    }

    public void evaluate(Stmt stmt) {
        stmt.accept(this);
    }

    public Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    public void resolve(Expr expr, int depth) {
        this.locals.put(expr, depth);
    }

    public void executeBlock(List<Stmt> statements, Environment environment) {
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
    public Void visitBlockStmt(Stmt.Block stmt) {
        this.executeBlock(stmt.statements(), new Environment(this.environment));
        return null;
    }

    @Override
    public Void visitClassStmt(Stmt.Class stmt) {
        Object superclass = null;
        if (stmt.superclass() != null) {
            superclass = this.evaluate(stmt.superclass());
            if (!(superclass instanceof LoxClass)) {
                throw new RuntimeError(stmt.superclass().name(), "Superclass must be a class.");
            }
        }

        this.environment.define(stmt.name().lexeme(), null);

        if (stmt.superclass() != null) {
            this.environment = new Environment(this.environment);
            this.environment.define("super", superclass);
        }

        Map<String, LoxFunction> methods = new HashMap<>();
        for (Stmt.Function method : stmt.methods()) {
            LoxFunction function = new LoxFunction(method, this.environment, method.name().lexeme().equals("init"));
            methods.put(method.name().lexeme(), function);
        }

        LoxClass klass = new LoxClass(stmt.name().lexeme(), (LoxClass) superclass, methods);

        if (superclass != null) {
            this.environment = this.environment.enclosing;
        }

        this.environment.assign(stmt.name(), klass);
        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        this.evaluate(stmt.expression());
        return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        LoxFunction function = new LoxFunction(stmt, this.environment, false);
        this.environment.define(stmt.name().lexeme(), function);
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        if (this.isTruthy(this.evaluate(stmt.condition()))) {
            this.evaluate(stmt.thenBranch());
        } else if (stmt.elseBranch() != null) {
            this.evaluate(stmt.elseBranch());
        }
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = this.evaluate(stmt.expression());
        System.out.println(this.stringify(value));
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        Object value = null;
        if (stmt.value() != null) {
            value = this.evaluate(stmt.value());
        }

        throw new Return(value);
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        Object value = null;
        if (stmt.initializer() != null) {
            value = this.evaluate(stmt.initializer());
        }

        this.environment.define(stmt.name().lexeme(), value);
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        while (this.isTruthy(this.evaluate(stmt.condition()))) {
            this.evaluate(stmt.body());
        }
        return null;
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = this.evaluate(expr.value());

        Integer distance = this.locals.get(expr);
        if (distance != null) {
            this.environment.assignAt(distance, expr.name(), value);
        } else {
            this.globals.assign(expr.name(), value);
        }

        return value;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
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
    public Object visitCallExpr(Expr.Call expr) {
        Object callee = this.evaluate(expr.callee());

        List<Object> arguments = new ArrayList<>();
        for (Expr argument : expr.arguments()) {
            arguments.add(this.evaluate(argument));
        }

        if (!(callee instanceof LoxCallable function)) {
            throw new RuntimeError(expr.paren(), "Can only call functions and classes.");
        }

        if (arguments.size() != function.arity()) {
            throw new RuntimeError(
                    expr.paren(), "Expected " + function.arity() + "  arguments but got " + arguments.size() + "."
            );
        }

        return function.call(this, arguments);
    }

    @Override
    public Object visitGetExpr(Expr.Get expr) {
        Object object = this.evaluate(expr.object());
        if (object instanceof LoxInstance instance) {
            return instance.get(expr.name());
        }

        throw new RuntimeError(expr.name(), "Only instances have properties.");
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return this.evaluate(expr.expression());
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value();
    }

    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
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
    public Object visitSetExpr(Expr.Set expr) {
        Object object = this.evaluate(expr.object());

        if (!(object instanceof LoxInstance instance)) {
            throw new RuntimeError(expr.name(), "Only instances have fields.");
        }

        Object value = this.evaluate(expr.value());
        instance.set(expr.name(), value);
        return value;
    }

    @Override
    public Object visitSuperExpr(Expr.Super expr) {
        int distance = this.locals.get(expr);
        LoxClass superclass = (LoxClass) this.environment.getAt(distance, "super");

        LoxInstance object = (LoxInstance) this.environment.getAt(distance - 1, "this");

        LoxFunction method = superclass.findMethod(expr.method().lexeme());

        if (method == null) {
            throw new RuntimeError(expr.method(), "Undefined property '" + expr.method().lexeme() + "'.");
        }

        return method.bind(object);
    }

    @Override
    public Object visitThisExpr(Expr.This expr) {
        return lookUpVariable(expr.keyword(), expr);
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
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
    public Object visitVariableExpr(Expr.Variable expr) {
        return lookUpVariable(expr.name(), expr);
    }

    public Object lookUpVariable(Token name, Expr expr) {
        Integer distance = this.locals.get(expr);
        if (distance != null) {
            return this.environment.getAt(distance, name.lexeme());
        } else {
            return this.globals.get(name);
        }
    }

    public void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) {
            return;
        }

        throw new RuntimeError(operator, "Operand must be a number.");
    }

    public void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) {
            return;
        }

        throw new RuntimeError(operator, "Operands must be a numbers.");
    }

    public boolean isTruthy(Object object) {
        if (object == null) {
            return false;
        }

        if (object instanceof Boolean) {
            return (boolean) object;
        }

        return true;
    }

    public boolean isEqual(Object a, Object b) {
        if (a == null && b == null) {
            return true;
        }

        if (a == null) {
            return false;
        }

        return a.equals(b);
    }

    public String stringify(Object object) {
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
