package io.portfolio.ewhitaker;

import io.portfolio.ewhitaker.lox.Lox;

//TODO: remove built-in print and add core lib
//TODO: bitwise operators, modulo operator, conditional operator
//TODO: do-while loop, foreach loop
//TODO: implement prototypes instead of classes, object literals
//TODO: new keyword for object instantiation
//TODO: better error handling, single error per line

public interface Main {
    static int EXIT_USAGE_FAILURE = 64;
    static int EXIT_INCORRECT_DATA_FAILURE = 65;

    static void main(String[] args) throws Exception {
        if (args.length > 1) {
            System.out.println("Usage: java -jar jlox.jar [script]");
            System.exit(EXIT_USAGE_FAILURE);
        } else if (args.length == 1) {
            Lox.runFile(args[0]);
        } else {
            Lox.runPrompt();
        }
    }
}
