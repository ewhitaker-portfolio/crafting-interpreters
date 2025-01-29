package io.portfolio.ewhitaker.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.portfolio.ewhitaker.Main;

public class GenerateAst {
    public static final String working = System.getProperty("user.dir");

    public static final List<String> expression = new ArrayList<>();

    static {
        expression.add("Literal  : Token kind, Object value");
        expression.add("Ternary  : Expr left, Expr middle, Expr right");
        expression.add("Binary   : Expr left, Token operator, Expr right");
        expression.add("Unary    : Token operator, Expr right");
        expression.add("Illegal  : Token from, Token to");
    }

    public static int defineAst(String outputDir, String baseName, List<String> types) {
        String path = outputDir + "/" + baseName + ".java";
        if (path.charAt(0) != '/') {
            path = Paths.get(working).resolve(path).normalize().toString();
        }

        try (PrintWriter writer = new PrintWriter(path, Charset.defaultCharset())) {
            defineBaseAstNode(writer, path, baseName, types);
        } catch (IOException e) {
            e.printStackTrace(System.err);
            return Main.EXIT_IO_FAILURE;
        }

        return Main.EXIT_SUCCESS;
    }

    public static void defineBaseAstNode(PrintWriter writer, String path, String baseName, List<String> types) {
        String[] parts = path.split("/");
        StringBuilder packages = new StringBuilder();
        for (int i = parts.length - 2; i >= 0; --i) {
            if (Objects.equals(parts[i], "java")) {
                break;
            }
            packages.insert(0, parts[i] + ".");
        }
        packages.setCharAt(packages.length() - 1, ';');

        writer.println("package " + packages);
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.println("public sealed interface " + baseName + " extends Node {");

        defineVisitor(writer, baseName, types);

        // The AST classes.
        for (String type : types) {
            String[] split = type.split(":");
            defineAstNode(writer, baseName, split[0].trim(), split[1].trim());
            writer.println();
        }

        // The base accept() method.
        writer.println(indent("<R> R accept(Visitor<R> visitor);", 1));
        writer.println("}");
    }

    public static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println(indent("public interface Visitor<R> {", 1));

        for (String type : types) {
            String name = type.split(":")[0].trim();
            writer.println(indent("R visit" + name + baseName + "(" + name + " " + baseName.toLowerCase() + ");", 2));
            writer.println();
        }

        writer.println(indent("}", 1));
    }

    public static void defineAstNode(PrintWriter writer, String baseName, String className, String fields) {
        writer.println(indent("record " + className + "(" + fields + ") implements " + baseName + " {", 1));
        writer.println(indent("@Override", 2));
        writer.println(indent("public <R> R accept(Visitor<R> visitor) {", 2));
        writer.println(indent("return visitor.visit" + className + baseName + "(this);", 3));
        writer.println(indent("}", 2));
        writer.println(indent("}", 1));
    }

    public static String indent(String x, int c) {
        return "   ".repeat(c) + x;
    }
}
