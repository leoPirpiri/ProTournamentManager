package model;

import java.util.ArrayList;

public class Jogador extends EntConcreta {
    private String posicao; //definir tamanho máximo de 3 caracteres.
    private int numero;

    public Jogador(int id, String nome, String posicao, int numero) {
        super(id, nome);
        this.posicao = posicao;
        this.numero = numero;
    }

    public String getPosicao() {
        return posicao;
    }

    public int getNumero() {
        return numero;
    }

    public void setPosicao(String posicao) {
        this.posicao = posicao;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String toString() {
        return super.toString() +
               " Posição: "+ posicao +
               " Número: "+ numero;
    }
}
