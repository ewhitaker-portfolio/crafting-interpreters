package io.portfolio.ewhitaker.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import io.portfolio.ewhitaker.Main;
import io.portfolio.ewhitaker.lox.evaluator.Evaluator;
import io.portfolio.ewhitaker.lox.parser.Parser;
import io.portfolio.ewhitaker.lox.parser.ast.Expr;

public class Lox {
    public static final Evaluator evaluator = new Evaluator();

    public static boolean hadCompiletimeError = false;
    public static boolean hadRuntimeError = false;

    public static int start(String[] args) {
        if (args.length == 1) {
            return runFile(args[0]);
        }

        return runPrompt();
    }

    public static int runFile(String path) {
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace(System.err);
            return Main.EXIT_IO_FAILURE;
        }

        run(new String(bytes, Charset.defaultCharset()));

        // Indicate an error in the exit code.
        if (hadCompiletimeError) {
            return Main.EXIT_DATA_FAILURE;
        }

        if (hadRuntimeError) {
            return Main.EXIT_INTERNAL_FAILURE;
        }

        return Main.EXIT_SUCCESS;
    }

    public static int runPrompt() {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        String line;
        for (;;) {
            System.out.print("> ");
            try {
                line = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace(System.err);
                return Main.EXIT_IO_FAILURE;
            }

            if (line == null) {
                break;
            }
            run(line);
            hadCompiletimeError = false;
        }

        return Main.EXIT_SUCCESS;
    }

    public static void run(String input) {
        final Source source = new Source(input, false);
        final Parser parser = new Parser(source);
        final Expr expression = parser.parse();

        // Stop if there was a syntax error.
        if (hadCompiletimeError) {
            return;
        }

        evaluator.evaluate(source, expression);
    }

    public static void compiletimeError(Source source, Position position, String message) {
        System.err.println("Error: " + message);
        final String prefix = position.line() + " | ";
        final String line;
        if (position.line() - 1 == source.lines.size() - 1) {
            line = source.input.substring(source.lines.get(position.line() - 1));
        } else {
            line = source.input.substring(
                    source.lines.get(position.line() - 1), source.lines.get(position.line()) - 1
            );
        }
        System.err.println("\t" + prefix + line);
        final String spaces = " ".repeat((position.column() - 1) + prefix.length());
        System.err.println("\t" + spaces + "^-- Here.");

        hadCompiletimeError = true;
    }

    // TODO: implement call stack
    public static void runtimeError(Source source, Position position, String message) {
        System.err.println(message + "\n[line " + position.line() + "]");

        hadRuntimeError = true;
    }
}
