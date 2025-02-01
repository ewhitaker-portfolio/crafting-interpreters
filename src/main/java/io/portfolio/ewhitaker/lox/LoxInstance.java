package io.portfolio.ewhitaker.lox;

import java.util.HashMap;
import java.util.Map;

public class LoxInstance {
    private LoxClass klass;
    private final Map<String, Object> fields = new HashMap<>();

    public LoxInstance(LoxClass klass) {
        this.klass = klass;
    }

    public Object Get(Token name) {
        if (this.fields.containsKey(name.lexeme())) {
            return this.fields.get(name.lexeme());
        }

        LoxFunction method = this.klass.FindMethod(name.lexeme());
        if (method != null) {
            return method.bind(this);
        }

        throw new RuntimeError(name, "Undefined property '" + name.lexeme() + "'.");
    }

    public void Set(Token name, Object value) {
        this.fields.put(name.lexeme(), value);
    }

    @Override
    public String toString() {
        return this.klass.Name + " instance";
    }
}
