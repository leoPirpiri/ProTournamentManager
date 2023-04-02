package model;

import android.content.Context;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

//Os Jogos olímpicos da antiguidade eram disputados no santuário de Olímpia.
public class Olimpia implements Serializable {
    public static final String NOME_ARQUIVO_SERIALIZADO = "default_santuario_de_olimpia.ser";
    public static final int TORNEIO_MAX = 8;
    public static final String TAG = "Nuvem";

    private ArrayList<Torneio> torneiosGerenciados;
    private ArrayList<Torneio> torneiosSeguidos;
    private Torneio simulacaoDePelada;
    private boolean atualizado = true;

    public Olimpia(){
        torneiosGerenciados = new ArrayList<>();
        torneiosSeguidos = new ArrayList<>();
        simulacaoDePelada = null;
    }

    public boolean estaAtualizado(){
        return atualizado;
    }

    public void atualizar(boolean atualizar){
        this.atualizado = !atualizar;
    }

    public int addTorneioGerenciado(Torneio torneio){
        if(getOcupacao() < TORNEIO_MAX){
            torneiosGerenciados.add(torneio);
            return torneio.getId();
        } else {
            return -1;
        }
    }

    public Torneio delTorneioGerenciado(int pos){ return torneiosGerenciados.remove(pos); }

    public Torneio getTorneioGerenciado(int idTorneio){
        for (Torneio t : torneiosGerenciados) {
            if (t.getId() == idTorneio){
                return t;
            }
        }
        return null;
    }

    public ArrayList<Torneio> getTorneiosGerenciados(){
        return torneiosGerenciados;
    }

    public ArrayList<Torneio> getTorneiosSeguidos(){
        return torneiosSeguidos;
    }

    public int getOcupacao(){
        return this.torneiosGerenciados.size();
    }

    public int getNovoTorneioId(){
        ArrayList<Integer> index = new ArrayList<>();
        for (Torneio t: torneiosGerenciados) {
            index.add(t.pegarIdNivel0());
        }
        int i = index.size()+1;
        do {
            if(!index.contains(i)){
                return i*10000;
            } else {
                i++;
            }
        }while (i<100);
        return 0;
    }

    public Torneio getSimulacaoDePelada() {
        return simulacaoDePelada;
    }

    public void setSimulacaoDePelada(Torneio simulacaoDePelada) {
        this.simulacaoDePelada = simulacaoDePelada;
    }

    public void setTorneiosGerenciados(ArrayList<Torneio> torneiosGerenciados) {
        this.torneiosGerenciados = torneiosGerenciados;
    }

    public void setTorneiosSeguidos(ArrayList<Torneio> torneiosSeguidos) {
        this.torneiosSeguidos = torneiosSeguidos;
    }

    //retorna o id completo de uma entidade de nível 0
    public static int extrairIdEntidadeSuperiorLv0(int id){
        //Do id 102030 retorna 100000
        return ((id/10000)*10000);
    }

    //retorna o id completo de uma entidade de nível 1
    public static int extrairIdEntidadeSuperiorLv1(int id){
        //Do id 102030 retorna 102000
        return ((id/100)*100);
    }

    public static Torneio testeExemplos (){
        Torneio tTeste = new Torneio(20000, "XV Torneio do Pirpiri", "d3r42ZanGWPa3KkLB63PJAghJib2");
        Equipe eTeste = new Equipe(20100, "Leicam do Maciel de Baixo", "LMB");
        Equipe eTeste2 = new Equipe(20200, "Juventude do Pirpiri Futsal", "JPF");
        Equipe eTeste3 = new Equipe(20300, "Sítio Escrivão Futsal", "SEF");
        Equipe eTeste4 = new Equipe(20400, "Leicam do Maciel de Cima", "LMC");
        Equipe eTeste5 = new Equipe(20500, "Passagem de Cima Futsal", "PCF");

        eTeste.addJogador(new Jogador(20201, "Almir Rogério", 2, 12));
        eTeste.addJogador(new Jogador(20101, "Nill do Maciel", 3, 7));
        eTeste2.addJogador(new Jogador(20202, "Bruno", 2, 1));
        eTeste3.addJogador(new Jogador(20301, "Valdir", 1, 9));
        eTeste4.addJogador(new Jogador(20401, "Kekel", 3, 7));
        eTeste5.addJogador(new Jogador(20501, "Gel", 1, 10));

        tTeste.addEquipe(eTeste);
        tTeste.addEquipe(eTeste2);
        tTeste.addEquipe(eTeste3);
        tTeste.addEquipe(eTeste4);
        tTeste.addEquipe(eTeste5);
        //teste.addTorneioGerenciado(tTeste);
        return tTeste;
    }
    public static void printteste(Context ctx, String msg){
        Toast.makeText(ctx, msg,
            Toast.LENGTH_SHORT).show();
    }
}
