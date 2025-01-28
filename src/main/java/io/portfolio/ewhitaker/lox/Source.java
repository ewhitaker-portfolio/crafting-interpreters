package io.portfolio.ewhitaker.lox;

import java.util.ArrayList;
import java.util.List;

public class Source {
    public final String input;
    public final List<Integer> lines = new ArrayList<>();

    public final boolean trace;

    public Source(String input, boolean trace) {
        this.input = input;
        this.trace = trace;
        this.lines.add(0);
    }

    public Position position(int offset) {
        final int ln = search(this.lines, offset);
        final int col = offset - this.lines.get(ln - 1) + 1;
        return new Position(offset, ln, col);
    }

    // TODO: add test
    public static int search(List<Integer> lines, int offset) {
        int l = 0;
        int h = lines.size();
        while (l < h) {
            int i = l + ((l + h) >> 1);
            if (lines.get(i) <= offset) {
                l = i + 1;
            } else {
                h = i;
            }
        }
        return l;
    }
}
