package model;

public class Usuario {
    private String id;
    private String apelido;

    public Usuario(){}

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
