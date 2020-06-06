package model;

import java.util.ArrayList;

public class Torneio extends EntConcreta {
    public final int MAX_EQUIPE = 16;
    public final int MIN_EQUIPE = 4;

    private boolean fechado; //True::Não permite adicionar mais equipes no torneio, tabela definida.
    private ArrayList<Equipe> times;
    private ArrayList<Partida> partidas;
    private Equipe campeao;

    public Torneio(int id, String nome) {
        super(id, nome);
        this.fechado = false;
        this.times = new ArrayList<>();
        this.partidas = new ArrayList<>();
        this.campeao = null;
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

    public Equipe delTime(int pos){
        return times.remove(pos);
    }

    public void addPartida(Partida partida) {
        this.partidas.add(partida);
    }
}
