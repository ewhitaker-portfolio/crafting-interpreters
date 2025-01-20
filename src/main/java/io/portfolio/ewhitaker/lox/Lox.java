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
    public static boolean hadError = false;

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
        if (hadError) {
            return Main.EXIT_DATA_FAILURE;
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
            hadError = false;
        }

        return Main.EXIT_SUCCESS;
    }

    public static void run(String source) {
        Lexer lexer = new Lexer(source, (Position position, String message) -> {
            report(source, position, message);
        });
        List<Token> tokens = lexer.tokens();

        // For now, just print the tokens.
        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    public static void report(String source, Position position, String message) {
        System.err.println("Error: " + message);
        final String prefix = position.line() + " | ";
        System.err.println("\t" + prefix + position.info());
        final String spaces = " ".repeat((position.column() - 1) + prefix.length());
        System.err.println("\t" + spaces + "^-- Here.");

        hadError = true;
    }
}
