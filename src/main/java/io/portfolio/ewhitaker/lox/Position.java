package io.portfolio.ewhitaker.lox;

public record Position(int offset, int line, int column, String info) {
    @Override
    public String toString() {
        return "Position" + "{" +
                "offset=" + this.offset + "," +
                "line=" + this.line + "," +
                "column=" + this.column +
                "}";
    }
}
