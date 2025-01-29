package io.portfolio.ewhitaker.lox.parser;

import io.portfolio.ewhitaker.lox.Source;
import io.portfolio.ewhitaker.lox.lexer.token.TokenType;
import io.portfolio.ewhitaker.lox.parser.ParserTest.ExprTest.BinaryTest;
import io.portfolio.ewhitaker.lox.parser.ParserTest.ExprTest.IllegalTest;
import io.portfolio.ewhitaker.lox.parser.ParserTest.ExprTest.LiteralTest;
import io.portfolio.ewhitaker.lox.parser.ParserTest.ExprTest.TernaryTest;
import io.portfolio.ewhitaker.lox.parser.ParserTest.ExprTest.UnaryTest;
import io.portfolio.ewhitaker.lox.parser.ast.Expr;
import io.portfolio.ewhitaker.util.Asserts;

public class ParserTest {
    public sealed interface ExprTest {
        record LiteralTest(TokenType type, Object value) implements ExprTest {
        }

        record TernaryTest(ExprTest left, ExprTest middle, ExprTest right) implements ExprTest {
        }

        record BinaryTest(ExprTest left, TokenType operator, ExprTest right) implements ExprTest {
        }

        record UnaryTest(TokenType operator, ExprTest right) implements ExprTest {
        }

        record IllegalTest() implements ExprTest {
        }
    }

    public void test() {
        record Test(String input, ExprTest expected) {
        }

        Test[] tests = new Test[] {
                new Test("5", new ExprTest.LiteralTest(TokenType.NUMBER, 5.0))
        };

        for (int i = 0; i < tests.length; ++i) {
            final Test test = tests[i];
            final Parser parser = new Parser(new Source(test.input, false));
            if (!testExpression(i, test.expected, parser.parse())) {
                continue;
            }
        }
    }

    public boolean testExpression(int index, ExprTest expected, Expr actual) {
        return switch (expected) {
            case LiteralTest test -> testLiteral(index, test, actual);
            case TernaryTest test -> testTernary(index, test, actual);
            case BinaryTest test -> testBinary(index, test, actual);
            case UnaryTest test -> testUnary(index, test, actual);
            case IllegalTest test -> testIllegal(index, test, actual);
        };
    }

    public boolean testLiteral(int index, LiteralTest test, Expr actual) {
        if (!(actual instanceof Expr.Literal lit)) {
            Asserts.fail(
                    "Test #" + index + " unexpected type. "
                            + "expected: " + Expr.Literal.class.getSimpleName()
                            + " actual: " + actual.getClass().getSimpleName()
            );
            return false;
        }

        switch (test.type) {
            case FALSE -> Asserts.isEqual(lit.value(), false, "Test #" + index);
            case TRUE -> Asserts.isEqual(lit.value(), true, "Test #" + index);
            case NIL -> Asserts.isEqual(lit.value(), null, "Test #" + index);
            case NUMBER, STRING -> Asserts.isEqual(lit.value(), test.value(), "Test #" + index);
            default -> Asserts.fail("Test #" + index + " unexpected type. actual: " + test.type);
        }

        return true;
    }

    public boolean testTernary(int index, TernaryTest test, Expr actual) {
        return false;
    }

    public boolean testBinary(int index, BinaryTest test, Expr actual) {
        return false;
    }

    public boolean testUnary(int index, UnaryTest test, Expr actual) {
        return false;
    }

    public boolean testIllegal(int index, IllegalTest test, Expr actual) {
        return false;
    }
}
