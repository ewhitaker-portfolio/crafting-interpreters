package io.portfolio.ewhitaker.lox;

import java.util.List;

public interface LoxCallable {
    int Arity();

    Object Call(Evaluator evaluator, List<Object> arguments);
}
