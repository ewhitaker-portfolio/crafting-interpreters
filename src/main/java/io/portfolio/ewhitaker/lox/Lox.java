package io.portfolio.ewhitaker.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import io.portfolio.ewhitaker.Main;

public class Lox {
    private static final Evaluator evaluator = new Evaluator();

    public static boolean HadError = false;
    public static boolean HadRuntimeError = false;

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(Main.EXIT_USAGE);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        // Indicate an error in the exit code.
        if (HadError) {
            System.exit(Main.EXIT_DATA_ERROR);
        }

        if (HadRuntimeError) {
            System.exit(Main.EXIT_SOFTWARE);
        }
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            run(line);
            HadError = false;
        }
    }

    private static void run(String source) {
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.ScanTokens();
//@formatter:off Parsing Expressions
//
//      // For now, just print the tokens.
//      for (Token token : tokens) {
//          System.out.println(token);
//      }
//@formatter:on
        Parser parser = new Parser(tokens);
//@formatter:off Statements and State
//      Expr expression = parser.parse();
//@formatter:on
        List<Stmt> statements = parser.parse();

        // Stop if there was a syntax error.
        if (HadError) {
            return;
        }

//@formatter:off Evaluating Expressions
//      System.out.println(new AstPrinter().print(expression));
//@formatter:on

//@formatter:off Statements and State
//      evaluator.Evaluate(expression);
//@formatter:on
        evaluator.Evaluate(statements);
    }

    public static void Error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        HadError = true;
    }

    public static void Error(Token token, String message) {
        if (token.type() == TokenType.EOF) {
            report(token.line(), " at end", message);
        } else {
            report(token.line(), " at '" + token.lexeme() + "'", message);
        }
    }

    public static void RuntimeError(RuntimeError error) {
        System.err.println(error.getMessage() + "\n[line " + error.token.line() + "]");
        HadRuntimeError = true;
    }
}
