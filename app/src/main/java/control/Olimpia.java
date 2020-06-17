package control;

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

    public Olimpia(){
        torneios = new ArrayList<>();
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

    public Torneio getTorneio(int index){
        for (Torneio t : torneios) {
            if (t.getId() == index){
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
        ArrayList index = new ArrayList();
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

    //retorna o id completo de uma entidade de nível 0
    public int extrairIdEntidadeSuperiorLv0(int id){
        //Do id 102030 retorna 100000
        return ((id/10000)*10000);
    }

    //retorna o id completo de uma entidade de nível 1
    public int extrairIdEntidadeSuperiorLv1(int id){
        //Do id 102030 retorna 102000
        return ((id/100)*100);
    }

    public Olimpia testeExemplos (Olimpia teste){
        Torneio tTeste = new Torneio(teste.getNovoTorneioId(), "Taça Pirpiri de Futsal");
        Equipe eTeste = new Equipe(tTeste.getNovoElementoId(tTeste.TIPO_TIME), "Maciel de Cima Futsal", "MCF");
        Jogador jTeste = new Jogador(eTeste.getNovoJogadorId(), "João Cana-brava", 3, 2);
        eTeste.addJogador(jTeste);
        jTeste = new Jogador(eTeste.getNovoJogadorId(), "Nome do goleiro", 4, 1);
        eTeste.addJogador(jTeste);
        tTeste.addTime(eTeste);
        teste.addTorneio(tTeste);
        return teste;
    }
}
