package io.portfolio.ewhitaker.lox;

import java.util.List;
import java.util.Map;

public class LoxClass implements LoxCallable {
    public final String Name;
    private final Map<String, LoxFunction> methods;

    public LoxClass(String Name, Map<String, LoxFunction> methods) {
        this.Name = Name;
        this.methods = methods;
    }

    public LoxFunction FindMethod(String name) {
        if (methods.containsKey(name)) {
            return methods.get(name);
        }

        return null;
    }

    @Override
    public int Arity() {
        LoxFunction initializer = this.FindMethod("init");
        if (initializer == null) {
            return 0;
        }
        return initializer.Arity();
    }

    @Override
    public Object Call(Evaluator evaluator, List<Object> arguments) {
        LoxInstance instance = new LoxInstance(this);
        LoxFunction initializer = this.FindMethod("init");
        if (initializer != null) {
            initializer.bind(instance).Call(evaluator, arguments);
        }
        return instance;
    }

    @Override
    public String toString() {
        return this.Name;
    }
}
