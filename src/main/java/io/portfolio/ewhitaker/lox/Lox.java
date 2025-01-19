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

    public static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        // Indicate an error in the exit code.
        if (hadError) {
            System.exit(Main.EXIT_INCORRECT_DATA_FAILURE);
        }
    }

    public static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            run(line);
            hadError = false;
        }
    }

    public static void run(String source) {
        Lexer lexer = new Lexer(source, (int offset, String message) -> {
            report(source, offset, message);
        });
        List<Token> tokens = lexer.tokens();

        // For now, just print the tokens.
        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    public static void report(String source, int offset, String message) {
        System.err.println("Error: " + message);
        System.err.println(format(source, offset));

        hadError = true;
    }

    public static String format(String source, int offset) {
        int nlNum = 1;
        int nlPos = 0;
        for (int i = 0; i < offset; ++i) {
            if (source.charAt(i) == '\n') {
                ++nlNum;
                nlPos = i;
            }
        }

        StringBuilder line = new StringBuilder();
        for (int i = nlPos; i < source.length(); ++i) {
            char c = source.charAt(i);
            if (c == '\n') {
                break;
            }
            line.append(c);
        }

        final String prefix = nlNum + " | ";
        final String spaces = " ".repeat((offset - nlPos) + prefix.length());
        return '\t' + prefix + line + '\n' + '\t' + spaces + "^-- Here.";
    }
}
