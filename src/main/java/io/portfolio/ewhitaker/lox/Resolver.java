package io.portfolio.ewhitaker.lox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {
    private enum FunctionType {
        NONE,
        FUNCTION,
        INITIALIZER,
        METHOD
    }

    private enum ClassType {
        NONE,
        CLASS
    }

    private final Evaluator evaluator;
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();
    private FunctionType currentFunction = FunctionType.NONE;
    private ClassType currentClass = ClassType.NONE;

    public Resolver(Evaluator evaluator) {
        this.evaluator = evaluator;
    }

    public void Resolve(List<Stmt> statements) {
        for (Stmt statement : statements) {
            this.resolve(statement);
        }
    }

    @Override
    public Void VisitBlockStmt(Stmt.Block stmt) {
        this.beginScope();
        this.Resolve(stmt.statements());
        this.endScope();
        return null;
    }

    @Override
    public Void VisitClassStmt(Stmt.Class stmt) {
        ClassType enclosingClass = this.currentClass;
        this.currentClass = ClassType.CLASS;

        this.declare(stmt.name());
        this.define(stmt.name());

        this.beginScope();
        this.scopes.peek().put("this", true);

        for (Stmt.Function method : stmt.methods()) {
            FunctionType declaration = FunctionType.METHOD;
            if (method.name().lexeme().equals("init")) {
                declaration = FunctionType.INITIALIZER;
            }
            this.resolveFunction(method, declaration);
        }

        this.endScope();

        this.currentClass = enclosingClass;
        return null;
    }

    @Override
    public Void VisitExpressionStmt(Stmt.Expression stmt) {
        this.resolve(stmt.expression());
        return null;
    }

    @Override
    public Void VisitFunctionStmt(Stmt.Function stmt) {
        this.declare(stmt.name());
        this.define(stmt.name());

        this.resolveFunction(stmt, FunctionType.FUNCTION);
        return null;
    }

    @Override
    public Void VisitIfStmt(Stmt.If stmt) {
        this.resolve(stmt.condition());
        this.resolve(stmt.thenBranch());
        if (stmt.elseBranch() != null) {
            this.resolve(stmt.elseBranch());
        }
        return null;
    }

    @Override
    public Void VisitPrintStmt(Stmt.Print stmt) {
        this.resolve(stmt.expression());
        return null;
    }

    @Override
    public Void VisitReturnStmt(Stmt.Return stmt) {
        if (this.currentFunction == FunctionType.NONE) {
            Lox.Error(stmt.keyword(), "Can't return from top-level code.");
        }

        if (stmt.value() != null) {
            if (this.currentFunction == FunctionType.INITIALIZER) {
                Lox.Error(stmt.keyword(), "Can't return a value from an initializer.");
            }
            this.resolve(stmt.value());
        }

        return null;
    }

    @Override
    public Void VisitVarStmt(Stmt.Var stmt) {
        this.declare(stmt.name());
        if (stmt.initializer() != null) {
            this.resolve(stmt.initializer());
        }
        this.define(stmt.name());
        return null;
    }

    @Override
    public Void VisitWhileStmt(Stmt.While stmt) {
        this.resolve(stmt.condition());
        this.resolve(stmt.body());
        return null;
    }

    @Override
    public Void VisitAssignExpr(Expr.Assign expr) {
        this.resolve(expr.value());
        this.resolveLocal(expr, expr.name());
        return null;
    }

    @Override
    public Void VisitBinaryExpr(Expr.Binary expr) {
        this.resolve(expr.left());
        this.resolve(expr.right());
        return null;
    }

    @Override
    public Void VisitCallExpr(Expr.Call expr) {
        this.resolve(expr.callee());

        for (Expr argument : expr.arguments()) {
            this.resolve(argument);
        }

        return null;
    }

    @Override
    public Void VisitGetExpr(Expr.Get expr) {
        this.resolve(expr.object());
        return null;
    }

    @Override
    public Void VisitGroupingExpr(Expr.Grouping expr) {
        this.resolve(expr.expression());
        return null;
    }

    @Override
    public Void VisitLiteralExpr(Expr.Literal expr) {
        return null;
    }

    @Override
    public Void VisitLogicalExpr(Expr.Logical expr) {
        this.resolve(expr.left());
        this.resolve(expr.right());
        return null;
    }

    @Override
    public Void VisitSetExpr(Expr.Set expr) {
        this.resolve(expr.value());
        this.resolve(expr.object());
        return null;
    }

    @Override
    public Void VisitThisExpr(Expr.This expr) {
        if (this.currentClass == ClassType.NONE) {
            Lox.Error(expr.keyword(), "Can't use 'this' outside of a class.");
            return null;
        }

        this.resolveLocal(expr, expr.keyword());
        return null;
    }

    @Override
    public Void VisitUnaryExpr(Expr.Unary expr) {
        this.resolve(expr.right());
        return null;
    }

    @Override
    public Void VisitVariableExpr(Expr.Variable expr) {
        if (!this.scopes.isEmpty() && this.scopes.peek().get(expr.name().lexeme()) == Boolean.FALSE) {
            Lox.Error(expr.name(), "Can't read local variable in its own initializer.");
        }

        this.resolveLocal(expr, expr.name());
        return null;
    }

    private void resolve(Stmt stmt) {
        stmt.accept(this);
    }

    private void resolve(Expr expr) {
        expr.accept(this);
    }

    private void resolveFunction(Stmt.Function function, FunctionType type) {
        FunctionType enclosingFunction = this.currentFunction;
        currentFunction = type;

        this.beginScope();
        for (Token param : function.params()) {
            this.define(param);
            this.define(param);
        }
        this.Resolve(function.body());
        this.endScope();
        currentFunction = enclosingFunction;
    }

    private void beginScope() {
        this.scopes.push(new HashMap<>());
    }

    private void endScope() {
        this.scopes.pop();
    }

    private void declare(Token name) {
        if (this.scopes.isEmpty()) {
            return;
        }

        Map<String, Boolean> scope = this.scopes.peek();
        if (scope.containsKey(name.lexeme())) {
            Lox.Error(name, "Already a variable with this name in this scope.");
        }
        scope.put(name.lexeme(), false);
    }

    private void define(Token name) {
        if (this.scopes.isEmpty()) {
            return;
        }
        this.scopes.peek().put(name.lexeme(), true);
    }

    private void resolveLocal(Expr expr, Token name) {
        for (int i = this.scopes.size() - 1; i >= 0; --i) {
            if (this.scopes.get(i).containsKey(name.lexeme())) {
                this.evaluator.Resolve(expr, scopes.size() - 1 - i);
                return;
            }
        }
    }
}
