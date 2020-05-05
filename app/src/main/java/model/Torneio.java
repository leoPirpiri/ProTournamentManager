package model;

import java.util.ArrayList;

public class Torneio {
    private int id;
    private boolean estado; //True::Não permite adicionar mais equipes no torneio, tabela definida.
    private ArrayList<Equipe> times;
    private ArrayList<Partida> partidas;
}
