package model;

public class Usuario {
    private final String id;
    private final String apelido;

    public Usuario(String id, String apelido) {
        this.id = id;
        this.apelido = apelido;
    }

    public String getId() {
        return id;
    }

    public String getApelido() {
        return apelido;
    }

}
