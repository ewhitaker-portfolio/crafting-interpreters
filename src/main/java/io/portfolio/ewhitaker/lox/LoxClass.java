package io.portfolio.ewhitaker.lox;

import java.util.List;
import java.util.Map;

public class LoxClass implements LoxCallable {
    public final LoxClass superclass;
    public final String name;
    public final Map<String, LoxFunction> methods;

    public LoxClass(String name, LoxClass superclass, Map<String, LoxFunction> methods) {
        this.superclass = superclass;
        this.name = name;
        this.methods = methods;
    }

    public LoxFunction findMethod(String name) {
        if (methods.containsKey(name)) {
            return methods.get(name);
        }

        if (this.superclass != null) {
            return this.superclass.findMethod(name);
        }

        return null;
    }

    @Override
    public int arity() {
        LoxFunction initializer = this.findMethod("init");
        if (initializer == null) {
            return 0;
        }
        return initializer.arity();
    }

    @Override
    public Object call(Evaluator evaluator, List<Object> arguments) {
        LoxInstance instance = new LoxInstance(this);
        LoxFunction initializer = this.findMethod("init");
        if (initializer != null) {
            initializer.bind(instance).call(evaluator, arguments);
        }
        return instance;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
