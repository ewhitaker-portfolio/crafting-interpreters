package io.portfolio.ewhitaker.lox;

import java.util.List;

public class LoxFunction implements LoxCallable {
    private final Stmt.Function declaration;
    private final Environment closure;

    private final boolean isInitializer;

    public LoxFunction(Stmt.Function declaration, Environment closure, boolean isInitializer) {
        this.isInitializer = isInitializer;
        this.closure = closure;
        this.declaration = declaration;
    }

    public LoxFunction Bind(LoxInstance instance) {
        Environment environment = new Environment(this.closure);
        environment.Define("this", instance);
        return new LoxFunction(declaration, environment, this.isInitializer);
    }

    @Override
    public int Arity() {
        return this.declaration.params().size();
    }

    @Override
    public Object Call(Evaluator evaluator, List<Object> arguments) {
        Environment environment = new Environment(this.closure);
        for (int i = 0; i < this.declaration.params().size(); ++i) {
            environment.Define(this.declaration.params().get(i).lexeme(), arguments.get(i));
        }

        try {
            evaluator.ExecuteBlock(this.declaration.body(), environment);
        } catch (Return returnValue) {
            if (this.isInitializer) {
                return this.closure.GetAt(0, "this");
            }
            return returnValue.Value;
        }

        if (this.isInitializer) {
            return this.closure.GetAt(0, "this");
        }

        return null;
    }

    @Override
    public String toString() {
        return "<fn " + this.declaration.name().lexeme() + ">";
    }
}
