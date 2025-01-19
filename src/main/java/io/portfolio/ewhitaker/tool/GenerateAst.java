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

    public static final List<String> types = new ArrayList<>();

    static {
        types.add("Binary   : Expr left, Token operator, Expr right");
        types.add("Grouping : Expr expression");
        types.add("Literal  : Object value");
        types.add("Unary    : Token operator, Expr right");
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
        writer.println("import java.util.List;");

        writer.println("public sealed interface " + baseName + " {");

        defineVisitor(writer, baseName, types);

        // The AST classes.
        for (String type : types) {
            String[] split = type.split(":");
            defineAstNode(writer, baseName, split[0].trim(), split[1].trim());
        }

        // The base accept() method.
        writer.println("\t<R> R accept(Visitor<R> visitor);");

        writer.println("}");
    }

    public static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println("\tpublic interface Visitor<R> {");

        for (String type : types) {
            String name = type.split(":")[0].trim();
            writer.println("\t\tR visit" + name + baseName + "(" + name + " " + baseName.toLowerCase() + ");");
        }

        writer.println("\t}");
    }

    public static void defineAstNode(PrintWriter writer, String baseName, String className, String fields) {
        writer.println("\trecord " + className + "(" + fields + ") implements " + baseName + " {");
        writer.println("\t\t@Override");
        writer.println("\t\tpublic <R> R accept(Visitor<R> visitor) {");
        writer.println("\t\t\treturn visitor.visit" + className + baseName + "(this);");
        writer.println("\t\t}");
        writer.println("\t}");
    }
}
