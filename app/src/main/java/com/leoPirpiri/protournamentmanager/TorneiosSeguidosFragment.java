package com.leoPirpiri.protournamentmanager;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import control.Olimpia;
import model.Torneio;

public class TorneiosSeguidosFragment extends Fragment {

    private MainActivity superActivity;
    private TorneiosAdapter adapterTorneio;
    private RecyclerView recyclerViewTorneiosSeguidos;
    private TextView txv_torneios_seguidos_recentes;
    private ArrayList<Torneio> listaTorneiosSeguidos;
    private TextView etx_nome_buscar_seguir_torneio;
    private Button btn_seguir_torneio;

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
        View v = inflater.inflate(R.layout.fragment_torneios_seguidos, container, false);
        recyclerViewTorneiosSeguidos = v.findViewById(R.id.recyclerview_torneios_seguidos);
        txv_torneios_seguidos_recentes = v.findViewById(R.id.txv_torneios_seguidos_recentes);
        etx_nome_buscar_seguir_torneio = v.findViewById(R.id.nome_buscar_seguir_torneio);
        btn_seguir_torneio = v.findViewById(R.id.btn_seguir_tourneio);
        
        desabilitarBtnNovoTorneio();
        listarTorneios();
        povoarRecycleView();

        //Listeners
        etx_nome_buscar_seguir_torneio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(etx_nome_buscar_seguir_torneio.getText().toString().equals("")){
                    desabilitarBtnNovoTorneio();
                }else{
                    btn_seguir_torneio.setEnabled(true);
                    btn_seguir_torneio.setBackground(ContextCompat.getDrawable(superActivity, R.drawable.button_shape_enabled));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        etx_nome_buscar_seguir_torneio.setOnFocusChangeListener((v1, hasFocus) -> {
            if (!hasFocus) {
                superActivity.esconderTeclado(etx_nome_buscar_seguir_torneio);
            }
        });

        btn_seguir_torneio.setOnClickListener(v12 -> {
            if (superActivity != null){
                superActivity.esconderTeclado(etx_nome_buscar_seguir_torneio);
                seguirNovoTorneio(etx_nome_buscar_seguir_torneio.getText().toString());
            }
        });

        return v;
    }

    private void desabilitarBtnNovoTorneio() {
        btn_seguir_torneio.setEnabled(false);
        btn_seguir_torneio.setBackground(
                ContextCompat.getDrawable(superActivity, R.drawable.button_shape_desabled)
        );
    }
    private void listarTorneios(){
        if(listaTorneiosSeguidos.isEmpty()){
            txv_torneios_seguidos_recentes.setText(R.string.santuario_seguidos_vazio);
        } else {
            txv_torneios_seguidos_recentes.setText(R.string.santuario_torneios_recentes);
        }
    }

    private void povoarRecycleView(){
        recyclerViewTorneiosSeguidos.setLayoutManager(new LinearLayoutManager(superActivity));
        adapterTorneio = new TorneiosAdapter(superActivity, listaTorneiosSeguidos);
        recyclerViewTorneiosSeguidos.setAdapter(adapterTorneio);
        construirListenersAdapterTorneio();
    }

    private void construirListenersAdapterTorneio() {
        adapterTorneio.setOnClickListener(v -> abrirTorneioSeguido(
                listaTorneiosSeguidos.get(
                        recyclerViewTorneiosSeguidos.getChildAdapterPosition(v)).getId()));

        adapterTorneio.setOnLongClickListener(v -> {
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
    private void seguirNovoTorneio(String nomeNovo){
        Olimpia.printteste(superActivity, "Novo Torneio Seguido: "+ nomeNovo);
    }

    private void abrirTorneioSeguido(int torneioId){
        superActivity.abrirTorneio(torneioId);
    }

    private void excluirTorneioSeguido(int position) {
        if (superActivity.excluirTorneio(position)){
            adapterTorneio.notifyItemRemoved(position);
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
        FirebaseFirestore.getInstance().collection("/teste")
                .document("tester")
                .addSnapshotListener((value, e) -> {
                    if (value.exists()){
                        txv_teste.setText(value.get("valor").toString());
                    }
                });
    }

    private void enviarValor() {
        Map<String, Object> mapa = new HashMap<>();
        mapa.put("valor", ++aux);
        FirebaseFirestore.getInstance().collection("/teste")
                .document("tester")
                .set(mapa)
                .addOnSuccessListener(onSuccessListener -> {
                    txv_teste.setText(String.valueOf(aux));
                })
                .addOnFailureListener(e -> {
                    txv_teste.setText("Deu Errado");
                });
    }*/

