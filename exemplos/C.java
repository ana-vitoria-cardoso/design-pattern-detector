interface A {
    String metodo1();
    int metodo2();
}

class B implements A {
    public String metodo1() {
        return "valor base";
    }
    public int metodo2() {
        return 10;
    }
}

public class C implements A {
    private A ref;

    public C(A ref) {
        this.ref = ref;
    }

    public String metodo1() {
        return ref.metodo1() + " modificado";
    }

    public int metodo2() {
        return ref.metodo2() + 1;
    }
}