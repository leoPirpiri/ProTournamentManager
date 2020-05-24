package model;

import java.io.Serializable;
import java.util.ArrayList;

public class Torneio implements Serializable {
    public final int MAX_EQUIPE = 16;
    public final int MIN_EQUIPE = 4;

    private String nome;
    private boolean fechado; //True::Não permite adicionar mais equipes no torneio, tabela definida.
    private ArrayList<Equipe> times;
    private ArrayList<Partida> partidas;
    private Equipe campeao;

    public Torneio(String nome) {
        this.nome = nome;
        this.fechado = false;
        this.times = new ArrayList<>();
        this.partidas = new ArrayList<>();
        this.campeao = null;
    }

    public String getNome() {
        return nome;
    }

    public boolean isFechado() {
        return fechado;
    }

    public Equipe getCampeao() {
        return campeao;
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

    public void setCampeao(Equipe campeao) {
        this.campeao = campeao;
    }

    public void addTime(Equipe time) {
        this.times.add(time);
    }

    public void addPartida(Partida partida) {
        this.partidas.add(partida);
    }
}
