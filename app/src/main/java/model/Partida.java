package model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Partida extends EntConcreta {
    private int campeaoId;
    private int mandante;
    private int visitante;
    private ArrayList<Score> placar;
    private long tempo;

    public Partida(){}

    public Partida(int id, String nome) {
        super(id, nome);
        this.mandante = 0;
        this.visitante = 0;
        this.placar = new ArrayList<>();
        this.campeaoId = 0;
        this.tempo = 0;
    }

    public Partida(int id, String nome, int campeaoId) {
        this(id, nome);
        this.campeaoId = campeaoId;
    }

    public int getCampeaoId() {
        return this.campeaoId;
    }

    public int getMandante() {
        return mandante;
    }

    public int getVisitante() {
        return visitante;
    }

    public long getTempo() {
        return tempo;
    }

    public boolean estaEncerrada() {
        return (campeaoId!=0);
    }

    public void setCampeaoId(int campeaoId){
        this.campeaoId = campeaoId;
    }

    public void setMandante(int mandante) {
        this.mandante = mandante;
    }

    public void setVisitante(int visitante) {
        this.visitante = visitante;
    }

    public void setTempo(long tempo) {
        this.tempo = tempo;
    }

    @SuppressWarnings("ConstantConditions")
    public boolean setEncerrada() {
        HashMap<String, Integer> pontosGerais = buscarDetalhesDaPartida();
        int pontosMandante  = pontosGerais.getOrDefault("Mand_"+Score.TIPO_PONTO, 0) +
                              pontosGerais.getOrDefault("Vist_"+Score.TIPO_AUTO_PONTO, 0);
        int pontosVisitante = pontosGerais.getOrDefault("Vist_"+Score.TIPO_PONTO, 0) +
                              pontosGerais.getOrDefault("Mand_"+Score.TIPO_AUTO_PONTO, 0);

        if(pontosMandante>pontosVisitante){
            setCampeaoId(mandante);
        } else if(pontosVisitante>pontosMandante) {
            setCampeaoId(visitante);
        }
        return estaEncerrada();
    }

    public void addScore(Score score) {
        this.placar.add(score);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public boolean delScore(Score score) {
        Score scoreDeletado = placar.stream().filter(s -> s.getTipo() == score.getTipo()
                && s.getIdJogador() == score.getIdJogador()).findFirst().get();
        return this.placar.remove(scoreDeletado);
    }


    @SuppressWarnings("unchecked")
    public HashMap<String, Integer> buscarDetalhesDaPartida() {
        return new HashMap<>((Map<String, Integer>) buscarScoreGeral().get(2));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public ArrayList<List<?>> buscarScoreGeral() {
        ArrayList<Score> rm = new ArrayList<>();
        ArrayList<Score> rv = new ArrayList<>();
        HashMap<String, Integer> r = new HashMap<>();
        for (Score s : placar) {
            if(Olimpia.extrairIdEntidadeSuperiorLv1(s.getIdJogador()) == mandante){
                rm.add(s);
                r.compute("Mand_"+s.getTipo(), (k, v) -> (v == null) ? 1 : ++v);
            } else {
                rv.add(s);
                r.compute("Vist_"+s.getTipo(), (k, v) -> (v == null) ? 1 : ++v);
            }
        }
        return new ArrayList(Arrays.asList(rm, rv, r));
    }

    public boolean participacaoJogadorNaPartida(int idJogador){
        for (Score s : placar) {
            if(s.getIdJogador() == idJogador){
                return true;
            }
        }
        return false;
    }

    @NonNull
    public String toString() {
        return super.toString() +
               "-> Equipe MandanteId: " + (mandante) + " Vs " +
               " Equipe VisitanteId: " + (visitante) +
               " , Estado: " + (estaEncerrada() ? "Partida encerrada." : "Partida em andamento.") +
               " IdCampeão: " + campeaoId;
    }
}
