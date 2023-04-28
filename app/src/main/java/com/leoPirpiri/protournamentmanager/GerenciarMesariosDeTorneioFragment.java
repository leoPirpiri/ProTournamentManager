package com.leoPirpiri.protournamentmanager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import adapters.MesariosAdapter;
import model.Torneio;
import model.Usuario;


public class GerenciarMesariosDeTorneioFragment extends Fragment {
    private final String TAG = "GERENCIADOR_DE_MESARIOS";

    private TorneioActivity superActivity;
    private MesariosAdapter adapterMesario;
    private RecyclerView recyclerViewMesarios;
    private TextView etx_novo_mesario;
    private TextView txv_msg_mesarios_recentes;
    private Torneio torneio;
    private ArrayList<Usuario> listaDeMesarios;

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
        etx_novo_mesario = v.findViewById(R.id.etx_apelido_mesario);

        //Listeners
        v.findViewById(R.id.btn_buscar_mesario).setOnClickListener(v1 -> {
            buscarNovoMesario(etx_novo_mesario.getText().toString());
            superActivity.esconderTeclado(etx_novo_mesario);
        });

        etx_novo_mesario.setOnFocusChangeListener((v1, hasFocus) -> {if (!hasFocus) superActivity.esconderTeclado(etx_novo_mesario);});

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
        listaDeMesarios = new ArrayList<>();
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
                            adapterMesario.notifyItemRangeChanged(0, listaDeMesarios.size());
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
                            montarAlertaAdicionarMesario(mesario);
                        } else {
                            Toast.makeText(superActivity, R.string.msg_alerta_erro_novo_mesario_torneio_nao_encontrado, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
            });
        }
    }

    private void montarAlertaAdicionarMesario(Usuario novoMesario){
        AlertDialog.Builder builder = new AlertDialog.Builder(superActivity);
        View view = getLayoutInflater().inflate(R.layout.alerta_default, null);

        Button btn_confirmar = view.findViewById(R.id.btn_confirmar_default);
        Button btn_cancelar = view.findViewById(R.id.btn_cancelar_default);
        TextView msg = view.findViewById(R.id.msg_alerta_default);
        btn_cancelar.setVisibility(View.VISIBLE);
        msg.setVisibility(View.VISIBLE);
        if (torneio.getGerenciadores().contains(novoMesario.getId())){
            msg.setText(R.string.msg_alerta_erro_novo_mesario_torneio_existente);
            etx_novo_mesario.setText("");
        } else if (torneio.getGerenciadores().size() <= Torneio.MAX_GERENCIADORES){
            btn_confirmar.setVisibility(View.VISIBLE);
            msg.setText(String.format("%s\n%s",
                                      getString(R.string.msg_alerta_confir_novo_mesario_torneio),
                                      novoMesario.getApelido().toUpperCase()));
        } else {
            etx_novo_mesario.setText("");
            msg.setText(R.string.msg_alerta_erro_novo_mesario_torneio);
        }

        btn_confirmar.setOnClickListener(arg0 -> {
            etx_novo_mesario.setText("");
            listaDeMesarios.add(novoMesario);
            adapterMesario.notifyItemInserted(listaDeMesarios.size()-1);
            torneio.adicionarMesario(novoMesario.getId());
            listarMesarios();
            superActivity.persistirDadosSantuario();
            superActivity.esconderAlerta();
        });

        btn_cancelar.setOnClickListener(arg0 -> superActivity.esconderAlerta());

        builder.setView(view);
        builder.setTitle(R.string.titulo_alerta_novo_mesario_torneio);
        superActivity.mostrarAlerta(builder);
    }

}