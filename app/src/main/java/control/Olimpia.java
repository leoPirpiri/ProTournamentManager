package control;

import java.io.File;
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
    public static File file;

    private ArrayList<Torneio> torneios;

    public Olimpia(){
        torneios = new ArrayList<>();
    }

    public static void salvarSantuario( Olimpia santuario) throws IOException{
        salvarSantuario(santuario, NOME_ARQUIVO_SERIALIZADO);
    }

    public static void salvarSantuario (Olimpia santuario, String destino) throws IOException{

        FileOutputStream fileOutputStream =  new FileOutputStream(destino);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(santuario);
        objectOutputStream.flush();
        objectOutputStream.close();
    }

    public static Olimpia carregarSantuario() throws IOException{
        return carregarSantuario(NOME_ARQUIVO_SERIALIZADO);
    }

    public static Olimpia carregarSantuario(String origem) throws IOException {
        try {
            FileInputStream fileInputStream = new FileInputStream(origem);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            Olimpia santuario = (Olimpia) objectInputStream.readObject();
            objectInputStream.close();
            return santuario;
        } catch (ClassNotFoundException ex) {
            throw new IOException("Exceção de classe não esperada.");
        }
    }

    public void addTorneio(Torneio torneio){
        this.torneios.add(torneio);
    }

}
