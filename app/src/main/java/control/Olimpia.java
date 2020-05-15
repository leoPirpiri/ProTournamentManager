package control;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;

import model.Torneio;
//Os Jogos olímpicos da antiguidade eram disputados no santuário de Olímpia.
public class Olimpia implements Serializable {
    private ArrayList<Torneio> torneios;

    public Olimpia(){
        torneios = new ArrayList<>();
    }

    public static void salvarSantuario (){

    }

    public static Olimpia carregarSantuario(String origem) throws IOException {
        try {
            FileInputStream fileInputStream = new FileInputStream(origem);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            Olimpia santuario = (Olimpia) objectInputStream.readObject();
            objectInputStream.close();
            return santuario;
        } catch (ClassNotFoundException ex) {
            throw new IOException("Exceção não esperada.");
        }
    }

    public void addTorneio(Torneio torneio){
        this.torneios.add(torneio);
    }

}
