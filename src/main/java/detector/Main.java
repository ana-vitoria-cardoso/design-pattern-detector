package detector;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length == 0) {
            System.out.println("Uso: java Main <caminho-do-arquivo.java>");
            return;
        }

        List<DetectorPadrao> detectores = List.of(
                new DetectorSingleton(),
                new DetectorObserver(),
                new DetectorDecorator()
        );

        File arquivo = new File(args[0]);
        CompilationUnit unidade = StaticJavaParser.parse(arquivo);

        boolean algumDetectado = false;

        for (ClassOrInterfaceDeclaration classe : unidade.findAll(ClassOrInterfaceDeclaration.class)) {
            for (DetectorPadrao detector : detectores) {
                Optional<PadraoDetectado> resultado = detector.detectar(classe);
                if (resultado.isPresent()) {
                    resultado.get().imprimir();
                    algumDetectado = true;
                }
            }
        }

        if (!algumDetectado) {
            System.out.println("Nenhum padrão de projeto encontrado.");
        }
    }
}