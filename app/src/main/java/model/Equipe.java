package model;

import java.io.Serializable;
import java.util.ArrayList;

public class Equipe implements Serializable {
    private int id;
    private String nome;
    private String sigla; //definir sigla tamanho máximo de 4 carcteres.
    private ArrayList<Jogador> jogadores;
}
