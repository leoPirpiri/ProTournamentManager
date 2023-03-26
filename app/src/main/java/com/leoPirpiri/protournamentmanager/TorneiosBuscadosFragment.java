package com.leoPirpiri.protournamentmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import model.Olimpia;
import model.Torneio;


public class TorneiosBuscadosFragment extends Fragment {
    private final String TAG = "TORNEIOS_GERENCIADOS";

    private MainActivity superActivity;
    private TorneiosAdapter adapterTorneio;
    private RecyclerView recyclerViewTorneiosBuscados;
    private TextView txv_msg_torneios_buscados_recentes;
    private ArrayList<Torneio> listaTorneiosBuscados;
    private EditText etx_buscar_torneio;
    private String ultimaBusca = "";

    public TorneiosBuscadosFragment() {
        // Required empty public constructor
    }

    public TorneiosBuscadosFragment(ArrayList<Torneio> listaTorneiosBuscados) {
        this.listaTorneiosBuscados = listaTorneiosBuscados;
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

        etx_buscar_torneio = v.findViewById(R.id.etx_torneios_recentes);
        Button btn_buscar_torneio = v.findViewById(R.id.btn_torneios_recentes);
        txv_msg_torneios_buscados_recentes = v.findViewById(R.id.txv_msg_lista_torneios_recentes);
        recyclerViewTorneiosBuscados = v.findViewById(R.id.recyclerview_torneios_recentes);
        Button btn_simulador_partida = v.findViewById(R.id.btn_simulador_tela_inicio);

        //Características iniciais dos elementos da tela do fragmento
        etx_buscar_torneio.setHint(R.string.hint_frag_torneios_etx_buscar);
        btn_buscar_torneio.setText(R.string.lbl_btn_buscar);
        btn_simulador_partida.setVisibility(View.GONE);


        //Listeners
        etx_buscar_torneio.setOnFocusChangeListener((v1, hasFocus) -> {
            if (!hasFocus) {
                superActivity.esconderTeclado(etx_buscar_torneio);
            }
        });

        btn_buscar_torneio.setOnClickListener(v12 -> {
            if (superActivity != null && !etx_buscar_torneio.getText().toString().equals("")){
                superActivity.esconderTeclado(etx_buscar_torneio);
                Olimpia.printteste(superActivity, "Buscando torneio");
                //buscarTorneio(etx_buscar_torneio.getText().toString());
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void listarTorneios(){
        if(listaTorneiosBuscados.isEmpty()){
            txv_msg_torneios_buscados_recentes.setText(R.string.santuario_buscados_vazio);
        } else {
            txv_msg_torneios_buscados_recentes.setText(R.string.santuario_buscados_encontrados);
        }
        povoarRecycleView();
    }

    private void povoarRecycleView(){
        recyclerViewTorneiosBuscados.setLayoutManager(new LinearLayoutManager(superActivity));
        adapterTorneio = new TorneiosAdapter(superActivity, listaTorneiosBuscados);
        recyclerViewTorneiosBuscados.setAdapter(adapterTorneio);
        construirListenersAdapterTorneio();
    }

    private void construirListenersAdapterTorneio() {
        adapterTorneio.setOnClickListener(v -> {
            //montarAlertaSeguirTorneio(recyclerViewTorneiosBuscados.getChildAdapterPosition(v));
        });

        adapterTorneio.setOnLongClickListener(v -> {
            //montarAlertaExcluirTorneio(recyclerViewTorneiosBuscados.getChildAdapterPosition(v));
            return true;
        });
    }

/*

    private void buscarTorneiosParaSeguir() {
        firestoreDB.collection("torneios")
            .whereNotEqualTo("organizadorId", usuario.getId())
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    QuerySnapshot documents = task.getResult();
                    if(!documents.isEmpty()){
                        for (QueryDocumentSnapshot document : documents) {
                            Torneio torneio = document.toObject(Torneio.class);
                            if (torneio.getNome().contains(ultimaBusca)
                                && !listaAdapterNomesTorneiosParaSeguir.contains(torneio.getNome())
                                && !usuario.getTorneiosSeguidos().contains(document.getId())){
                                listaTorneiosParaSequir.add(torneio);
                                listaAdapterNomesTorneiosParaSeguir.add(torneio.getNome());
                            }
                            Log.d(TAG, torneio.toString());
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
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
        msg.setText(R.string.msg_alerta_confir_excluir_torneio);

        btn_confirmar.setOnClickListener(arg0 -> {
            superActivity.esconderAlerta();
            excluirTorneioGerenciado(posTorneio);
        });

        btn_cancelar.setOnClickListener(arg0 -> superActivity.esconderAlerta());

        builder.setView(view);
        builder.setTitle(R.string.titulo_alerta_confir_excluir_torneio);
        superActivity.mostrarAlerta(builder);
    }
    private void criarTorneioGerenciado(String nomeNovoTorneio){
        Torneio novoTorneio = superActivity.criarNovoTorneio(nomeNovoTorneio);
        if (novoTorneio!=null){
            desabilitarBtnNovoTorneio();
            etx_buscar_torneio.setText("");
            superActivity.abrirTorneio(novoTorneio.getId());
        } else {
            etx_buscar_torneio.setError(getString(R.string.erro_numero_max_torneios));
        }
    }

    private void abrirTorneioBuscado(int torneioId){
        superActivity.abrirTorneio(torneioId);
    }

    private void excluirTorneioGerenciado(int position) {
        if (superActivity.excluirTorneio(position)){
            adapterTorneio.notifyItemRemoved(position);
            listarTorneios();
        }
    }

    private void abrirSimulador(){
        Intent intent = new Intent(superActivity, PartidaActivity.class);
        intent.putExtra("partida", -1);
        startActivity(intent);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void buscarTorneiosRemotos() {
        if(listaTorneiosBuscados.isEmpty()) {
            FirebaseFirestore.getInstance().collection("torneios").
                whereEqualTo("organizadorId", superActivity.getUsuarioLogado()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot documents = task.getResult();
                        if(!documents.isEmpty()){
                            for (QueryDocumentSnapshot document : documents) {
                                Torneio aux = document.toObject(Torneio.class);
                                listaTorneiosBuscados.add(aux);
                                Log.d(TAG, aux.toString());
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            superActivity.persistirDados();
                            adapterTorneio.notifyDataSetChanged();
                            listarTorneios();
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
        }
    }*/
}