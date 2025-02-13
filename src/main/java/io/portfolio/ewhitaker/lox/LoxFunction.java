package io.portfolio.ewhitaker.lox;

import java.util.List;

public class LoxFunction implements LoxCallable {
    public final Stmt.Function declaration;
    public final Environment closure;

    public final boolean isInitializer;

    public LoxFunction(Stmt.Function declaration, Environment closure, boolean isInitializer) {
        this.isInitializer = isInitializer;
        this.closure = closure;
        this.declaration = declaration;
    }

    public LoxFunction bind(LoxInstance instance) {
        Environment environment = new Environment(this.closure);
        environment.define("this", instance);
        return new LoxFunction(declaration, environment, this.isInitializer);
    }

    @Override
    public int arity() {
        return this.declaration.params().size();
    }

    @Override
    public Object call(Evaluator evaluator, List<Object> arguments) {
        Environment environment = new Environment(this.closure);
        for (int i = 0; i < this.declaration.params().size(); ++i) {
            environment.define(this.declaration.params().get(i).lexeme(), arguments.get(i));
        }

        try {
            evaluator.executeBlock(this.declaration.body(), environment);
        } catch (Return returnValue) {
            if (this.isInitializer) {
                return this.closure.getAt(0, "this");
            }
            return returnValue.Value;
        }

        if (this.isInitializer) {
            return this.closure.getAt(0, "this");
        }

        return null;
    }

    @Override
    public String toString() {
        return "<fn " + this.declaration.name().lexeme() + ">";
    }
}
