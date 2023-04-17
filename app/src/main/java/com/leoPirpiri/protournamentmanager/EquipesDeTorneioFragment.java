package com.leoPirpiri.protournamentmanager;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import adapters.EquipesAdapter;
import model.Equipe;
import model.Torneio;

public class EquipesDeTorneioFragment extends Fragment {

    private TorneioActivity superActivity;
    private ArrayList<Equipe> listaEquipes;
    private RecyclerView recyclerViewEquipesDoTorneio;
    private EquipesAdapter adapterEquipe;
    private TextView txv_info_lista_equipes;
    private EditText etx_sigla_equipe;
    private EditText etx_nome_equipe;
    private Button btn_confirma_equipe;
    private int tentativas_exclusao = 0;

    public EquipesDeTorneioFragment() {
        // Required empty public constructor
    }

    public EquipesDeTorneioFragment(Torneio torneio) {
        this.listaEquipes = torneio.getEquipes();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.superActivity = (TorneioActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_equipes_torneio, container, false);

        recyclerViewEquipesDoTorneio = v.findViewById(R.id.recyclerview_lista_equipes);
        txv_info_lista_equipes = v.findViewById(R.id.txv_msg_equipes_salvas);

        listarTimes();
        //Listeners
        ((FloatingActionButton) v.findViewById(R.id.btn_nova_equipe)).setOnClickListener(view -> {
            if(superActivity.torneioFechado() || listaEquipes.size() == Torneio.MAX_EQUIPE){
                montarAlertaEquipeAdicaoNaoPermitida();
            } else {
                montarAlertaNovaEquipe();
            }
        });
        return v;
    }

    private void listarTimes() {
        if (!listaEquipes.isEmpty()) {
            txv_info_lista_equipes.setVisibility(View.GONE);
            povoarRecycleView();
        }
    }

    private void povoarRecycleView(){
        recyclerViewEquipesDoTorneio.setLayoutManager(new LinearLayoutManager(superActivity));
        adapterEquipe = new EquipesAdapter(superActivity, listaEquipes);
        recyclerViewEquipesDoTorneio.setAdapter(adapterEquipe);
        construirListenersAdapterEquipe();
    }

