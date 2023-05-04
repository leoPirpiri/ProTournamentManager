package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tabela  implements Serializable {
    private HashMap<Integer, Partida> partidas;

    public Tabela() {
        this.partidas = new HashMap<>();
    }

    public HashMap<Integer, Partida> getPartidas() {
        return partidas;
    }

    private void adddPartidas(Integer key, Partida partida) {
        this.partidas.put(key, partida);
    }

    public void sortearTimesGerarTabela(ArrayList<Equipe> equipes, String[] nomesPartidas){
        Collections.shuffle(equipes);
        gerarTabela(equipes, nomesPartidas, 1);
    }
    private void gerarTabela(List<Equipe> equipesRestantes, String[] nomesPartidas, int idPartida){
        Partida novaPartida = new Partida(idPartida, nomesPartidas[idPartida]);
        partidas.put(idPartida, novaPartida);
        int meio = (int)Math.round(equipesRestantes.size()/2.0);
        switch (equipesRestantes.size()){
            case 3:
                novaPartida.setVisitante(equipesRestantes.get(2).getId());
                gerarTabela(equipesRestantes.subList(0, meio), nomesPartidas, idPartida*2);
                break;
            case 2:
                novaPartida.setMandante(equipesRestantes.get(0).getId());
                novaPartida.setVisitante(equipesRestantes.get(1).getId());
                break;
            default:
                gerarTabela(equipesRestantes.subList(0, meio), nomesPartidas, idPartida*2);
                gerarTabela(equipesRestantes.subList(meio, equipesRestantes.size()), nomesPartidas, (idPartida*2)+1);
                break;
        }
    }

    public Partida buscarPartida(Integer valor){
        return partidas.get(valor);
    }

    public boolean verificarExclusaoSegura(int jogador){
        for (Map.Entry<Integer, Partida> entry : partidas.entrySet()) {
            Partida partida = entry.getValue();
            if (partida.participacaoJogadorNaPartida(jogador)) return true;
        }
        return false;
    }

}
