package detector;

import java.io.File;
import java.io.FileNotFoundException;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

public class ExploradorAST {

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length == 0) {
            System.out.println("Uso: java ExploradorAST <caminho-do-arquivo.java>");
            return;
        }

        File arquivo = new File(args[0]);
        CompilationUnit unidade = StaticJavaParser.parse(arquivo);

        for (ClassOrInterfaceDeclaration classe : unidade.findAll(ClassOrInterfaceDeclaration.class)) {
            System.out.println("=== Classe: " + classe.getNameAsString() + " ===");

            System.out.println("-- Campos --");
            for (FieldDeclaration campo : classe.getFields()) {
                System.out.println("  " + campo.getModifiers() + " " + campo.getVariables());
            }

            System.out.println("-- Construtores --");
            for (ConstructorDeclaration ctor : classe.getConstructors()) {
                System.out.println("  " + ctor.getModifiers() + " " + ctor.getNameAsString() + ctor.getParameters());
            }

            System.out.println("-- Métodos --");
            for (MethodDeclaration metodo : classe.getMethods()) {
                System.out.println("  " + metodo.getModifiers() + " " + metodo.getType() + " " + metodo.getNameAsString() + metodo.getParameters());
            }
        }
    }
}
