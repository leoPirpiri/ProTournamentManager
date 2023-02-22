package com.leoPirpiri.protournamentmanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import model.Jogador;

public class JogadoresDeEquipeFragment extends Fragment {

    private Context context;
    private ArrayList<Jogador> listaJogadores;
    private AlertDialog alertaDialog;
    private RecyclerView recyclerViewEquipesDoTorneio;
    private EquipesAdapter adapterEquipe;
    private TextView txv_info_lista_equipes;

    public JogadoresDeEquipeFragment() {
        // Required empty public constructor
    }

    public JogadoresDeEquipeFragment(Context context, ArrayList<Jogador> jogadores) {
        this.context = context;
        this.listaJogadores = jogadores;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_jogadores_equipe, container, false);
//
//        recyclerViewEquipesDoTorneio = v.findViewById(R.id.recyclerview_lista_equipes);
//        txv_info_lista_equipes = v.findViewById(R.id.txv_msg_equipes_salvas);
//        listarTimes();
        //Listeners

        return v;
    }
   /* private void listarTimes() {
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
    }*/

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}