package model;

import java.util.ArrayList;

public class Torneio extends EntConcreta {
    public final int MAX_EQUIPE = 16;
    public final int MIN_EQUIPE = 4;
    public final int TIPO_TIME = 0;
    public final int TIPO_PARTIDA = 1;

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

    public Equipe getTime(int index){
        for (Equipe e : times) {
            if (e.getId() == index){
                return e;
            }
        }
        return null;
    }

    public void addPartida(Partida partida) {
        this.partidas.add(partida);
    }

    public int getNovoElementoId(int tipo){
        ArrayList index = new ArrayList();
        switch (tipo){
            case TIPO_TIME:
                for (Equipe e: times) {
                    index.add(e.getIdNivel1());
                }
                break;
            case TIPO_PARTIDA:
                for (Partida p: partidas) {
                    index.add(p.getIdNivel1());
                }
                break;
            default:
                return 0;
        }
        int i = index.size()+1;
        do {
            if(!index.contains(i)){
                return getId() + i*100;
            } else {
                i++;
            }
        }while (i<100);
        return 0;
    }

    public String toString(){
        return super.toString() +
               " Quantidade de times: " + times.size() +
               " Estado: "+ (fechado ? (campeao == null ? "Fechado e Não finalizado" :
                                    "Finalizado - Campeão: " + campeao.getNome()) : "Aberto");
    }
}
