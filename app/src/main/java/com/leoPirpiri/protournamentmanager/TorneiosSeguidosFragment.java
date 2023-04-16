package com.leoPirpiri.protournamentmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import model.Olimpia;
import model.Torneio;
import model.Usuario;

public class TorneiosSeguidosFragment extends Fragment {
    private final String TAG = "TORNEIOS_SEGUIDOS";

    private MainActivity superActivity;
    private TextView txv_msg_torneios_seguidos_recentes;
    private RecyclerView recyclerViewTorneiosSeguidos;
    private TorneiosAdapter adapterTorneioSeguidos;
    private ArrayList<Torneio> listaTorneiosSeguidos;

    private Usuario usuario = new Usuario();
    private FirebaseFirestore firestoreDB;

    //private FirebaseUser nowUser;

    public TorneiosSeguidosFragment() {/* Required empty public constructor*/}

    public TorneiosSeguidosFragment(ArrayList<Torneio> listaTorneiosSeguidos) {
        this.listaTorneiosSeguidos = listaTorneiosSeguidos;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.superActivity = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_torneios_diversos, container, false);

        TableRow tablerow_acao = v.findViewById(R.id.tablerow_acao_torneios_recentes);
        txv_msg_torneios_seguidos_recentes = v.findViewById(R.id.txv_msg_lista_torneios_recentes);
        recyclerViewTorneiosSeguidos = v.findViewById(R.id.recyclerview_torneios_recentes);
        Button btn_simulador_partida = v.findViewById(R.id.btn_simulador_tela_inicio);

        //Características iniciais dos elementos da tela do fragmento
        tablerow_acao.setVisibility(View.GONE);
        btn_simulador_partida.setVisibility(View.GONE);

        firestoreDB = FirebaseFirestore.getInstance();

        listarTorneios();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        povoarRecycleView();
        buscarUsuarioLogado();
    }

    private void buscarUsuarioLogado() {
        if(usuario.getId() == null) {
            firestoreDB.collection("usuarios")
                .document(superActivity.getUsuarioLogado())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if(document.exists()){
                            usuario = document.toObject(Usuario.class);
                            Log.d(TAG, usuario.toString());
                            if(listaTorneiosSeguidos.isEmpty() && !usuario.getTorneiosSeguidos().isEmpty()){
                                buscarTorneiosRemotos();
                            }
                        } else {
                            superActivity.efetuarLogout();
                            Log.d(TAG, task.getResult().getData().toString());
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void buscarTorneiosRemotos() {
        firestoreDB.collection("torneios")
            .whereIn(FieldPath.documentId(), usuario.getTorneiosSeguidos()).get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    QuerySnapshot documents = task.getResult();
                    if(!documents.isEmpty()){
                        for (QueryDocumentSnapshot document : documents) {
                            Torneio torneio = document.toObject(Torneio.class);
                            listaTorneiosSeguidos.add(torneio);
                            Log.d(TAG, torneio.toString());
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                        superActivity.persistirDados();
                        adapterTorneioSeguidos.notifyDataSetChanged();
                        listarTorneios();
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            })
            .addOnFailureListener( exception -> Log.d(TAG, exception.getMessage()));
    }

    private void listarTorneios(){
        if(listaTorneiosSeguidos.isEmpty()){
            txv_msg_torneios_seguidos_recentes.setText(R.string.santuario_seguidos_vazio);
        } else {
            txv_msg_torneios_seguidos_recentes.setText(R.string.santuario_torneios_recentes);
        }
    }

    private void povoarRecycleView(){
        recyclerViewTorneiosSeguidos.setLayoutManager(new LinearLayoutManager(superActivity));
        adapterTorneioSeguidos = new TorneiosAdapter(superActivity, listaTorneiosSeguidos);
        recyclerViewTorneiosSeguidos.setAdapter(adapterTorneioSeguidos);
        construirListenersAdapterTorneio();
    }

    private void construirListenersAdapterTorneio() {
        adapterTorneioSeguidos.setOnClickListener(v -> abrirTorneioSeguido(
                listaTorneiosSeguidos.get(
                        recyclerViewTorneiosSeguidos.getChildAdapterPosition(v)).buscarUuid()));

        adapterTorneioSeguidos.setOnLongClickListener(v -> {
            montarAlertaExcluirTorneio(recyclerViewTorneiosSeguidos.getChildAdapterPosition(v));
            return true;
        });
    }

    private void montarAlertaExcluirTorneio(final int posTorneio){
        AlertDialog.Builder builder = new AlertDialog.Builder(superActivity);
        View view = getLayoutInflater().inflate(R.layout.alerta_default, null);

        Button btn_confirmar = view.findViewById(R.id.btn_confirmar_default);
        Button btn_cancelar = view.findViewById(R.id.btn_cancelar_default);
        TextView msg = view.findViewById(R.id.msg_alerta_default);
        btn_confirmar.setVisibility(View.VISIBLE);
        btn_cancelar.setVisibility(View.VISIBLE);
        msg.setVisibility(View.VISIBLE);
        msg.setText(R.string.msg_alerta_confir_unfollow_torneio);

        btn_confirmar.setOnClickListener(arg0 -> {
            superActivity.esconderAlerta();
            excluirTorneioSeguido(posTorneio);
        });

        btn_cancelar.setOnClickListener(arg0 -> superActivity.esconderAlerta());

        builder.setView(view);
        builder.setTitle(R.string.titulo_alerta_confir_unfollow_torneio);
        superActivity.mostrarAlerta(builder);
    }

    private void abrirTorneioSeguido(String torneioId){
        superActivity.abrirTorneio(torneioId);
    }

    private void excluirTorneioSeguido(int position) {
        if (superActivity.excluirTorneio(position)){
            adapterTorneioSeguidos.notifyItemRemoved(position);
            listarTorneios();
        }
    }
}
/*
        Button btn_teste = v.findViewById(R.id.btn_teste);
        txv_teste = v.findViewById(R.id.txv_teste);
        nowUser = FirebaseAuth.getInstance().getCurrentUser();
        if (nowUser.getEmail().equals("teste@gmail.com")){
            btn_teste.setEnabled(false);
            btn_teste.setVisibility(View.INVISIBLE);
            acionarSnapshot();
        }
        //Listeners
        btn_teste.setOnClickListener(v1 -> {
            enviarValor();
        });

    private void acionarSnapshot(){
        firestoreDB.collection("/teste")
                .document("tester")
                .addSnapshotListener((value, e) -> {
                    if (value.exists()){
                        txv_teste.setText(value.get("valor").toString());
                    }
                });
    }

    private void enviarValor() {
        Map<String, Object> mapa = new HashMap<>();
        mapa.put("valor", ++torneio);
        firestoreDB.collection("/teste")
                .document("tester")
                .set(mapa)
                .addOnSuccessListener(onSuccessListener -> {
                    txv_teste.setText(String.valueOf(torneio));
                })
                .addOnFailureListener(e -> {
                    txv_teste.setText("Deu Errado");
                });
    }*/

