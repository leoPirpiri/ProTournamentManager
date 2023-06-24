package model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class Usuario {
    private static final String TAG = "USUARIO_CONECTION";
    private String id;
    private String apelido;
    private String email;
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

    public String getEmail() { return email; }

    public ArrayList<String> getTorneiosSeguidos() {
        return torneiosSeguidos;
    }

    public void setTorneiosSeguidos(ArrayList<String> torneiosSeguidos) {
        this.torneiosSeguidos = torneiosSeguidos;
    }

    public boolean ComecarSeguirTorneio(String uuidTorneio){
        if(torneiosSeguidos.size()<Olimpia.MAX_TORNEIOS_SEGUIDOS && !torneiosSeguidos.contains(uuidTorneio)){
            this.torneiosSeguidos.add(uuidTorneio);
            return true;
        }
        return false;
    }

    public boolean deixarSeguirTorneio(String uidTorneio) {
        return this.torneiosSeguidos.remove(uidTorneio);
    }

    public void setId(String uid) {
        this.id = uid;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void atualizarUsuario(FirebaseFirestore db){
         db.collection("usuarios").
                document(this.getId()).
                update("torneiosSeguidos", this.torneiosSeguidos).
                addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!")).
                addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
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
