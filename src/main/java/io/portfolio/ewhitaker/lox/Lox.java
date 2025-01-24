package io.portfolio.ewhitaker.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import io.portfolio.ewhitaker.Main;

public class Lox {
    public static final Interpreter interpreter = new Interpreter();

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

        run(new Source(new String(bytes, Charset.defaultCharset())));

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
            run(new Source(line));
            hadCompiletimeError = false;
        }

        return Main.EXIT_SUCCESS;
    }

    // TODO: interpreter should take a parser
    public static void run(Source source) {
        Parser parser = new Parser(source);
        Expr expression = parser.parse();

        // Stop if there was a syntax error.
        if (hadCompiletimeError) {
            return;
        }

        interpreter.interpret(source, expression);
    }

    public static void compiletimeError(Source source, Parser.Error error) {
        System.err.println("Error: " + error.message());
        final String prefix = error.position().line() + " | ";
        final String line;
        if (error.position().line() - 1 == source.lines.size() - 1) {
            line = source.input.substring(source.lines.get(error.position().line() - 1));
        } else {
            line = source.input.substring(
                    source.lines.get(error.position().line() - 1), source.lines.get(error.position().line()) - 1
            );
        }
        System.err.println("\t" + prefix + line);
        final String spaces = " ".repeat((error.position().column() - 1) + prefix.length());
        System.err.println("\t" + spaces + "^-- Here.");

        hadCompiletimeError = true;
    }

    // TODO: implement call stack
    public static void runtimeError(Source source, Interpreter.Error error) {
        System.err.println(error.getMessage() + "\n[line " + error.position.line() + "]");

        hadRuntimeError = true;
    }
}
