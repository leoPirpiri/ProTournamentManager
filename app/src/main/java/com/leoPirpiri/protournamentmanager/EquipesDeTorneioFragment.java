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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import control.Olimpia;
import model.Equipe;
import model.Torneio;

public class EquipesDeTorneioFragment extends Fragment {

    private Context context;
    private ArrayList<Equipe> listaEquipes;
    private AlertDialog alertaDialog;
    private RecyclerView recyclerViewEquipesDoTorneio;
    private EquipesAdapter adapterEquipe;
    private TextView txv_info_lista_equipes;

    public EquipesDeTorneioFragment() {
        // Required empty public constructor
    }

    public EquipesDeTorneioFragment(Context context, ArrayList<Equipe> equipes) {
        this.context = context;
        this.listaEquipes = equipes;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_equipes_torneio, container, false);

        recyclerViewEquipesDoTorneio = v.findViewById(R.id.recyclerview_lista_equipes);
        txv_info_lista_equipes = v.findViewById(R.id.txv_msg_equipes_salvas);
        listarTimes();
        //Listeners

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
        recyclerViewEquipesDoTorneio.setLayoutManager(new LinearLayoutManager(context));
        adapterEquipe = new EquipesAdapter(context, listaEquipes);
        recyclerViewEquipesDoTorneio.setAdapter(adapterEquipe);
        construirListenersAdapterEquipe();
    }

    private void construirListenersAdapterEquipe() {
        adapterEquipe.setOnClickListener(v -> abrirEquipe(listaEquipes.get(recyclerViewEquipesDoTorneio.getChildAdapterPosition(v)).getId()));

        adapterEquipe.setOnLongClickListener(v -> {
            Olimpia.printteste(context, "Clicou no Longo");
            //montarAlertaExcluirEquipe(recyclerViewEquipesDoTorneio.getChildAdapterPosition(v));
            return true;
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void esconderTeclado(Context context, View editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    private void mostrarAlerta(AlertDialog.Builder builder) {
        mostrarAlerta(builder, R.drawable.background_alerta);
    }

    private void mostrarAlerta(AlertDialog.Builder builder, int background) {
        alertaDialog = builder.create();
        alertaDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(background));
        alertaDialog.show();
    }

    private void abrirEquipe(int idEquipe) {
        Intent intent = new Intent(context, EquipeActivity.class);
        Bundle dados = new Bundle();
        //Passa alguns dados para a próxima activity
        dados.putInt("equipe", idEquipe);
        intent.putExtras(dados);
        startActivity(intent);
    }
    private void excluirEquipe(int position) {
//        if(torneio.delTime(position) != null){
//            atualizar = true;
//            listarTimes();
//        }
    }

}