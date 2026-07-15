interface Bebida {
    String descricao();
    double preco();
}

class Cafe implements Bebida {
    public String descricao() {
        return "Café";
    }
    public double preco() {
        return 5.0;
    }
}

public class ComLeite implements Bebida {
    private Bebida base;

    public ComLeite(Bebida base) {
        this.base = base;
    }

    public String descricao() {
        return base.descricao() + " com leite";
    }

    public double preco() {
        return base.preco() + 1.5;
    }
}