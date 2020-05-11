package model;

import java.util.ArrayList;

public class Torneio {
    private int id;
    private String nome;
    private boolean estado; //True::Não permite adicionar mais equipes no torneio, tabela definida.
    private ArrayList<Equipe> times;
    private ArrayList<Partida> partidas;

    public Torneio(int id, String nome, boolean estado) {
        this.id = id;
        this.nome = nome;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public boolean isEstado() {
        return estado;
    }

    public ArrayList<Equipe> getTimes() {
        return times;
    }

    public ArrayList<Partida> getPartidas() {
        return partidas;
    }
}
