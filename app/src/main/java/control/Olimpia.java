package control;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
    public static final String NOME_ARQUIVO_SERIALIZADO = "default_santuario_de_olimpia.ser";
    public static final int TORNEIO_MAX = 8;
    public static final String TAG = "Nuvem";

    private ArrayList<Torneio> torneios;
    private Torneio simulacao;
    private final FirebaseFirestore firestore;


    public Olimpia(){
        torneios = new ArrayList<>();
        simulacao = null;
        firestore = FirebaseFirestore.getInstance();
    }

    public void salvarSantuarioLocal(Olimpia santuario, FileOutputStream fileOutputStream) throws IOException{
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(santuario);
        objectOutputStream.flush();
        objectOutputStream.close();
    }

    public Olimpia carregarSantuarioLocal(FileInputStream fileInputStream) throws IOException {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            Olimpia santuario = (Olimpia) objectInputStream.readObject();
            objectInputStream.close();
            return santuario;
        } catch (ClassNotFoundException ex) {
            throw new IOException("Exceção de classe não esperada.");
        }
    }

    public void salvarSantuarioRemoto(){
        firestore.collection("torneios").add(torneios.get(0))
                .addOnCompleteListener(task -> {
                    if(!task.isSuccessful()){
                        Log.d(TAG, task.getResult().getId());
                    }
                });
    }

    public void carregarSantuarioRemoto(){
        firestore.collection("torneios").
                whereEqualTo("organizador", "tester").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        for (QueryDocumentSnapshot document: task.getResult()) {
                            Torneio aux = document.toObject(Torneio.class);
                            Log.d(TAG, aux.toString());

                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
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
        Torneio tTeste = new Torneio(20000, "XV Torneio do Pirpiri", "tester");
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
        teste.addTorneio(tTeste);
        return teste;
    }
    public static void printteste(Context ctx, String msg){
        Toast.makeText(ctx, msg,
            Toast.LENGTH_SHORT).show();
        System.out.println(msg);
    }
}
