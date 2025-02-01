package io.portfolio.ewhitaker.lox;

public class Return extends RuntimeException {
    public final Object Value;

    public Return(Object Value) {
        super(null, null, false, false);
        this.Value = Value;
    }
}
