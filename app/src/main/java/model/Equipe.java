package model;

import java.io.Serializable;
import java.util.ArrayList;

public class Equipe implements Serializable {
    private String nome;
    private String sigla; //definir sigla tamanho máximo de 4 carcteres.
    private ArrayList<Jogador> jogadores;

    public Equipe(String nome, String sigla) {
        this.nome = nome;
        this.sigla = sigla;
        this.jogadores = new ArrayList<>();
    }
}
