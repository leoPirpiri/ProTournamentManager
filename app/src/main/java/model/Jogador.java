package model;

import androidx.annotation.NonNull;

public class Jogador extends EntConcreta implements Comparable<Jogador> {
    private int posicao; //definir tamanho máximo de 3 caracteres.
    private int numero;

    public Jogador(){}

    public Jogador(int id, String nome, int posicao, int numero) {
        super(id, nome);
        this.posicao = posicao;
        this.numero = numero;
    }

    public int getPosicao() {
        return posicao;
    }

    public int getNumero() {
        return numero;
    }

    public void setPosicao(int posicao) {
        this.posicao = posicao;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    @Override
    public int compareTo(Jogador pl) {
        return Integer.compare(posicao, pl.posicao);
    }

    @NonNull
    public String toString() {
        return super.toString() +
               " Posição: "+ posicao +
               " Número: "+ numero;
    }

}
