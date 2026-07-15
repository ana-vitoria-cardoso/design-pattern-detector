package detector;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

public class DetectorSingleton implements DetectorPadrao {

    @Override
    public Optional<PadraoDetectado> detectar(ClassOrInterfaceDeclaration classe) {
        String nomeClasse = classe.getNameAsString();

        List<ConstructorDeclaration> construtores = classe.getConstructors();
        boolean temConstrutor = !construtores.isEmpty();
        boolean todosConstructoresNaoPublicos = construtores.stream()
                .noneMatch(c -> c.getModifiers().contains(Modifier.publicModifier()));

        List<FieldDeclaration> campoEstaticoMesmoTipo = classe.getFields().stream()
                .filter(f -> f.getModifiers().contains(Modifier.staticModifier()))
                .filter(f -> f.getVariables().stream()
                        .anyMatch(v -> v.getTypeAsString().equals(nomeClasse)))
                .collect(Collectors.toList());

        List<MethodDeclaration> metodoAcessoEstatico = classe.getMethods().stream()
                .filter(m -> m.getModifiers().contains(Modifier.staticModifier()))
                .filter(m -> m.getModifiers().contains(Modifier.publicModifier()))
                .filter(m -> m.getTypeAsString().equals(nomeClasse))
                .collect(Collectors.toList());

        boolean detectado = temConstrutor
                && todosConstructoresNaoPublicos
                && !campoEstaticoMesmoTipo.isEmpty()
                && !metodoAcessoEstatico.isEmpty();

        if (!detectado) {
            return Optional.empty();
        }

        String elementos = String.format(
                "construtor(es) não-público(s) [%s], campo estático do tipo %s (%s), método estático público de acesso: %s",
                construtores.stream().map(c -> c.getModifiers().toString()).collect(Collectors.joining(", ")),
                nomeClasse,
                campoEstaticoMesmoTipo.get(0).getVariable(0).getNameAsString(),
                metodoAcessoEstatico.get(0).getNameAsString() + "()"
        );

        String vantagem = String.format(
                "Garante que a classe %s tenha no máximo uma instância acessível globalmente via %s(), " +
                "útil se essa classe representa um recurso que não deveria ser duplicado no contexto analisado.",
                nomeClasse, metodoAcessoEstatico.get(0).getNameAsString()
        );

        String risco = String.format(
                "O construtor privado de %s dificulta testes unitários isolados (não é possível injetar um mock no lugar da instância real), " +
                "e o estado guardado no campo estático (%s) é compartilhado globalmente, criando acoplamento implícito entre partes do sistema que usam essa classe.",
                nomeClasse, campoEstaticoMesmoTipo.get(0).getVariable(0).getNameAsString()
        );

        return Optional.of(new PadraoDetectado("Singleton", elementos, vantagem, risco));
    }
}