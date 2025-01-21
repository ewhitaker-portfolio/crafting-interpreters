package io.portfolio.ewhitaker.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import io.portfolio.ewhitaker.Main;
import io.portfolio.ewhitaker.lox.printer.Printer;

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

        run(new Source(new String(bytes, Charset.defaultCharset())));

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
            run(new Source(line));
            hadError = false;
        }

        return Main.EXIT_SUCCESS;
    }

    public static void run(Source source) {
        Parser parser = new Parser(source);
        Expr expression = parser.parse();

        // Stop if there was a syntax error.
        if (hadError) {
            return;
        }

        System.out.println(new Printer().print(expression));
    }

    public static void report(Source source, Source.Position position, String message) {
        System.err.println("Error: " + message);
        final String prefix = position.line() + " | ";
        final String line;
        if (position.line() - 1 == source.lines.size() - 1) {
            line = source.input.substring(source.lines.get(position.line() - 1));
        } else {
            line = source.input.substring(source.lines.get(position.line() - 1), source.lines.get(position.line()) - 1);
        }
        System.err.println("\t" + prefix + line);
        final String spaces = " ".repeat((position.column() - 1) + prefix.length());
        System.err.println("\t" + spaces + "^-- Here.");

        hadError = true;
    }
}
