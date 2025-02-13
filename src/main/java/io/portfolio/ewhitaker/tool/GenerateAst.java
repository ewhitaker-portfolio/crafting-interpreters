package io.portfolio.ewhitaker.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import io.portfolio.ewhitaker.Main;

public class GenerateAst {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(Main.EXIT_USAGE);
        }
        String outputDir = args[0];
        defineAst(
                outputDir, "Expr", Arrays.asList(
                        "Assign   : Token name, Expr value",
                        "Binary   : Expr left, Token operator, Expr right",
                        "Call     : Expr callee, Token paren, List<Expr> arguments",
                        "Get      : Expr object, Token name",
                        "Grouping : Expr expression",
                        "Literal  : Object value",
                        "Logical  : Expr left, Token operator, Expr right",
                        "Set      : Expr object, Token name, Expr value",
                        "Super    : Token keyword, Token method",
                        "This     : Token keyword",
                        "Unary    : Token operator, Expr right",
                        "Variable : Token name"
                )
        );

        defineAst(
                outputDir, "Stmt", Arrays.asList(
                        "Block      : List<Stmt> statements",
                        "Class      : Token name, Expr.Variable superclass, List<Stmt.Function> methods",
                        "Expression : Expr expression",
                        "Function   : Token name, List<Token> params, List<Stmt> body",
                        "If         : Expr condition, Stmt thenBranch, Stmt elseBranch",
                        "Print      : Expr expression",
                        "Return     : Token keyword, Expr value",
                        "Var        : Token name, Expr initializer",
                        "While      : Expr condition, Stmt body"
                )
        );
    }

    private static void defineAst(String outputDir, String baseName, List<String> types) throws IOException {
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        writer.println("package io.portfolio.ewhitaker.lox;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.println("public interface " + baseName + " {");

        defineVisitor(writer, baseName, types);

        // The AST classes.
        writer.println();
        for (int i = 0; i < types.size(); ++i) {
            String type = types.get(i);
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
            if (i + 1 < types.size()) {
                writer.println();
            }
        }

        // The base accept() method.
        writer.println();
        writer.println("    <R> R accept(Visitor<R> visitor);");

        writer.println("}");
        writer.close();
    }

    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println("    public interface Visitor<R> {");

        for (int i = 0; i < types.size(); ++i) {
            String typeName = types.get(i).split(":")[0].trim();
            writer.println(
                    "        R visit" + typeName + baseName + "(" + typeName + " " + baseName.toLowerCase() + ");"
            );
            if (i + 1 < types.size()) {
                writer.println();
            }
        }

        writer.println("    }");
    }

    private static void defineType(PrintWriter writer, String baseName, String className, String fields) {
        writer.println("    public record " + className + "(" + fields + ") implements " + baseName + " {");

        // Visitor pattern.
        writer.println("        @Override");
        writer.println("        public <R> R accept(Visitor<R> visitor) {");
        writer.println("            return visitor.visit" + className + baseName + "(this);");
        writer.println("        }");

        writer.println("    }");
    }
}
