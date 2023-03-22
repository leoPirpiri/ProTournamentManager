package model;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Usuario {
    private String id;
    private String apelido;
    private ArrayList<String> torneiosSeguidos;

    public Usuario(){
        this.torneiosSeguidos = new ArrayList<>();
    }

    public Usuario(String id, String apelido) {
        this.id = id;
        this.apelido = apelido;
    }

    public String getId() {
        return id;
    }

    public String getApelido() {
        return apelido;
    }

    public ArrayList<String> getTorneiosSeguidos() {
        return torneiosSeguidos;
    }

    public void setTorneiosSeguidos(ArrayList<String> torneiosSeguidos) {
        this.torneiosSeguidos = torneiosSeguidos;
    }

    public void addTorneioSeguido (String uidTornieo){
        this.torneiosSeguidos.add(uidTornieo);
    }

    public void setId(String uid) {
        this.id = uid;
    }

    @NonNull
    @Override
    public String toString() {
        return "Usuario{" +
                "id='" + id + '\'' +
                ", apelido='" + apelido + '\'' +
                ", torneiosSeguidos=" + torneiosSeguidos.size() +
                '}';
    }
}
