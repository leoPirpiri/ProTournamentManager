package model;

import androidx.annotation.NonNull;

public class EntConcreta extends EntGeral {
    private String nome;

    public EntConcreta(){}
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

    @NonNull
    public String toString(){
        return super.toString() + " Nome: "+ nome;
    }
}
