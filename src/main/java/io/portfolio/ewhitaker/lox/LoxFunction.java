package io.portfolio.ewhitaker.lox;

import java.util.List;

public class LoxFunction implements LoxCallable {
    private final Stmt.Function declaration;
    private final Environment closure;

    public LoxFunction(Stmt.Function declaration, Environment closure) {
        this.closure = closure;
        this.declaration = declaration;
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
            return returnValue.Value;
        }
        return null;
    }

    @Override
    public String toString() {
        return "<fn " + this.declaration.name().lexeme() + ">";
    }
}
