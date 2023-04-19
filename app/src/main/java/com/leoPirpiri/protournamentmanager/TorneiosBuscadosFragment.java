package com.leoPirpiri.protournamentmanager;

import android.annotation.SuppressLint;
import android.content.Context;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import adapters.TorneiosAdapter;
import model.Torneio;
import model.Usuario;


public class TorneiosBuscadosFragment extends Fragment {
    private final String TAG = "TORNEIOS_GERENCIADOS";

    private MainActivity superActivity;
    private TorneiosAdapter adapterTorneio;
    private RecyclerView recyclerViewTorneiosBuscados;
    private TextView txv_msg_torneios_buscados_recentes;
    private ArrayList<Torneio> listaTorneiosBuscados;
    private EditText etx_buscar_torneio;
    private Button btn_buscar_torneio;

    private Usuario usuario = new Usuario();
    private FirebaseFirestore firestoreDB;

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
        btn_buscar_torneio = v.findViewById(R.id.btn_torneios_recentes);
        txv_msg_torneios_buscados_recentes = v.findViewById(R.id.txv_msg_lista_torneios_recentes);
        recyclerViewTorneiosBuscados = v.findViewById(R.id.recyclerview_torneios_recentes);
        Button btn_simulador_partida = v.findViewById(R.id.btn_simulador_tela_inicio);

        //Características iniciais dos elementos da tela do fragmento
        etx_buscar_torneio.setHint(R.string.hint_frag_torneios_etx_buscar);
        btn_buscar_torneio.setText(R.string.lbl_btn_buscar);
        btn_simulador_partida.setVisibility(View.GONE);

        firestoreDB = FirebaseFirestore.getInstance();

        //Listeners
        etx_buscar_torneio.setOnFocusChangeListener((v1, hasFocus) -> {
            if (!hasFocus) {
                superActivity.esconderTeclado(etx_buscar_torneio);
            }
        });

        btn_buscar_torneio.setOnClickListener(v12 -> {
            String torneioBuscado = etx_buscar_torneio.getText().toString().toLowerCase();
            if (superActivity != null && !torneioBuscado.equals("")){
                superActivity.esconderTeclado(etx_buscar_torneio);
                buscarTorneiosParaSeguir(Arrays.stream(torneioBuscado.split(" "))
                                               .filter(s -> s.length()>2)
                                               .collect(Collectors.toList()));
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        povoarRecycleView();
        buscarUsuarioLogado();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void listarTorneios(){
        if(listaTorneiosBuscados.isEmpty()){
            txv_msg_torneios_buscados_recentes.setText(R.string.santuario_buscados_vazio);
        } else {
            txv_msg_torneios_buscados_recentes.setText(R.string.santuario_buscados_encontrados);
        }
        adapterTorneio.notifyDataSetChanged();
    }

    private void povoarRecycleView(){
        recyclerViewTorneiosBuscados.setLayoutManager(new LinearLayoutManager(superActivity));
        adapterTorneio = new TorneiosAdapter(superActivity, listaTorneiosBuscados);
        recyclerViewTorneiosBuscados.setAdapter(adapterTorneio);
        construirListenersAdapterTorneio();
    }

    private void construirListenersAdapterTorneio() {
        adapterTorneio.setOnClickListener(v -> montarAlertaSeguirTorneio(recyclerViewTorneiosBuscados.getChildAdapterPosition(v)));

        adapterTorneio.setOnLongClickListener(v -> {
            // Não há ação pensada no momento.
            return true;
        });
    }

    private void montarAlertaSeguirTorneio(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(superActivity);
        View view = getLayoutInflater().inflate(R.layout.alerta_default, null);

        Button btn_confirmar = view.findViewById(R.id.btn_confirmar_default);
        Button btn_cancelar = view.findViewById(R.id.btn_cancelar_default);
        TextView msg = view.findViewById(R.id.msg_alerta_default);
        btn_confirmar.setVisibility(View.VISIBLE);
        btn_cancelar.setVisibility(View.VISIBLE);
        msg.setVisibility(View.VISIBLE);
        msg.setText(R.string.msg_alerta_confir_follow_torneio);

        btn_confirmar.setOnClickListener(arg0 -> seguirTorneio(listaTorneiosBuscados.get(position).buscarUuid()));

        btn_cancelar.setOnClickListener(arg0 -> superActivity.esconderAlerta());

        builder.setView(view);
        builder.setTitle(R.string.titulo_alerta_confir_unfollow_torneio);
        superActivity.mostrarAlerta(builder);
    }

    private void seguirTorneio(String torneioUuid) {
        usuario.addTorneioSeguido(torneioUuid);
        if (usuario.atualizarUsuario(firestoreDB)){
            superActivity.abrirTorneio(torneioUuid);
            superActivity.esconderAlerta();
        }
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
                                Log.d(TAG, usuario != null ? usuario.toString() : null);
                                btn_buscar_torneio.setEnabled(true);
                            } else {
                                superActivity.efetuarLogout();
                                Log.d(TAG, Objects.requireNonNull(task.getResult().getData()).toString());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });
        }
    }

    private void buscarTorneiosParaSeguir(List<String> busca) {
        firestoreDB.collection("torneios")
            .whereArrayContainsAny("nomeFragmentado", busca)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    QuerySnapshot documents = task.getResult();
                    if(!documents.isEmpty()){
                        listaTorneiosBuscados.clear();
                        for (QueryDocumentSnapshot document : documents) {
                            Torneio torneio = document.toObject(Torneio.class);
                            if (!torneio.getGerenciadores().contains(usuario.getId()) &&
                                !usuario.getTorneiosSeguidos().contains(torneio.buscarUuid()) ) {
                                listaTorneiosBuscados.add(torneio);
                            }
                            Log.d(TAG, torneio.toString());
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                        listarTorneios();
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            });
    }
}