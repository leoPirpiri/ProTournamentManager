package model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import control.Olimpia;

public class NoPartida extends EntConcreta {
    private int campeaoId;
    private NoPartida mandante; //Nó esquerdo do ramo. Equipe mandante da partida
    private NoPartida visitante; //Nó direito do ramo. Equipe visitante
    private ArrayList<Score> placar;
    private long tempo;

    public NoPartida(int id, String nome) {
        super(id, nome);
        this.mandante = null;
        this.visitante = null;
        this.placar = new ArrayList<>();
        this.campeaoId = 0;
        this.tempo = 0;
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

    public long getTempo() {
        return tempo;
    }

    public boolean isEncerrada() {
        return (campeaoId!=0);
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

    public void setTempo(long tempo) {
        this.tempo = tempo;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean setEncerrada() {
        HashMap<String, Integer> pontosGerais = getDetalhesPartida();
        int pontosMandante  = pontosGerais.getOrDefault("Mand_"+Score.TIPO_PONTO, 0) +
                              pontosGerais.getOrDefault("Vist_"+Score.TIPO_AUTO_PONTO, 0);
        int pontosVisitante = pontosGerais.getOrDefault("Vist_"+Score.TIPO_PONTO, 0) +
                              pontosGerais.getOrDefault("Mand_"+Score.TIPO_AUTO_PONTO, 0);

        if(pontosMandante>pontosVisitante){
            setCampeaoId(mandante.getCampeaoId());
        } else if(pontosVisitante>pontosMandante) {
            setCampeaoId(visitante.getCampeaoId());
        }

        return isEncerrada();
    }

    public void addScore(Score score) {
        this.placar.add(score);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean delScore(Score score) {
        Score s_to_del = placar.stream().filter(s -> s.getTipo() == score.getTipo() && s.getIdJogador() == score.getIdJogador()).findFirst().get();
        return this.placar.remove(s_to_del);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public HashMap<String, Integer> getDetalhesPartida() {
        return new HashMap((Map) getScoreGeral().get(2));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<List> getScoreGeral() {
        ArrayList<Score> rm = new ArrayList<>();
        ArrayList<Score> rv = new ArrayList<>();
        HashMap<String, Integer> r = new HashMap<>();
        for (Score s : placar) {
            if(Olimpia.extrairIdEntidadeSuperiorLv1(s.getIdJogador()) == mandante.getCampeaoId()){
                rm.add(s);
                r.compute("Mand_"+s.getTipo(), (k, v) -> (v == null) ? 1 : ++v);
            } else {
                rv.add(s);
                r.compute("Vist_"+s.getTipo(), (k, v) -> (v == null) ? 1 : ++v);
            }
        }
        return new ArrayList(Arrays.asList(rm, rv, r));
    }

    public String toString() {
        return super.toString() +
               " Nó Equipe MandanteId: " + (mandante!=null ? mandante.getId() : "Nó folha") +
               " Nó Equipe VisitanteId: " + (visitante!=null ? visitante.getId() : "Nó folha") +
               " Estado: " + (isEncerrada() ? "Partida encerrada." : "Partida em andamento.") +
               " IdCampeão: " + campeaoId;

    }
}
