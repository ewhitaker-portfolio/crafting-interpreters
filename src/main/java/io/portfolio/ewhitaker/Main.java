package io.portfolio.ewhitaker;

import io.portfolio.ewhitaker.lox.Lox;
import io.portfolio.ewhitaker.tool.GenerateAst;

//TODO: remove built-in print and add core lib
//TODO: bitwise operators, modulo operator, conditional operator
//TODO: do-while loop, foreach loop
//TODO: implement prototypes instead of classes, object literals
//TODO: new keyword for object instantiation
//TOOD: remove semicolon

public interface Main {
    static int EXIT_SUCCESS = 0;
    static int EXIT_USAGE_FAILURE = 64;
    static int EXIT_DATA_FAILURE = 65;
    static int EXIT_IO_FAILURE = 74;

    static void main(String[] args) {
        if (args.length > 2) {
            exitUsage();
        }

        if (args.length == 2) {
            if (!"generate".equals(args[0])) {
                exitUsage();
            }
            System.exit(GenerateAst.defineAst(args[1], "Expr", GenerateAst.expression));
        }

        System.exit(Lox.start(args));
    }

    static void exitUsage() {
        System.out.println("Usage: java -jar crafting-interpreters.jar [<script> | generate <directory>]");
        System.exit(EXIT_USAGE_FAILURE);
    }
}
