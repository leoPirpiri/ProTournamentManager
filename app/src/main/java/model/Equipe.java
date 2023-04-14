package model;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Equipe extends EntConcreta {
    public static final int JOGADORES_MAX = 25;

    private String sigla; //definir sigla tamanho máximo de 4 carcteres.
    private ArrayList<Jogador> jogadores;

    public Equipe(){}

    public Equipe(int id, String nome, String sigla) {
        super(id, nome);
        this.sigla = sigla;
        this.jogadores = new ArrayList<>();
    }

    public String getSigla(){
        return this.sigla;
    }

    public ArrayList<Jogador> getJogadores() {
        return this.jogadores;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public void setJogadores(ArrayList<Jogador> jogadores) {
        this.jogadores = jogadores;
    }

    public int getTamanhoEquipe() {
        return jogadores.size();
    }

    public int addJogador(Jogador jogador){
        if(getTamanhoEquipe()<JOGADORES_MAX) {
            jogadores.add(jogador);
            return jogador.getId();
        } else {
            return -1;
        }
    }

    public Jogador delJogador(int pos){
        return this.jogadores.remove(pos);
    }

    public int bucarIdParaNovoJogador(){
        ArrayList<Integer> index = new ArrayList<>();
        for (Jogador j:jogadores) index.add(j.pegarIdNivel2());
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

    public ArrayList<Integer> buscarNumerosDosJogadores() {
        ArrayList<Integer> numeros = new ArrayList<>();
        for (Jogador j:jogadores) numeros.add(j.getNumero());
        return numeros;
    }

    public ArrayList<Integer> buscarNumerosLivresNoPlantel(int numEditado) {
        ArrayList<Integer> numeros = new ArrayList<>();
        ArrayList<Integer> plantel = buscarNumerosDosJogadores();
        if(numEditado!=-1){
            numeros.add(numEditado);
        }
        for(int i=1; i<=JOGADORES_MAX; i++) {
            if(!plantel.contains(i)){
                numeros.add(i);
            }
        }
        return numeros;
    }

    @NonNull
    public static String formatarSigla(String entrada) {
        String sigla = "";
        for (String word : entrada.split(" ")) {
            if (word.length()>2){
                sigla = sigla.concat(word.substring(0,1));
            }
            if(sigla.length()>4){
                break;
            }
        }
        return sigla.toUpperCase();
    }

    @NonNull
    public String toString() {
        return super.toString() +
               " Sigla: "+ sigla +
               " Quantidade de jogadores: " + jogadores.size();
    }
}
