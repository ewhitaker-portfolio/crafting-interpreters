package io.portfolio.ewhitaker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    static void main(String[] args) throws Exception {
        if (args.length > 2) {
            System.out.println("Usage: java -jar crafting-interpreters.jar [script | --generate <directory>]");
            System.exit(EXIT_USAGE_FAILURE);
        }

        if (args.length == 2) {
            System.exit(GenerateAst.defineAst(args[1], "Expr", GenerateAst.types));
        }

        System.exit(Lox.start(args));
    }
}
