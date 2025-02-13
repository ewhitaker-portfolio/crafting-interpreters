package io.portfolio.ewhitaker.lox;

import java.util.List;

public interface LoxCallable {
    int arity();

    Object call(Evaluator evaluator, List<Object> arguments);
}
