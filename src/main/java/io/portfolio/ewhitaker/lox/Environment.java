package io.portfolio.ewhitaker.lox;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    public final Environment Enclosing;
    private final Map<String, Object> values = new HashMap<>();

    public Environment() {
        this.Enclosing = null;
    }

    public Environment(Environment Enclosing) {
        this.Enclosing = Enclosing;
    }

    public Object Get(Token name) {
        if (this.values.containsKey(name.lexeme())) {
            return this.values.get(name.lexeme());
        }

        if (this.Enclosing != null) {
            return this.Enclosing.Get(name);
        }

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme() + "'.");
    }

    public void Assign(Token name, Object value) {
        if (this.values.containsKey(name.lexeme())) {
            this.values.put(name.lexeme(), value);
            return;
        }

        if (this.Enclosing != null) {
            this.Enclosing.Assign(name, value);
            return;
        }

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme() + "'.");
    }

    public void Define(String name, Object value) {
        this.values.put(name, value);
    }

    public Environment Ancestor(int distance) {
        Environment environment = this;
        for (int i = 0; i < distance; ++i) {
            environment = environment.Enclosing;
        }

        return environment;
    }

    public Object GetAt(int distance, String name) {
        return this.Ancestor(distance).values.get(name);
    }

    public void AssignAt(int distance, Token name, Object value) {
        this.Ancestor(distance).values.put(name.lexeme(), value);
    }
}
