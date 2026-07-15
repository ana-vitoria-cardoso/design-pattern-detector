package detector;

import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

public class DetectorObserver implements DetectorPadrao {

    private static final List<String> TIPOS_COLECAO = List.of("List", "ArrayList", "Set", "HashSet", "Collection");

    @Override
    public Optional<PadraoDetectado> detectar(ClassOrInterfaceDeclaration classe) {
        String nomeClasse = classe.getNameAsString();

        // (a) campo do tipo coleção parametrizada com algum tipo X
        Optional<FieldDeclaration> campoColecao = classe.getFields().stream()
                .filter(this::ehCampoDeColecao)
                .findFirst();

        if (campoColecao.isEmpty()) {
            return Optional.empty();
        }

        VariableDeclarator variavel = campoColecao.get().getVariable(0);
        String nomeCampo = variavel.getNameAsString();
        String tipoElemento = extrairTipoParametrizado(variavel.getType());

        if (tipoElemento == null) {
            return Optional.empty();
        }

        // (b) método que chama <campo>.add(...)
        Optional<MethodDeclaration> metodoInscricao = classe.getMethods().stream()
                .filter(m -> chamaAddNoCampo(m, nomeCampo))
                .findFirst();

        // (c) método que itera o campo em foreach e chama algo em cada elemento
        Optional<MethodDeclaration> metodoNotificacao = classe.getMethods().stream()
                .filter(m -> iteraCampoComChamada(m, nomeCampo))
                .findFirst();

        if (metodoInscricao.isEmpty() || metodoNotificacao.isEmpty()) {
            return Optional.empty();
        }

        String elementos = String.format(
                "campo coleção '%s' de %s, método de inscrição '%s()' que adiciona a '%s', " +
                "método de notificação '%s()' que itera '%s' invocando método em cada elemento",
                nomeCampo, tipoElemento,
                metodoInscricao.get().getNameAsString(), nomeCampo,
                metodoNotificacao.get().getNameAsString(), nomeCampo
        );

        String vantagem = String.format(
                "%s pode notificar múltiplos %s inscritos em '%s' via %s() sem conhecer suas implementações concretas, " +
                "permitindo adicionar novos tipos de observadores sem alterar %s.",
                nomeClasse, tipoElemento, nomeCampo, metodoNotificacao.get().getNameAsString(), nomeClasse
        );

        String risco = String.format(
                "A ordem de notificação em '%s' depende da ordem de inserção em '%s', o que pode causar dependências " +
                "implícitas entre observadores; além disso, se algum elemento de '%s' lançar exceção durante %s(), " +
                "os observadores seguintes na coleção podem não ser notificados, dependendo de como a iteração for tratada.",
                metodoNotificacao.get().getNameAsString(), nomeCampo, nomeCampo, metodoNotificacao.get().getNameAsString()
        );

        return Optional.of(new PadraoDetectado("Observer", elementos, vantagem, risco));
    }

    private boolean ehCampoDeColecao(FieldDeclaration campo) {
        Type tipo = campo.getVariable(0).getType();
        if (!tipo.isClassOrInterfaceType()) return false;
        String nomeTipo = tipo.asClassOrInterfaceType().getNameAsString();
        return TIPOS_COLECAO.contains(nomeTipo) && extrairTipoParametrizado(tipo) != null;
    }

    private String extrairTipoParametrizado(Type tipo) {
        if (!tipo.isClassOrInterfaceType()) return null;
        ClassOrInterfaceType classType = tipo.asClassOrInterfaceType();
        return classType.getTypeArguments()
                .map(args -> args.isEmpty() ? null : args.get(0).asString())
                .orElse(null);
    }

    private boolean chamaAddNoCampo(MethodDeclaration metodo, String nomeCampo) {
        return metodo.findAll(MethodCallExpr.class).stream()
                .anyMatch(chamada -> chamada.getNameAsString().equals("add")
                        && chamada.getScope().isPresent()
                        && chamada.getScope().get().toString().equals(nomeCampo));
    }

    private boolean iteraCampoComChamada(MethodDeclaration metodo, String nomeCampo) {
        return metodo.findAll(ForEachStmt.class).stream()
                .anyMatch(forEach -> forEach.getIterable().toString().equals(nomeCampo)
                        && !forEach.getBody().findAll(MethodCallExpr.class).isEmpty());
    }
}