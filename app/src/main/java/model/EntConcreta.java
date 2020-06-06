package model;

public class EntConcreta extends EntGeral {
    private String nome;

    public EntConcreta(int id, String nome) {
        super(id);
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
