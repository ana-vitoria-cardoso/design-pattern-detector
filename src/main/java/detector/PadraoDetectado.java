package detector;

public class PadraoDetectado {

    private final String nomePadrao;
    private final String elementosIdentificados;
    private final String vantagem;
    private final String risco;

    public PadraoDetectado(String nomePadrao, String elementosIdentificados, String vantagem, String risco) {
        this.nomePadrao = nomePadrao;
        this.elementosIdentificados = elementosIdentificados;
        this.vantagem = vantagem;
        this.risco = risco;
    }

    public void imprimir() {
        System.out.println("[PADRÃO DETECTADO] " + nomePadrao);
        System.out.println("Elementos identificados: " + elementosIdentificados);
        System.out.println("Vantagem neste contexto: " + vantagem);
        System.out.println("Risco/desvantagem neste contexto: " + risco);
        System.out.println();
    }
}