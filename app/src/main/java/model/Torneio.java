package model;

import java.io.Serializable;
import java.util.ArrayList;

public class Torneio implements Serializable {
    private String nome;
    private boolean fechado; //True::Não permite adicionar mais equipes no torneio, tabela definida.
    private ArrayList<Equipe> times;
    private ArrayList<Partida> partidas;

    public Torneio(String nome) {
        this.nome = nome;
        this.fechado = false;
        this.times = new ArrayList<>();
        this.partidas = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public boolean isEstado() {
        return fechado;
    }

    public ArrayList<Equipe> getTimes() {
        return times;
    }

    public ArrayList<Partida> getPartidas() {
        return partidas;
    }

    public void setFechado(boolean fechado) {
        this.fechado = fechado;
    }

    public void addTime(Equipe time) {
        this.times.add(time);
    }

    public void addPartida(Partida partida) {
        this.partidas.add(partida);
    }
}
