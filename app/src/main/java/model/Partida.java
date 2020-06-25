package model;

import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class Partida extends EntConcreta {
    private int mandante; //Equipe mandante da partida
    private int visitante; //Equipe visitante
    private boolean estado; //True::partida encerrada
    private ArrayList<Score> placar;

    public Partida(int id, String nome, int mandante, int visitante) {
        super(id, nome);
        this.mandante = mandante;
        this.visitante = visitante;
        this.estado = false;
        this.placar = new ArrayList<>();
    }

    public int getMandante() {
        return mandante;
    }

    public void setMandante(int mandante) {
        this.mandante = mandante;
    }

    public int getVisitante() {
        return visitante;
    }

    public void setVisitante(int visitante) {
        this.visitante = visitante;
    }

    public boolean isEncerrada() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public void addScore(Score score) {
        this.placar.add(score);
    }

    public int[] getPlacarPontos() {
        int pontos[] = new int[2];
        int pontosMandante, pontosVisitante;
        pontosMandante = pontosVisitante= 0;
        for (Score s : placar) {
            if (s.getTipo() == s.TIPO_PONTO){
                if (mandante == s.extrairIdEntidadeSuperiorLv1()) {
                    pontosMandante++;
                } else if (visitante == s.extrairIdEntidadeSuperiorLv1()) {
                    pontosVisitante++;
                } else {
                    break;
                }
            }
        }
        pontos[0] = pontosMandante;
        pontos[1] = pontosVisitante;
        return pontos;
    }

    public int[] getPlacarFaltas() {
        int falta[] = new int[2];
        int faltaMandante, faltaVisitante;
        faltaMandante = faltaVisitante= 0;
        for (Score s : placar) {
            if (s.getTipo() == s.TIPO_FALTA_INDIVIDUAL){
                if (mandante == s.extrairIdEntidadeSuperiorLv1()) {
                    faltaMandante++;
                } else if (visitante == s.extrairIdEntidadeSuperiorLv1()) {
                    faltaVisitante++;
                } else {
                    break;
                }
            }
        }
        falta[0] = faltaMandante;
        falta[1] = faltaVisitante;
        return falta;
    }

    public int getNovoScoreId(){
        ArrayList index = new ArrayList();
        for (Score s: placar) {
            index.add(s.getIdNivel2());
        }
        int i = index.size()+1;
        do {
            if(!index.contains(i)){
                return getId() + i;
            } else {
                i++;
            }
        }while (i<100);
        return 0;
    }

    public String toString() {
        return super.toString() +
               " Equipe Mandante: " + mandante +
               " Equipe Visitante: " + visitante +
               " Estado: " + (estado ? "Partida encerrada.": "Partida em andamento.");

    }
}
