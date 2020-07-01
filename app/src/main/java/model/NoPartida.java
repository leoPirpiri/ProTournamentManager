package model;

import java.util.ArrayList;

public class NoPartida extends EntConcreta {
    private int campeaoId;
    private NoPartida mandante; //Nó esquerdo do ramo. Equipe mandante da partida
    private NoPartida visitante; //Nó direito do ramo. Equipe visitante
    private ArrayList<Score> placar;

    public NoPartida(int id, String nome) {
        super(id, nome);
        this.mandante = null;
        this.visitante = null;
        this.placar = new ArrayList<>();
        this.campeaoId = 0;
    }
    public NoPartida(int id, String nome, int campeaoId) {
        this(id, nome);
        this.campeaoId = campeaoId;

    }

    public int getCampeaoId() {
        return this.campeaoId;
    }

    public NoPartida getMandante() {
        return mandante;
    }

    public NoPartida getVisitante() {
        return visitante;
    }

    public boolean isEncerrada() {
        return (campeaoId != 0);
    }

    public void setCampeaoId(int campeaoId){
        this.campeaoId = campeaoId;
    }
    public void setMandante(NoPartida mandante) {
        this.mandante = mandante;
    }

    public void setVisitante(NoPartida visitante) {
        this.visitante = visitante;
    }

    public void setEncerrada() {
        int[] pontos = getPlacarPontos();
        if(pontos[0]>pontos[1]){
            setCampeaoId(mandante.getCampeaoId());
        } else if(pontos[1]>pontos[0]) {
            setCampeaoId(visitante.getCampeaoId());
        } else {
            setCampeaoId(0);
        }
    }

    public void addScore(Score score) {
        this.placar.add(score);
    }

    public int[] getPlacarPontos() {
        int pontos[] = new int[2];
        int pontosMandante = 0;
        int pontosVisitante = 0;
        for (Score s : placar) {
            if (s.getTipo() == s.TIPO_PONTO){
                if (mandante.getCampeaoId() == s.extrairIdEntidadeSuperiorLv1()) {
                    pontosMandante++;
                } else if (visitante.getCampeaoId() == s.extrairIdEntidadeSuperiorLv1()) {
                    pontosVisitante++;
                } else {
                    return null;
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
                if (mandante.getCampeaoId() == s.extrairIdEntidadeSuperiorLv1()) {
                    faltaMandante++;
                } else if (visitante.getCampeaoId() == s.extrairIdEntidadeSuperiorLv1()) {
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

//    public int getNovoScoreId(){
//        ArrayList index = new ArrayList();
//        for (Score s: placar) {
//            index.add(s.getIdNivel2());
//        }
//        int i = index.size()+1;
//        do {
//            if(!index.contains(i)){
//                return getId() + i;
//            } else {
//                i++;
//            }
//        }while (i<100);
//        return 0;
//    }

    public String toString() {
        return super.toString() +
               " Nó Equipe MandanteId: " + (mandante!=null ? mandante.getId() : "Nó folha") +
               " Nó Equipe VisitanteId: " + (visitante!=null ? visitante.getId() : "Nó folha") +
               " Estado: " + (isEncerrada() ? "Partida encerrada." : "Partida em andamento.") +
               " IdCampeão: " + campeaoId;

    }
}
