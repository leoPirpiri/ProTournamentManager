package control;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

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
            //Retorna o índice do elemento que está sendo adicionado
            return torneios.indexOf(torneio);
        } else {
            return -1;
        }
    }

    public Torneio getTorneio(int pos){
        return torneios.get(pos);
    }

    public ArrayList<Torneio> getTorneios(){
        return torneios;
    }

    public int getOcupacao(){
        return this.torneios.size();
    }
}
