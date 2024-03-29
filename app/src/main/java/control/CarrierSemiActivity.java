package control;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.leoPirpiri.protournamentmanager.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import model.Olimpia;
import model.Torneio;

public final class CarrierSemiActivity {
    private static final String TAG = "SEMIACTIVITY";

    public static void persistirSantuario(Context ctx, Olimpia santuarioOlimpia){
        try {
            salvarSantuarioLocal(santuarioOlimpia,
                    ctx.openFileOutput(Olimpia.NOME_ARQUIVO_SERIALIZADO, Context.MODE_PRIVATE));
            Toast.makeText(ctx, R.string.dados_salvando, Toast.LENGTH_LONG).show();
            Toast.makeText(ctx, R.string.dados_salvo, Toast.LENGTH_SHORT).show();
        } catch (IOException ex) {
            System.out.println("\nSalvar\n"+ex.getMessage());//apagar quando der certo.
            Toast.makeText(ctx, R.string.dados_erro_gravar_santuario, Toast.LENGTH_LONG).show();
        }
    }

    public static Olimpia carregarSantuario(Context ctx) {
        Olimpia santuarioOlimpia = new Olimpia();
        if (new File(ctx.getFileStreamPath(Olimpia.NOME_ARQUIVO_SERIALIZADO).toString()).exists()) {
            try {
                santuarioOlimpia = carregarSantuarioLocal(ctx.openFileInput(Olimpia.NOME_ARQUIVO_SERIALIZADO));
            } catch (IOException ex) {
                System.out.println("\nCarregar\n" + ex.getMessage());//apagar quando der certo.
                Toast.makeText(ctx, R.string.dados_erro_leitura_santuario, Toast.LENGTH_LONG).show();
            }
        }
        return santuarioOlimpia;
    }

    private static void salvarSantuarioLocal(Olimpia santuario, FileOutputStream fileOutputStream) throws IOException{
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(santuario);
        objectOutputStream.flush();
        objectOutputStream.close();
    }

    private static Olimpia carregarSantuarioLocal(FileInputStream fileInputStream) throws IOException {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            Olimpia santuario = (Olimpia) objectInputStream.readObject();
            objectInputStream.close();
            return santuario;
        } catch (ClassNotFoundException ex) {
            throw new IOException("Exceção de classe não esperada.");
        }
    }

    public static boolean salvarSantuarioRemoto(Torneio torneio, FirebaseFirestore database){
        AtomicBoolean atualizar = new AtomicBoolean(false);
        database.collection("torneios")
                .document(torneio.buscarUuid())
                .set(torneio)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                    torneio.setDataAtualizacaoRemota(torneio.getDataAtualizacaoLocal());
                    atualizar.set(true);
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
        return atualizar.get();
    }

    public static void limparSantuarioLocal(Context ctx){
        File arq = new File(ctx.getFileStreamPath(Olimpia.NOME_ARQUIVO_SERIALIZADO).toString());
        if (arq.exists()) arq.delete();
    }
/*
    public static ArrayList<Torneio> carregarSantuarioRemotoGerenciados(String userUid){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        ArrayList<Torneio> torneiosGerenciados = new ArrayList<>();
        firestore.collection("torneios").
                whereEqualTo("organizadorId", userUid).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        for (QueryDocumentSnapshot document: task.getResult()) {
                            Torneio aux = document.toObject(Torneio.class);
                            torneiosGerenciados.add(aux);
                            Log.d(TAG, aux.toString());
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
        return torneiosGerenciados;
    }*/
    public static ArrayList<Torneio> carregarSantuarioRemotoSeguidos(ArrayList<String> listaTorneiosSeguidos){
        return new ArrayList<>();
    }

    public static void exemplo(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
    }
}
