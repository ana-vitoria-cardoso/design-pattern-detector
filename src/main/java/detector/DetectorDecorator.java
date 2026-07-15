package detector;

import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class DetectorDecorator implements DetectorPadrao {

    @Override
    public Optional<PadraoDetectado> detectar(ClassOrInterfaceDeclaration classe) {
        String nomeClasse = classe.getNameAsString();

        // (a) a classe implementa pelo menos uma interface
        List<ClassOrInterfaceType> interfacesImplementadas = classe.getImplementedTypes();
        if (interfacesImplementadas.isEmpty()) {
            return Optional.empty();
        }

        for (ClassOrInterfaceType interfaceImplementada : interfacesImplementadas) {
            String nomeInterface = interfaceImplementada.getNameAsString();

            // (b) campo do mesmo tipo da interface implementada
            Optional<FieldDeclaration> campoMesmoTipo = classe.getFields().stream()
                    .filter(f -> f.getVariable(0).getTypeAsString().equals(nomeInterface))
                    .findFirst();

            if (campoMesmoTipo.isEmpty()) {
                continue;
            }

            VariableDeclarator variavel = campoMesmoTipo.get().getVariable(0);
            String nomeCampo = variavel.getNameAsString();

            // (c) o campo é recebido via construtor (parâmetro com o mesmo tipo, atribuído ao campo)
            boolean recebidoNoConstrutor = classe.getConstructors().stream()
                    .anyMatch(ctor -> construtorAtribuiParametro(ctor, nomeInterface, nomeCampo));

            if (!recebidoNoConstrutor) {
                continue;
            }

            // (d) algum método delega chamada para o campo
            Optional<MethodDeclaration> metodoDelegador = classe.getMethods().stream()
                    .filter(m -> delegaParaCampo(m, nomeCampo))
                    .findFirst();

            if (metodoDelegador.isEmpty()) {
                continue;
            }

            String elementos = String.format(
                    "implementa a interface %s, campo '%s' do tipo %s recebido via construtor, " +
                    "método '%s()' delega chamada para '%s'",
                    nomeInterface, nomeCampo, nomeInterface,
                    metodoDelegador.get().getNameAsString(), nomeCampo
            );

            String vantagem = String.format(
                    "%s pode adicionar comportamento a qualquer implementação de %s em tempo de execução " +
                    "(via composição no campo '%s'), sem precisar de subclasses fixas para cada combinação de comportamento.",
                    nomeClasse, nomeInterface, nomeCampo
            );

            String risco = String.format(
                    "Encadear múltiplas decorações sobre '%s' pode dificultar depurar o fluxo real de execução, " +
                    "já que a chamada a '%s()' se propaga por uma cadeia de objetos %s cujo tamanho não é visível " +
                    "só de olhar para %s isoladamente.",
                    nomeCampo, metodoDelegador.get().getNameAsString(), nomeInterface, nomeClasse
            );

            return Optional.of(new PadraoDetectado("Decorator", elementos, vantagem, risco));
        }

        return Optional.empty();
    }

    private boolean construtorAtribuiParametro(ConstructorDeclaration ctor, String tipoParametro, String nomeCampo) {
        boolean temParametroDoTipo = ctor.getParameters().stream()
                .anyMatch(p -> p.getTypeAsString().equals(tipoParametro));

        if (!temParametroDoTipo) {
            return false;
        }

        // procura uma atribuição do tipo "this.campo = parametro" ou "campo = parametro" no corpo
        String corpo = ctor.getBody().toString();
        return corpo.contains(nomeCampo + " =");
    }

    private boolean delegaParaCampo(MethodDeclaration metodo, String nomeCampo) {
        return metodo.findAll(MethodCallExpr.class).stream()
                .anyMatch(chamada -> chamada.getScope().isPresent()
                        && chamada.getScope().get().toString().equals(nomeCampo));
    }
}