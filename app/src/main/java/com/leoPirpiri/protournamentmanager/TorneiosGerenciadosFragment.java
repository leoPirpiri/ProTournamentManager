package com.leoPirpiri.protournamentmanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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


public class TorneiosGerenciadosFragment extends Fragment {

    private MainActivity superActivity;
    private TorneiosAdapter adapterTorneio;
    private RecyclerView recyclerViewTorneiosGerenciados;
    private TextView txv_torneios_gerenciados_recentes;
    private ArrayList<Torneio> listaTorneiosGerenciados;
    private TextView etx_nome_novo_torneio;
    private Button btn_novo_torneio;

    public TorneiosGerenciadosFragment() {
        // Required empty public constructor
    }

    public TorneiosGerenciadosFragment(ArrayList<Torneio> listaTorneiosGerenciados) {
        this.listaTorneiosGerenciados = listaTorneiosGerenciados;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.superActivity = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_torneios_gerenciados, container, false);
        recyclerViewTorneiosGerenciados = v.findViewById(R.id.recyclerview_torneios_gerenciados);
        txv_torneios_gerenciados_recentes = v.findViewById(R.id.txv_torneios_gerenciados_recentes);
        etx_nome_novo_torneio = v.findViewById(R.id.nome_novo_torneio);
        btn_novo_torneio = v.findViewById(R.id.btn_novo_tourneio);
        Button btn_simulador_partida = v.findViewById(R.id.btn_simulador_tela_inicio);

        desabilitarBtnNovoTorneio();
        listarTorneios();
        povoarRecycleView();

        //Listeners
        etx_nome_novo_torneio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(etx_nome_novo_torneio.getText().toString().equals("")){
                    desabilitarBtnNovoTorneio();
                }else{
                    btn_novo_torneio.setEnabled(true);
                    btn_novo_torneio.setBackground(ContextCompat.getDrawable(superActivity, R.drawable.button_shape_enabled));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        etx_nome_novo_torneio.setOnFocusChangeListener((v1, hasFocus) -> {
            if (!hasFocus) {
                superActivity.esconderTeclado(etx_nome_novo_torneio);
            }
        });

        btn_novo_torneio.setOnClickListener(v12 -> {
            if (superActivity != null){
                superActivity.esconderTeclado(etx_nome_novo_torneio);
                criarTorneioGerenciado(etx_nome_novo_torneio.getText().toString());
            }
        });

        btn_simulador_partida.setOnClickListener(v13 -> {
            etx_nome_novo_torneio.setText("");
            desabilitarBtnNovoTorneio();
            abrirSimulador();
        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void listarTorneios(){
        if(listaTorneiosGerenciados.isEmpty()){
            txv_torneios_gerenciados_recentes.setText(R.string.santuario_gerenciados_vazio);
        } else {
            txv_torneios_gerenciados_recentes.setText(R.string.santuario_torneios_recentes);
        }
    }

    private void povoarRecycleView(){
        recyclerViewTorneiosGerenciados.setLayoutManager(new LinearLayoutManager(superActivity));
        adapterTorneio = new TorneiosAdapter(superActivity, listaTorneiosGerenciados);
        recyclerViewTorneiosGerenciados.setAdapter(adapterTorneio);
        construirListenersAdapterTorneio();
    }

    private void construirListenersAdapterTorneio() {
        adapterTorneio.setOnClickListener(v -> abrirTorneioGerenciado(
                listaTorneiosGerenciados.get(
                        recyclerViewTorneiosGerenciados.getChildAdapterPosition(v)).getId()));

        adapterTorneio.setOnLongClickListener(v -> {
            montarAlertaExcluirTorneio(recyclerViewTorneiosGerenciados.getChildAdapterPosition(v));
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

    private void desabilitarBtnNovoTorneio() {
        if(Olimpia.TORNEIO_MAX==listaTorneiosGerenciados.size()) {
            etx_nome_novo_torneio.setHint(R.string.santuario_gerenciados_cheio);
        } else {
            etx_nome_novo_torneio.setHint(R.string.novo_torneio);
        }
        btn_novo_torneio.setEnabled(false);
        btn_novo_torneio.setBackground(
            ContextCompat.getDrawable(superActivity, R.drawable.button_shape_desabled)
        );
    }

    private void criarTorneioGerenciado(String nomeNovoTorneio){
        Torneio novoTorneio = superActivity.criarNovoTorneio(nomeNovoTorneio);
        if (novoTorneio!=null){
            desabilitarBtnNovoTorneio();
            etx_nome_novo_torneio.setText("");
            superActivity.abrirTorneio(novoTorneio.getId());
        } else {
            etx_nome_novo_torneio.setError(getString(R.string.erro_numero_max_torneios));
        }
    }

    private void abrirTorneioGerenciado(int torneioId){
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
}