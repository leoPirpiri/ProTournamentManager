package model;

import java.util.ArrayList;

public class Equipe extends EntConcreta {
    public final int JOGADORES_MAX = 25;
    private int novoIdJogador;

    private String sigla; //definir sigla tamanho máximo de 4 carcteres.
    private ArrayList<Jogador> jogadores;

    public Equipe(int id, String nome, String sigla) {
        super(id, nome);
        this.sigla = sigla;
        this.novoIdJogador = 0;
        this.jogadores = new ArrayList<>();
    }

    private int getIdNovoJogador(){
        return 0;
    }

    public String getSigla(){
        return this.sigla;
    }

    public int getTamanhoEquipe() {
        return jogadores.size();
    }

    public int addJogador(Jogador jogador){
        if(jogadores.size()<JOGADORES_MAX) {
            jogador.setId(this.getIdNovoJogador());
            jogadores.add(jogador);
            return jogador.getId();
        } else {
            return -1;
        }
    }

    public ArrayList<Jogador> getJogadores() {
        return this.jogadores;
    }
}
