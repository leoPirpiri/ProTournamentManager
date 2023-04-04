package com.leoPirpiri.protournamentmanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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

import model.Olimpia;
import model.Equipe;
import model.Torneio;

public class EquipesDeTorneioFragment extends Fragment {

    private TorneioActivity superActivity;
    private ArrayList<Equipe> listaEquipes;
    private RecyclerView recyclerViewEquipesDoTorneio;
    private EquipesAdapter adapterEquipe;
    private TextView txv_info_lista_equipes;
    //views alertDialog adicionar nova equipe.
    private EditText etx_sigla_equipe;
    private EditText etx_nome_equipe;
    private Button btn_confirma_equipe;

    public EquipesDeTorneioFragment() {
        // Required empty public constructor
    }

    public EquipesDeTorneioFragment(ArrayList<Equipe> equipes) {
        this.listaEquipes = equipes;
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
        FloatingActionButton btn_add_equipe = v.findViewById(R.id.btn_nova_equipe);
        listarTimes();
        //Listeners
        btn_add_equipe.setOnClickListener(view -> {
            if(superActivity.estadoDoTorneio() != Torneio.STATUS_ABERTO || listaEquipes.size() == Torneio.MAX_EQUIPE){
                montarAlertaEquipeAdicaoNaoPermitida();
            } else {
                montarAlertaNovaEquipe();
            }
        });
        return v;
    }

    private void listarTimes() {
        if (!listaEquipes.isEmpty()) {
            //ativarAddBtnNovaEquipe();
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
        adapterEquipe.setOnClickListener(v -> {
            superActivity.abrirEquipe(
                listaEquipes.get(
                    recyclerViewEquipesDoTorneio.getChildAdapterPosition(v)
                ).getId()
            );
        });

        adapterEquipe.setOnLongClickListener(v -> {
            Olimpia.printteste(superActivity, "Clicou no Longo");
//                if(torneio.isFechado()){
//                    CarrierSemiActivity.exemplo(TorneioActivity.this, getString(R.string.msg_alerta_erro_excluir_equipe));
//                } else {
//                    montarAlertaExcluirEquipe(position);
//                }
            //montarAlertaExcluirEquipe(recyclerViewEquipesDoTorneio.getChildAdapterPosition(v));
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
                    String sigla_bot;
                    if (nome.contains(" ")) {
                        sigla_bot = formatarSigla(nome);
                    } else {
                        sigla_bot = nome.substring(0, 1).toUpperCase();
                    }
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

    private void ativarBtnOKalertaNovaEquipe() {
        btn_confirma_equipe.setEnabled(true);
        btn_confirma_equipe.setBackground(ContextCompat.getDrawable(superActivity, R.drawable.button_shape_enabled));
    }

    private void desativarBtnOKalertaNovaEquipe() {
        btn_confirma_equipe.setEnabled(false);
        btn_confirma_equipe.setBackground(ContextCompat.getDrawable(superActivity, R.drawable.button_shape_desabled));
    }

    private String formatarSigla(String entrada) {
        String sigla = "";
        for (String word : entrada.split(" ")) {
            if (word.length()>2){
                sigla = sigla.concat(word.substring(0,1));
            }
            if(sigla.length()>4){
                break;
            }
        }
        return sigla.toUpperCase();
    }

    private void excluirEquipe(int position) {
//        if(torneio.delTime(position) != null){
//            atualizar = true;
//            listarTimes();
//        }
    }

}