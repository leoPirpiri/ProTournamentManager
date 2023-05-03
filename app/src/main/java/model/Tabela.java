package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Tabela {
    private HashMap<String, Partida> partidas;

    public Tabela() {
        this.partidas = new HashMap<>();
    }

    public HashMap<String, Partida> getPartidas() {
        return partidas;
    }

    private void adddPartidas(String key, Partida partida) {
        this.partidas.put(key, partida);
    }

    public void gerarTabela(ArrayList<Equipe> equipes, ArrayList<String> nomesPartidas){

    }

    public Partida buscarPartida(String valor){
        return partidas.get(valor);
    }

    public boolean verificarExclusaoSegura(int jogador){
        for (Map.Entry<String, Partida> entry : partidas.entrySet()) {
            Partida partida = entry.getValue();
            if (partida.participacaoJogadorNaPartida(jogador)) return true;
        }
        return false;
    }

}
