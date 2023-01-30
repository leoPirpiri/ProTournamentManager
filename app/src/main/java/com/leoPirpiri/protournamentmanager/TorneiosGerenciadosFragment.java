package com.leoPirpiri.protournamentmanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;


import java.util.ArrayList;

import control.Olimpia;
import model.Torneio;


public class TorneiosGerenciadosFragment extends Fragment {

    private Context context;
    private TorneiosAdapter adapterTorneio;
    private RecyclerView recyclerViewTorneiosGerenciados;
    private TextView txv_torneios_recentes;
    private ArrayList<Torneio> listaTorneios;
    private Olimpia santuarioOlimpia;
    private TextView etx_nome_novo_torneio;
    private Button btn_novo_torneio;
    private AlertDialog alertaDialog;

    public TorneiosGerenciadosFragment() {
        // Required empty public constructor
    }

    public TorneiosGerenciadosFragment(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_torneios_gerenciados, container, false);
            recyclerViewTorneiosGerenciados = v.findViewById(R.id.recyclerview_torneios_gerenciados);
            txv_torneios_recentes = v.findViewById(R.id.txv_torneios_gerenciados_recentes);
            etx_nome_novo_torneio = v.findViewById(R.id.nome_novo_torneio);
            btn_novo_torneio = v.findViewById(R.id.btn_novo_tourneio);
            Button btn_simulador_partida = v.findViewById(R.id.btn_simulador_tela_inicio);
            metodoRaiz();
        //Listeners
        etx_nome_novo_torneio.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(etx_nome_novo_torneio.getText().toString().equals("")){
                    desarmaBTNnovoTorneio();
                }else{
                    btn_novo_torneio.setEnabled(true);
                    btn_novo_torneio.setBackground(getResources().getDrawable(R.drawable.button_shape_enabled));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etx_nome_novo_torneio.setOnFocusChangeListener((v1, hasFocus) -> {
            if (!hasFocus) {
                esconderTeclado(context, etx_nome_novo_torneio);
            }
        });

        btn_novo_torneio.setOnClickListener(v12 -> {
            esconderTeclado(context, etx_nome_novo_torneio);
            int idNovoTorneio = santuarioOlimpia.getNovoTorneioId();
            if (idNovoTorneio != 0){
                idNovoTorneio = santuarioOlimpia.addTorneio(new Torneio( idNovoTorneio,
                        etx_nome_novo_torneio.getText().toString())
                );
                if (idNovoTorneio != -1){
                    CarrierSemiActivity.persistirSantuario(context, santuarioOlimpia);
                    abrirTorneio(idNovoTorneio);
                } else {
                    etx_nome_novo_torneio.setError(getString(R.string.erro_numero_max_torneios));
                }
            }
            desarmaBTNnovoTorneio();
            etx_nome_novo_torneio.setText("");
        });

        btn_simulador_partida.setOnClickListener(v13 -> {
            etx_nome_novo_torneio.setText("");
            desarmaBTNnovoTorneio();
            abrirSimulador();
        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void metodoRaiz(){
        //Carrega ou inicia o santuário onde ocorre os jogos.
        santuarioOlimpia = CarrierSemiActivity.carregarSantuario(context);
        //torneiosAdapter = new TorneiosAdapter(getActivity(), santuarioOlimpia.getTorneios());
        //ltv_torneios_recentes.setAdapter(torneiosAdapter);
        desarmaBTNnovoTorneio();
        //Lista os torneios salvos anteriormente.
        listarTorneios();
        povoarRecycleView();
    }

    private void listarTorneios(){
        if(santuarioOlimpia.getTorneios().isEmpty()){
            txv_torneios_recentes.setText(R.string.santuario_vazio);
        } else {
            listaTorneios = santuarioOlimpia.getTorneios();
            txv_torneios_recentes.setText(R.string.santuario_com_torneios);
        }
    }

    private void povoarRecycleView(){
        recyclerViewTorneiosGerenciados.setLayoutManager(new LinearLayoutManager(context));
        adapterTorneio = new TorneiosAdapter(context, listaTorneios);
        recyclerViewTorneiosGerenciados.setAdapter(adapterTorneio);
        construirListenersAdapterTorneio();
    }

    private void esconderTeclado(Context context, View editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    private void construirListenersAdapterTorneio() {
        adapterTorneio.setOnClickListener(v -> abrirTorneio(listaTorneios.get(recyclerViewTorneiosGerenciados.getChildAdapterPosition(v)).getId()));

        adapterTorneio.setOnLongClickListener(v -> {
            montarAlertaExcluirTorneio(recyclerViewTorneiosGerenciados.getChildAdapterPosition(v));
            return true;
        });
    }


    private void montarAlertaExcluirTorneio(final int posTorneio){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = getLayoutInflater().inflate(R.layout.alerta_default, null);

        Button btn_confirmar = view.findViewById(R.id.btn_confirmar_default);
        Button btn_cancelar = view.findViewById(R.id.btn_cancelar_default);
        TextView msg = view.findViewById(R.id.msg_alerta_default);
        btn_confirmar.setVisibility(View.VISIBLE);
        btn_cancelar.setVisibility(View.VISIBLE);
        msg.setVisibility(View.VISIBLE);
        msg.setText(R.string.msg_alerta_confir_excluir_torneio);

        btn_confirmar.setOnClickListener(arg0 -> {
            alertaDialog.dismiss();
            excluirTorneio(posTorneio);
        });

        btn_cancelar.setOnClickListener(arg0 -> alertaDialog.dismiss());

        builder.setView(view);
        builder.setTitle(R.string.titulo_alerta_confir_excluir_torneio);
        mostrarAlerta(builder);
    }

    private void mostrarAlerta(AlertDialog.Builder builder) {
        mostrarAlerta(builder, R.drawable.background_alerta);
    }

    private void mostrarAlerta(AlertDialog.Builder builder, int background) {
        alertaDialog = builder.create();
        alertaDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(background));
        alertaDialog.show();
    }

    private void desarmaBTNnovoTorneio() {
        if(santuarioOlimpia.TORNEIO_MAX==santuarioOlimpia.getOcupacao()) {
            etx_nome_novo_torneio.setHint(R.string.santuario_cheio);
        } else {
            etx_nome_novo_torneio.setHint(R.string.novo_torneio);
        }
        btn_novo_torneio.setEnabled(false);
        btn_novo_torneio.setBackground(getResources().getDrawable(R.drawable.button_shape_desabled));
    }

    private void abrirTorneio(int torneioId){
        Intent intent = new Intent(context, TorneioActivity.class);
        Bundle dados = new Bundle();
        //Passa alguns dados para a próxima activity
        dados.putInt("torneio", torneioId);
        intent.putExtras(dados);
        startActivity(intent);
    }

    private void excluirTorneio(int position) {
        if(santuarioOlimpia.delTorneio(position) != null){
            CarrierSemiActivity.persistirSantuario(context, santuarioOlimpia);
            listarTorneios();
            adapterTorneio.notifyItemRemoved(position);
        }
    }

    private void abrirSimulador(){
        Intent intent = new Intent(context, PartidaActivity.class);
        intent.putExtra("partida", -1);
        startActivity(intent);
    }
}