package control;

import android.content.Context;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import model.Equipe;
import model.Jogador;
import model.Torneio;
//Os Jogos olímpicos da antiguidade eram disputados no santuário de Olímpia.
public class Olimpia implements Serializable {
    public final static String NOME_ARQUIVO_SERIALIZADO = "default_santuario_de_olimpia.ser";
    public final int TORNEIO_MAX = 8;

    private ArrayList<Torneio> torneios;
    private Torneio simulacao;

    public Olimpia(){
        torneios = new ArrayList<>();
        simulacao = null;
    }

    public static void salvarSantuario (Olimpia santuario, FileOutputStream fileOutputStream) throws IOException{
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(santuario);
        objectOutputStream.flush();
        objectOutputStream.close();
    }

    public static Olimpia carregarSantuario(FileInputStream fileInputStream) throws IOException {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            Olimpia santuario = (Olimpia) objectInputStream.readObject();
            objectInputStream.close();
            return santuario;
        } catch (ClassNotFoundException ex) {
            throw new IOException("Exceção de classe não esperada.");
        }
    }

    public int addTorneio(Torneio torneio){
        if(torneios.size()<TORNEIO_MAX){
            torneios.add(torneio);
            return torneio.getId();
        } else {
            return -1;
        }
    }

    public Torneio delTorneio(int pos){
            return torneios.remove(pos);
    }

    public Torneio getTorneio(int idTorneio){
        for (Torneio t : torneios) {
            if (t.getId() == idTorneio){
                return t;
            }
        }
        return null;
    }

    public ArrayList<Torneio> getTorneios(){
        return torneios;
    }

    public int getOcupacao(){
        return this.torneios.size();
    }

    public int getNovoTorneioId(){
        ArrayList<Integer> index = new ArrayList<>();
        for (Torneio t:torneios) {
            index.add(t.getIdNivel0());
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

    public Torneio getSimulacao() {
        return simulacao;
    }

    public void setSimulacao(Torneio simulacao) {
        this.simulacao = simulacao;
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

    public Olimpia testeExemplos (Olimpia teste){
        Torneio tTeste = new Torneio(10000, "XV Torneio do Pirpiri");
        Equipe eTeste = new Equipe(10100, "Leicam do Maciel de Baixo", "LMB");
        Equipe eTeste2 = new Equipe(10200, "Juventude do Pirpiri Futsal", "JPF");
        Equipe eTeste3 = new Equipe(10300, "Sítio Escrivão Futsal", "SEF");
        Equipe eTeste4 = new Equipe(10400, "Leicam do Maciel de Cima", "LMC");
        Equipe eTeste5 = new Equipe(10500, "Passagem de Cima Futsal", "PCF");

        eTeste.addJogador(new Jogador(10201, "Almir Rogério", 2, 12));
        eTeste.addJogador(new Jogador(10101, "Nill do Maciel", 3, 7));
        eTeste2.addJogador(new Jogador(10202, "Bruno", 2, 1));
        eTeste3.addJogador(new Jogador(10301, "Valdir", 1, 9));
        eTeste4.addJogador(new Jogador(10401, "Kekel", 3, 7));
        eTeste5.addJogador(new Jogador(10501, "Gel", 1, 10));

        tTeste.addEquipe(eTeste);
        tTeste.addEquipe(eTeste2);
        tTeste.addEquipe(eTeste3);
        tTeste.addEquipe(eTeste4);
        tTeste.addEquipe(eTeste5);
        teste.addTorneio(tTeste);
        return teste;
    }
    public static void printteste(Context ctx, String msg){
        Toast.makeText(ctx, msg,
            Toast.LENGTH_SHORT).show();
        System.out.println(msg);
    }
}