    private void construirListenersAdapterEquipe() {
        adapterEquipe.setOnClickListener(v -> superActivity.abrirEquipe(
            listaEquipes.get(
                recyclerViewEquipesDoTorneio.getChildAdapterPosition(v)
            ).getId()
        ));

        adapterEquipe.setOnLongClickListener(v -> {
            if (superActivity.torneioFechado()){
                if(tentativas_exclusao%4==0) {
                    montarAlertaEquipeExclusaoNaoPermitida();
                }
                tentativas_exclusao+=1;
            } else {
                montarAlertaExcluirEquipe(recyclerViewEquipesDoTorneio.getChildAdapterPosition(v));
            }
            return true;
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void montarAlertaNovaEquipe(){
        AlertDialog.Builder builder = new AlertDialog.Builder(superActivity);
        View view = getLayoutInflater().inflate(R.layout.alerta_nova_equipe, null);
        etx_nome_equipe = view.findViewById(R.id.etx_nome_nova_equipe);
        etx_sigla_equipe = view.findViewById(R.id.etx_sigla_nova_equipe);
        btn_confirma_equipe = view.findViewById(R.id.btn_confirmar_nova_equipe);
        etx_nome_equipe.requestFocus();

        //Listeners possíveis do alerta
        view.findViewById(R.id.btn_confirmar_nova_equipe).setOnClickListener(arg0 -> {
            String nome = etx_nome_equipe.getText().toString().trim();
            String sigla = etx_sigla_equipe.getText().toString().trim().toUpperCase();
            if (nome.isEmpty()){
                etx_nome_equipe.setError(getString(R.string.erro_campo_texto_vazio));
            } else if (sigla.isEmpty()) {
                etx_sigla_equipe.setError(getString(R.string.erro_campo_texto_vazio));
            } else if (superActivity.adicionarEquipe(nome, sigla)){
                Toast.makeText(superActivity, R.string.sucesso_adicionar_equipe, Toast.LENGTH_SHORT).show();
                superActivity.esconderAlerta();
                listarTimes();
            }
        });

        view.findViewById(R.id.btn_cancelar_equipe).setOnClickListener(arg0 -> superActivity.esconderAlerta());

        etx_nome_equipe.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && !etx_sigla_equipe.isFocused()) {
                superActivity.esconderTeclado(etx_nome_equipe);
            }
        });

        etx_sigla_equipe.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && !etx_nome_equipe.isFocused()) {
                superActivity.esconderTeclado(etx_sigla_equipe);
            }
        });

        etx_nome_equipe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                String nome = etx_nome_equipe.getText().toString().trim();
                String sigla = etx_sigla_equipe.getText().toString().trim().toUpperCase();
                if (nome.isEmpty()) {
                    etx_sigla_equipe.setText("");
                    desativarBtnOKalertaNovaEquipe();
                } else {
                    String sigla_bot = nome.contains(" ") ? Equipe.formatarSigla(nome) : nome.substring(0, 1).toUpperCase();
                    if (!sigla.equals(sigla_bot)) {
                        sigla = sigla_bot;
                        etx_sigla_equipe.setText(sigla);
                    }
                    if(sigla.length()>1){
                        ativarBtnOKalertaNovaEquipe();
                    }else {
                        desativarBtnOKalertaNovaEquipe();
                    }
                }
            }
        });

        etx_sigla_equipe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                String sigla = etx_sigla_equipe.getText().toString();
                if (etx_nome_equipe.getText().toString().isEmpty() || sigla.length()<=1) {
                    desativarBtnOKalertaNovaEquipe();
                } else {
                    ativarBtnOKalertaNovaEquipe();
                }
            }
        });

        builder.setView(view);
        builder.setTitle(R.string.titulo_alerta_nova_equipe);
        superActivity.mostrarAlerta(builder);
    }

    private void montarAlertaExcluirEquipe(final int posEquipe){
        AlertDialog.Builder builder = new AlertDialog.Builder(superActivity);
        View view = getLayoutInflater().inflate(R.layout.alerta_default, null);

        Button btn_confirmar = view.findViewById(R.id.btn_confirmar_default);
        Button btn_cancelar = view.findViewById(R.id.btn_cancelar_default);
        TextView msg = view.findViewById(R.id.msg_alerta_default);
        btn_confirmar.setVisibility(View.VISIBLE);
        btn_cancelar.setVisibility(View.VISIBLE);
        msg.setVisibility(View.VISIBLE);
        msg.setText(R.string.msg_alerta_confir_excluir_equipe);

        btn_confirmar.setOnClickListener(arg0 -> {
            if(superActivity.excluirEquipe(posEquipe)){
                listarTimes();
            } else {
                Toast.makeText(superActivity, R.string.erro_excluir_equipe, Toast.LENGTH_SHORT).show();
            }
            superActivity.esconderAlerta();
        });

        btn_cancelar.setOnClickListener(arg0 -> superActivity.esconderAlerta());

        builder.setView(view);
        builder.setTitle(R.string.titulo_alerta_confir_excluir_equipe);
        superActivity.mostrarAlerta(builder);
    }

    private void montarAlertaEquipeAdicaoNaoPermitida(){
        AlertDialog.Builder builder = new AlertDialog.Builder(superActivity);
        View view = getLayoutInflater().inflate(R.layout.alerta_default, null);
        Button btn_confirmar = view.findViewById(R.id.btn_confirmar_default);
        TextView msg = view.findViewById(R.id.msg_alerta_default);
        btn_confirmar.setVisibility(View.VISIBLE);
        msg.setVisibility(View.VISIBLE);
        msg.setText(R.string.msg_alerta_impedir_adicao_nova_equipe);

        btn_confirmar.setOnClickListener(arg0 -> superActivity.esconderAlerta());

        builder.setView(view);
        builder.setTitle(R.string.titulo_alerta_impedir_adicao_nova_equipe);
        superActivity.mostrarAlerta(builder);
    }

    private void montarAlertaEquipeExclusaoNaoPermitida(){
        AlertDialog.Builder builder = new AlertDialog.Builder(superActivity);
        View view = getLayoutInflater().inflate(R.layout.alerta_default, null);
        Button btn_confirmar = view.findViewById(R.id.btn_confirmar_default);
        TextView msg = view.findViewById(R.id.msg_alerta_default);
        btn_confirmar.setVisibility(View.VISIBLE);
        msg.setVisibility(View.VISIBLE);
        msg.setText(R.string.msg_alerta_impedir_exclusao_de_equipe);

        btn_confirmar.setOnClickListener(arg0 -> superActivity.esconderAlerta());

        builder.setView(view);
        builder.setTitle(R.string.titulo_alerta_impedir_exclusao_de_equipe);
        superActivity.mostrarAlerta(builder);
    }

    private void ativarBtnOKalertaNovaEquipe() {
        btn_confirma_equipe.setEnabled(true);
        btn_confirma_equipe.setBackground(ContextCompat.getDrawable(superActivity, R.drawable.button_shape_enabled));
    }

    private void desativarBtnOKalertaNovaEquipe() {
        btn_confirma_equipe.setEnabled(false);
        btn_confirma_equipe.setBackground(ContextCompat.getDrawable(superActivity, R.drawable.button_shape_desabled));
    }


}