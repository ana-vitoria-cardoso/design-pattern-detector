package detector;

import java.util.Optional;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public interface DetectorPadrao {

    /**
     * Analisa uma classe e retorna o padrão detectado, se a estrutura bater
     * com a heurística deste detector. Retorna Optional.empty() se não detectar.
     */
    Optional<PadraoDetectado> detectar(ClassOrInterfaceDeclaration classe);
}