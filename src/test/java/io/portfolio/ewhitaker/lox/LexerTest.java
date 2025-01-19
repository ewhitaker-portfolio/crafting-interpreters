package io.portfolio.ewhitaker.lox;

import java.util.Objects;

public class LexerTest {
    public void test() {
        record Test(String input, Token[] expect, String message) {
        }
        Test[] tests = new Test[] {
                /* #0 */new Test("var five = 5.0;", new Token[] {
                        new Token(TokenType.VAR, "var", 0),
                        new Token(TokenType.IDENTIFIER, "five", 4),
                        new Token(TokenType.EQUAL, "=", 9),
                        new Token(TokenType.NUMBER, "5.0", 11),
                        new Token(TokenType.SEMICOLON, ";", 14),
                        new Token(TokenType.EOF, "", 15),
                }, ""),
                /* #1 */new Test("var ten = 10;", new Token[] {
                        new Token(TokenType.VAR, "var", 0),
                        new Token(TokenType.IDENTIFIER, "ten", 4),
                        new Token(TokenType.EQUAL, "=", 8),
                        new Token(TokenType.NUMBER, "10", 10),
                        new Token(TokenType.SEMICOLON, ";", 12),
                        new Token(TokenType.EOF, "", 13),
                }, ""),
                /* #2 */new Test("""
                        fun add(x, y) {
                            return x + y;
                        }""", new Token[] {
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
                }, ""),
                /* #3 */new Test("var result = add(five, ten);", new Token[] {
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
                }, ""),
                /* #4 */new Test("var negate = !result;", new Token[] {
                        new Token(TokenType.VAR, "var", 0),
                        new Token(TokenType.IDENTIFIER, "negate", 4),
                        new Token(TokenType.EQUAL, "=", 11),
                        new Token(TokenType.BANG, "!", 13),
                        new Token(TokenType.IDENTIFIER, "result", 14),
                        new Token(TokenType.SEMICOLON, ";", 20),
                        new Token(TokenType.EOF, "", 21),
                }, ""),
                /* #5 */new Test("""
                        var i = 0;
                        while (i < 10) {
                            print i;
                            i = i + 1;
                        }""", new Token[] {
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
                }, ""),
                /* #6 */new Test("""
                        for (var i = 10; i >= 0; i = i - 1) {
                            print i;
                        }""", new Token[] {
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
                }, ""),
                /* #7 */new Test("""
                        if (true) {
                            print "hello world";
                        }""", new Token[] {
                        new Token(TokenType.IF, "if", 0),
                        new Token(TokenType.LEFT_PAREN, "(", 3),
                        new Token(TokenType.TRUE, "true", 4),
                        new Token(TokenType.RIGHT_PAREN, ")", 8),
                        new Token(TokenType.LEFT_BRACE, "{", 10),
                        new Token(TokenType.PRINT, "print", 16),
                        new Token(TokenType.STRING, "hello world", 22),
                        new Token(TokenType.SEMICOLON, ";", 35),
                        new Token(TokenType.RIGHT_BRACE, "}", 37),
                        new Token(TokenType.EOF, "", 38),
                }, ""),
                /* #8 */new Test("", new Token[] {
                        new Token(TokenType.EOF, "", 0),
                }, ""),
                /* #9 */new Test("var unexpected %", new Token[] {
                        new Token(TokenType.VAR, "var", 0),
                        new Token(TokenType.IDENTIFIER, "unexpected", 4),
                        new Token(TokenType.ILLEGAL, "%", 15),
                        new Token(TokenType.EOF, "", 16),
                }, "Unexpected character."),
                /* #10 */new Test("var unterminated = \"missing", new Token[] {
                        new Token(TokenType.VAR, "var", 0),
                        new Token(TokenType.IDENTIFIER, "unterminated", 4),
                        new Token(TokenType.EQUAL, "=", 17),
                        new Token(TokenType.ILLEGAL, "\"missing", 19),
                        new Token(TokenType.EOF, "", 27),
                }, "Unterminated string."),
                /* #11 */new Test("""
                        // single-line comment
                        fun documented() {
                            return nil;
                        }""", new Token[] {
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
                }, ""),
                /* #12 */new Test("""
                        /*
                         * /* nested multi-line comment */
                         */
                        fun documented() {
                            return nil;
                        }""", new Token[] {
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
                }, ""),
        };

        for (int i = 0; i < tests.length; ++i) {
            Test test = tests[i];
            final int finalI = i;
            Lexer lexer = new Lexer(test.input, (int offset, String message) -> {
                Lox.report(test.input, offset, message);
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
