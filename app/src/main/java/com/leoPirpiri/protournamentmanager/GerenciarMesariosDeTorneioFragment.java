package com.leoPirpiri.protournamentmanager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Locale;

import adapters.MesariosAdapter;
import adapters.TorneiosAdapter;
import model.Torneio;
import model.Usuario;


public class GerenciarMesariosDeTorneioFragment extends Fragment {
    private final String TAG = "GERENCIADOR_DE_MESARIOS";

    private TorneioActivity superActivity;
    private MesariosAdapter adapterMesario;
    private RecyclerView recyclerViewMesarios;
    private TextView txv_msg_mesarios_recentes;
    private TorneiosAdapter adapterTorneioSeguidos;
    private Torneio torneio;
    private ArrayList<Usuario> listaDeMesarios = new ArrayList<>();

    private FirebaseFirestore firestoreDB;

    public GerenciarMesariosDeTorneioFragment() {/*Required empty public constructor*/}

    public GerenciarMesariosDeTorneioFragment(Torneio torneio) {
        this.torneio = torneio;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.superActivity = (TorneioActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mesarios_torneio, container, false);
        txv_msg_mesarios_recentes = v.findViewById(R.id.txv_msg_lista_mesarios_recentes);
        recyclerViewMesarios = v.findViewById(R.id.recyclerview_mesarios_recentes);

        //Listeners
        v.findViewById(R.id.btn_buscar_mesario).setOnClickListener(v1 -> buscarNovoMesario(
                ((TextView) v.findViewById(R.id.etx_apelido_mesario)).getText().toString()
        ));

        firestoreDB = FirebaseFirestore.getInstance();

        povoarRecycleView();
        listarMesarios();
        buscarMesarios();
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void listarMesarios(){
        if(torneio.getGerenciadores().size()==1){
            txv_msg_mesarios_recentes.setText(R.string.msg_torneio_sem_mesarios);
        } else {
            txv_msg_mesarios_recentes.setText(R.string.msg_torneio_com_mesarios);
        }
    }

    private void povoarRecycleView(){
        recyclerViewMesarios.setLayoutManager(new LinearLayoutManager(superActivity));
        adapterMesario = new MesariosAdapter(superActivity, listaDeMesarios);
        recyclerViewMesarios.setAdapter(adapterMesario);
        construirListenersAdapterMesario();
    }

    private void construirListenersAdapterMesario() {
    }

    private void buscarMesarios() {
        if(torneio.getGerenciadores().size()>1){
            firestoreDB.collection("usuarios").
                whereIn(FieldPath.documentId(), torneio.getGerenciadores().subList(1, torneio.getGerenciadores().size())).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot documents = task.getResult();
                        if(!documents.isEmpty()){
                            for (QueryDocumentSnapshot document : documents) {
                                Usuario mesario = document.toObject(Usuario.class);
                                listaDeMesarios.add(mesario);
                                Log.d(TAG, mesario.toString());
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            adapterMesario.notifyDataSetChanged();
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });

        }
    }

    private void buscarNovoMesario(String apelidioUsuario){
        if(!apelidioUsuario.isEmpty()){
            firestoreDB.collection("usuarios").
                whereEqualTo("apelido", apelidioUsuario.toLowerCase()).get().
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot documents = task.getResult();
                        if(!documents.isEmpty()){
                            Usuario mesario = documents.getDocuments().get(0).toObject(Usuario.class);
                            Log.d(TAG, mesario.toString());
                            listaDeMesarios.add(mesario);
                            torneio.adicionarMesario(mesario.getId());
                            adapterMesario.notifyItemInserted(listaDeMesarios.size()-1);
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
            });
        }
    }

}