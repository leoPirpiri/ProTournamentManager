package model;

import java.util.ArrayList;

public class Equipe extends EntConcreta {
    public final int JOGADORES_MAX = 25;

    private String sigla; //definir sigla tamanho máximo de 4 carcteres.
    private ArrayList<Jogador> jogadores;

    public Equipe(int id, String nome, String sigla) {
        super(id, nome);
        this.sigla = sigla;
        this.jogadores = new ArrayList<>();
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getSigla(){
        return this.sigla;
    }

    public int getTamanhoEquipe() {
        return jogadores.size();
    }

    public int addJogador(Jogador jogador){
        if(jogadores.size()<JOGADORES_MAX) {
            jogadores.add(jogador);
            return jogador.getId();
        } else {
            return -1;
        }
    }

    public ArrayList<Jogador> getJogadores() {
        return this.jogadores;
    }

    public int getNovoJogadorId(){
        ArrayList index = new ArrayList();
        for (Jogador j:jogadores) {
            index.add(j.getIdNivel0());
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
               " Sigla: "+ sigla +
               " Quantidade de jogadores: " + jogadores.size();
    }
}
