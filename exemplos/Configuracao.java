public class Configuracao {
    private static Configuracao instancia;
    private String valor;

    private Configuracao() {
        this.valor = "padrão";
    }

    public static Configuracao getInstancia() {
        if (instancia == null) {
            instancia = new Configuracao();
        }
        return instancia;
    }
}