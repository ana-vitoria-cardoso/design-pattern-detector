import java.util.ArrayList;
import java.util.List;

interface OuvinteEvento {
    void notificar(String mensagem);
}

public class Emissor {
    private List<OuvinteEvento> ouvintes = new ArrayList<>();

    public void inscrever(OuvinteEvento ouvinte) {
        ouvintes.add(ouvinte);
    }

    public void disparar(String mensagem) {
        for (OuvinteEvento ouvinte : ouvintes) {
            ouvinte.notificar(mensagem);
        }
    }
}