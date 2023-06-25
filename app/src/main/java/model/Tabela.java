package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Tabela  implements Serializable {
    private final HashMap<Integer, Partida> partidas;

    public Tabela() {
        this.partidas = new HashMap<>();
    }

    public HashMap<Integer, Partida> getPartidas() {
        return partidas;
    }

    public void addPartida(Integer key, Partida partida) {
        this.partidas.put(key, partida);
    }

    public void sortearTimesGerarTabela(ArrayList<Equipe> equipes, String[] nomesPartidas, String mesario){
        Collections.shuffle(equipes);
        gerarTabela(equipes, nomesPartidas, 1, mesario);
    }
    private void gerarTabela(List<Equipe> equipesRestantes, String[] nomesPartidas, int idPartida, String mesario){
        Partida novaPartida = new Partida(idPartida, nomesPartidas[idPartida], mesario);
        addPartida(idPartida, novaPartida);
        int meio = (int)Math.round(equipesRestantes.size()/2.0);
        switch (equipesRestantes.size()){
            case 3:
                novaPartida.setVisitante(equipesRestantes.get(2).getId());
                gerarTabela(equipesRestantes.subList(0, meio), nomesPartidas, idPartida*2, mesario);
                break;
            case 2:
                novaPartida.setMandante(equipesRestantes.get(0).getId());
                novaPartida.setVisitante(equipesRestantes.get(1).getId());
                break;
            default:
                gerarTabela(equipesRestantes.subList(0, meio), nomesPartidas, idPartida*2, mesario);
                gerarTabela(equipesRestantes.subList(meio, equipesRestantes.size()), nomesPartidas, (idPartida*2)+1, mesario);
                break;
        }
    }

    public Partida buscarPartida(Integer valor){
        return partidas.get(valor);
    }

    public ArrayList<Partida> buscarPartidasOitavas(boolean lado){
        // O lado é esquero se valor igual a true. Então a busca vai retornar as oitavas de finais para o lado esquerdo.
        ArrayList<Partida> partidasOitavas = new ArrayList<>();
        for (int i=(lado ? 8 : 12); i<= (lado ? 11 : 15); i++){
            partidasOitavas.add(buscarPartida(i));
        }
        return partidasOitavas;
    }

    public ArrayList<Score> buscarEstatisticasGerais(int idEquipe){
        ArrayList<Score> r = new ArrayList<>();
        for (Map.Entry<Integer, Partida> entry : partidas.entrySet()) {
            if (idEquipe>0){
                if (entry.getValue().getMandante() == idEquipe || entry.getValue().getVisitante() == idEquipe){
                    r.addAll(entry.getValue().getPlacar().stream()
                            .filter(score -> Olimpia.extrairIdEntidadeSuperiorLv1(score.getIdJogador()) == idEquipe)
                            .collect(Collectors.toList())
                    );
                }
            } else {
                r.addAll(entry.getValue().getPlacar());
            }
        }
        return r;
    }

    public boolean verificarExclusaoSegura(int jogador){
        for (Map.Entry<Integer, Partida> entry : partidas.entrySet()) {
            Partida partida = entry.getValue();
            if (partida.participacaoJogadorNaPartida(jogador)) return true;
        }
        return false;
    }

}
