package io.portfolio.ewhitaker.lox.lexer;

import java.util.Objects;

import io.portfolio.ewhitaker.lox.Lox;
import io.portfolio.ewhitaker.lox.Position;
import io.portfolio.ewhitaker.lox.Source;
import io.portfolio.ewhitaker.lox.lexer.token.Token;
import io.portfolio.ewhitaker.lox.lexer.token.TokenType;
import io.portfolio.ewhitaker.lox.parser.Parser;

public class LexerTest {
    public void test() {
        record Test(String input, Token[] expect, String message) {
        }
        Test[] tests = new Test[] {
                /* #0 */
                new Test(
                        "var five = 5.0;",
                        new Token[] {
                                new Token(TokenType.VAR, "var", 0),
                                new Token(TokenType.IDENTIFIER, "five", 4),
                                new Token(TokenType.EQUAL, "=", 9),
                                new Token(TokenType.NUMBER, "5.0", 11),
                                new Token(TokenType.SEMICOLON, ";", 14),
                                new Token(TokenType.EOF, "", 15),
                        }, ""
                ),
                /* #1 */
                new Test(
                        "var ten = 10;",
                        new Token[] {
                                new Token(TokenType.VAR, "var", 0),
                                new Token(TokenType.IDENTIFIER, "ten", 4),
                                new Token(TokenType.EQUAL, "=", 8),
                                new Token(TokenType.NUMBER, "10", 10),
                                new Token(TokenType.SEMICOLON, ";", 12),
                                new Token(TokenType.EOF, "", 13),
                        }, ""
                ),
                /* #2 */
                new Test(
                        """
                                fun add(x, y) {
                                    return x + y;
                                }""",
                        new Token[] {
                                new Token(TokenType.FUN, "fun", 0),
                                new Token(TokenType.IDENTIFIER, "add", 4),
                                new Token(TokenType.LEFT_PAREN, "(", 7),
                                new Token(TokenType.IDENTIFIER, "x", 8),
                                new Token(TokenType.COMMA, ",", 9),
                                new Token(TokenType.IDENTIFIER, "y", 11),
                                new Token(TokenType.RIGHT_PAREN, ")", 12),
                                new Token(TokenType.LEFT_BRACE, "{", 14),
                                new Token(TokenType.RETURN, "return", 20),
                                new Token(TokenType.IDENTIFIER, "x", 27),
                                new Token(TokenType.PLUS, "+", 29),
                                new Token(TokenType.IDENTIFIER, "y", 31),
                                new Token(TokenType.SEMICOLON, ";", 32),
                                new Token(TokenType.RIGHT_BRACE, "}", 34),
                                new Token(TokenType.EOF, "", 35),
                        }, ""
                ),
                /* #3 */
                new Test(
                        """
                                fun subtract(x, y) {
                                    return x - y;
                                }""",
                        new Token[] {
                                new Token(TokenType.FUN, "fun", 0),
                                new Token(TokenType.IDENTIFIER, "subtract", 4),
                                new Token(TokenType.LEFT_PAREN, "(", 12),
                                new Token(TokenType.IDENTIFIER, "x", 13),
                                new Token(TokenType.COMMA, ",", 14),
                                new Token(TokenType.IDENTIFIER, "y", 16),
                                new Token(TokenType.RIGHT_PAREN, ")", 17),
                                new Token(TokenType.LEFT_BRACE, "{", 19),
                                new Token(TokenType.RETURN, "return", 25),
                                new Token(TokenType.IDENTIFIER, "x", 32),
                                new Token(TokenType.MINUS, "-", 34),
                                new Token(TokenType.IDENTIFIER, "y", 36),
                                new Token(TokenType.SEMICOLON, ";", 37),
                                new Token(TokenType.RIGHT_BRACE, "}", 39),
                                new Token(TokenType.EOF, "", 40),
                        }, ""
                ),
                /* #4 */
                new Test(
                        """
                                fun multiply(x, y) {
                                    return x * y;
                                }""",
                        new Token[] {
                                new Token(TokenType.FUN, "fun", 0),
                                new Token(TokenType.IDENTIFIER, "multiply", 4),
                                new Token(TokenType.LEFT_PAREN, "(", 12),
                                new Token(TokenType.IDENTIFIER, "x", 13),
                                new Token(TokenType.COMMA, ",", 14),
                                new Token(TokenType.IDENTIFIER, "y", 16),
                                new Token(TokenType.RIGHT_PAREN, ")", 17),
                                new Token(TokenType.LEFT_BRACE, "{", 19),
                                new Token(TokenType.RETURN, "return", 25),
                                new Token(TokenType.IDENTIFIER, "x", 32),
                                new Token(TokenType.STAR, "*", 34),
                                new Token(TokenType.IDENTIFIER, "y", 36),
                                new Token(TokenType.SEMICOLON, ";", 37),
                                new Token(TokenType.RIGHT_BRACE, "}", 39),
                                new Token(TokenType.EOF, "", 40),
                        }, ""
                ),
                /* #5 */
                new Test(
                        """
                                fun divide(x, y) {
                                    return x / y;
                                }""",
                        new Token[] {
                                new Token(TokenType.FUN, "fun", 0),
                                new Token(TokenType.IDENTIFIER, "divide", 4),
                                new Token(TokenType.LEFT_PAREN, "(", 10),
                                new Token(TokenType.IDENTIFIER, "x", 11),
                                new Token(TokenType.COMMA, ",", 12),
                                new Token(TokenType.IDENTIFIER, "y", 14),
                                new Token(TokenType.RIGHT_PAREN, ")", 15),
                                new Token(TokenType.LEFT_BRACE, "{", 17),
                                new Token(TokenType.RETURN, "return", 23),
                                new Token(TokenType.IDENTIFIER, "x", 30),
                                new Token(TokenType.SLASH, "/", 32),
                                new Token(TokenType.IDENTIFIER, "y", 34),
                                new Token(TokenType.SEMICOLON, ";", 35),
                                new Token(TokenType.RIGHT_BRACE, "}", 37),
                                new Token(TokenType.EOF, "", 38),
                        }, ""
                ),
                /* #6 */
                new Test(
                        "var result = add(five, ten);",
                        new Token[] {
                                new Token(TokenType.VAR, "var", 0),
                                new Token(TokenType.IDENTIFIER, "result", 4),
                                new Token(TokenType.EQUAL, "=", 11),
                                new Token(TokenType.IDENTIFIER, "add", 13),
                                new Token(TokenType.LEFT_PAREN, "(", 16),
                                new Token(TokenType.IDENTIFIER, "five", 17),
                                new Token(TokenType.COMMA, ",", 21),
                                new Token(TokenType.IDENTIFIER, "ten", 23),
                                new Token(TokenType.RIGHT_PAREN, ")", 26),
                                new Token(TokenType.SEMICOLON, ";", 27),
                                new Token(TokenType.EOF, "", 28),
                        }, ""
                ),
                /* #7 */
                new Test(
                        """
                                if (!result) {
                                    print true;
                                } else {
                                    print false;
                                }""",
                        new Token[] {
                                new Token(TokenType.IF, "if", 0),
                                new Token(TokenType.LEFT_PAREN, "(", 3),
                                new Token(TokenType.BANG, "!", 4),
                                new Token(TokenType.IDENTIFIER, "result", 5),
                                new Token(TokenType.RIGHT_PAREN, ")", 11),
                                new Token(TokenType.LEFT_BRACE, "{", 13),
                                new Token(TokenType.PRINT, "print", 19),
                                new Token(TokenType.TRUE, "true", 25),
                                new Token(TokenType.SEMICOLON, ";", 29),
                                new Token(TokenType.RIGHT_BRACE, "}", 31),
                                new Token(TokenType.ELSE, "else", 33),
                                new Token(TokenType.LEFT_BRACE, "{", 38),
                                new Token(TokenType.PRINT, "print", 44),
                                new Token(TokenType.FALSE, "false", 50),
                                new Token(TokenType.SEMICOLON, ";", 55),
                                new Token(TokenType.RIGHT_BRACE, "}", 57),
                                new Token(TokenType.EOF, "", 58),
                        }, ""
                ),
                /* #8 */
                new Test(
                        """
                                var i = 0;
                                while (i < 10) {
                                    print i;
                                    i = i + 1;
                                }""",
                        new Token[] {
                                new Token(TokenType.VAR, "var", 0),
                                new Token(TokenType.IDENTIFIER, "i", 4),
                                new Token(TokenType.EQUAL, "=", 6),
                                new Token(TokenType.NUMBER, "0", 8),
                                new Token(TokenType.SEMICOLON, ";", 9),
                                new Token(TokenType.WHILE, "while", 11),
                                new Token(TokenType.LEFT_PAREN, "(", 17),
                                new Token(TokenType.IDENTIFIER, "i", 18),
                                new Token(TokenType.LESS, "<", 20),
                                new Token(TokenType.NUMBER, "10", 22),
                                new Token(TokenType.RIGHT_PAREN, ")", 24),
                                new Token(TokenType.LEFT_BRACE, "{", 26),
                                new Token(TokenType.PRINT, "print", 32),
                                new Token(TokenType.IDENTIFIER, "i", 38),
                                new Token(TokenType.SEMICOLON, ";", 39),
                                new Token(TokenType.IDENTIFIER, "i", 45),
                                new Token(TokenType.EQUAL, "=", 47),
                                new Token(TokenType.IDENTIFIER, "i", 49),
                                new Token(TokenType.PLUS, "+", 51),
                                new Token(TokenType.NUMBER, "1", 53),
                                new Token(TokenType.SEMICOLON, ";", 54),
                                new Token(TokenType.RIGHT_BRACE, "}", 56),
                                new Token(TokenType.EOF, "", 57),
                        }, ""
                ),
                /* #9 */
                new Test(
                        """
                                for (var i = 10; i >= 0; i = i - 1) {
                                    print i;
                                }""",
                        new Token[] {
                                new Token(TokenType.FOR, "for", 0),
                                new Token(TokenType.LEFT_PAREN, "(", 4),
                                new Token(TokenType.VAR, "var", 5),
                                new Token(TokenType.IDENTIFIER, "i", 9),
                                new Token(TokenType.EQUAL, "=", 11),
                                new Token(TokenType.NUMBER, "10", 13),
                                new Token(TokenType.SEMICOLON, ";", 15),
                                new Token(TokenType.IDENTIFIER, "i", 17),
                                new Token(TokenType.GREATER_EQUAL, ">=", 19),
                                new Token(TokenType.NUMBER, "0", 22),
                                new Token(TokenType.SEMICOLON, ";", 23),
                                new Token(TokenType.IDENTIFIER, "i", 25),
                                new Token(TokenType.EQUAL, "=", 27),
                                new Token(TokenType.IDENTIFIER, "i", 29),
                                new Token(TokenType.MINUS, "-", 31),
                                new Token(TokenType.NUMBER, "1", 33),
                                new Token(TokenType.RIGHT_PAREN, ")", 34),
                                new Token(TokenType.LEFT_BRACE, "{", 36),
                                new Token(TokenType.PRINT, "print", 42),
                                new Token(TokenType.IDENTIFIER, "i", 48),
                                new Token(TokenType.SEMICOLON, ";", 49),
                                new Token(TokenType.RIGHT_BRACE, "}", 51),
                                new Token(TokenType.EOF, "", 52),
                        }, ""
                ),
                /* #10 */
                new Test(
                        "",
                        new Token[] {
                                new Token(TokenType.EOF, "", 0),
                        }, ""
                ),
                /* #11 */
                new Test(
                        "var unexpected %",
                        new Token[] {
                                new Token(TokenType.VAR, "var", 0),
                                new Token(TokenType.IDENTIFIER, "unexpected", 4),
                                new Token(TokenType.ILLEGAL, "%", 15),
                                new Token(TokenType.EOF, "", 16),
                        }, "Unexpected character."
                ),
                /* #12 */
                new Test(
                        """
                                // single-line comment
                                var unterminated = "missing""",
                        new Token[] {
                                new Token(TokenType.VAR, "var", 23),
                                new Token(TokenType.IDENTIFIER, "unterminated", 27),
                                new Token(TokenType.EQUAL, "=", 40),
                                new Token(TokenType.ILLEGAL, "\"missing", 42),
                                new Token(TokenType.EOF, "", 50),
                        }, "Unterminated string."
                ),
                /* #13 */
                new Test(
                        """
                                // single-line comment
                                fun documented() {
                                    return nil;
                                }""",
                        new Token[] {
                                new Token(TokenType.FUN, "fun", 23),
                                new Token(TokenType.IDENTIFIER, "documented", 27),
                                new Token(TokenType.LEFT_PAREN, "(", 37),
                                new Token(TokenType.RIGHT_PAREN, ")", 38),
                                new Token(TokenType.LEFT_BRACE, "{", 40),
                                new Token(TokenType.RETURN, "return", 46),
                                new Token(TokenType.NIL, "nil", 53),
                                new Token(TokenType.SEMICOLON, ";", 56),
                                new Token(TokenType.RIGHT_BRACE, "}", 58),
                                new Token(TokenType.EOF, "", 59),
                        }, ""
                ),
                /* #14 */
                new Test(
                        """
                                /*
                                 * /* nested multi-line comment */
                                 */
                                fun documented() {
                                    return nil;
                                }""",
                        new Token[] {
                                new Token(TokenType.FUN, "fun", 42),
                                new Token(TokenType.IDENTIFIER, "documented", 46),
                                new Token(TokenType.LEFT_PAREN, "(", 56),
                                new Token(TokenType.RIGHT_PAREN, ")", 57),
                                new Token(TokenType.LEFT_BRACE, "{", 59),
                                new Token(TokenType.RETURN, "return", 65),
                                new Token(TokenType.NIL, "nil", 72),
                                new Token(TokenType.SEMICOLON, ";", 75),
                                new Token(TokenType.RIGHT_BRACE, "}", 77),
                                new Token(TokenType.EOF, "", 78),
                        }, ""
                ),
        };

        for (int i = 0; i < tests.length; ++i) {
            Test test = tests[i];
            final int finalI = i;
            final Source source = new Source(test.input, false);
            Lexer lexer = new Lexer(source, (Position position, String message) -> {
                Lox.compiletimeError(source, new Parser.Error(position, message));
                if (test.message != message) {
                    throw new IllegalArgumentException(error(finalI, null, test.message + " != " + message));
                }
            });

            for (Token expect : test.expect) {
                Token actual = lexer.next();
                if (expect.type() != actual.type()) {
                    throw new IllegalArgumentException(error(i, expect, expect.type() + " != " + actual.type()));
                }

                if (!Objects.equals(expect.lexeme(), actual.lexeme())) {
                    throw new IllegalArgumentException(error(i, expect, expect.lexeme() + " != " + actual.lexeme()));
                }

                if (expect.offset() != actual.offset()) {
                    throw new IllegalArgumentException(error(i, expect, expect.offset() + " != " + actual.offset()));
                }
            }
        }
    }

    public static String error(int i, Token test, String message) {
        return "#" + i + " " + test + " ==> " + message;
    }
}
