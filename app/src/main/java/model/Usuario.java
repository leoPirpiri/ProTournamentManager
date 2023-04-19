package model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Usuario {
    private static final String TAG = "USUARIO_CONECTION";
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

    public void addTorneioSeguido (String uidTorneio){
        this.torneiosSeguidos.add(uidTorneio);
    }

    public boolean dellTroneioSeguido (String uidTorneio) { return this.torneiosSeguidos.remove(uidTorneio); }

    public void setId(String uid) {
        this.id = uid;
    }

    public boolean atualizarUsuario(FirebaseFirestore db){
        AtomicBoolean sucesso = new AtomicBoolean(false);
        db.collection("usuarios").
                document(this.getId()).
                update("torneiosSeguidos", this.torneiosSeguidos).
                addOnSuccessListener(aVoid -> {
                    sucesso.set(true);
                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                }).
                addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
        return sucesso.get();
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
