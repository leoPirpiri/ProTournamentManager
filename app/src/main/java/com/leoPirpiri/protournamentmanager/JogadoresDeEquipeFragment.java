package com.leoPirpiri.protournamentmanager;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import model.Jogador;

public class JogadoresDeEquipeFragment extends Fragment {

    private Context context;
    private ArrayList<Jogador> listaJogadores;
    private AlertDialog alertaDialog;
    private RecyclerView recyclerViewJogadoresDeEquipe;
    private JogadoresAdapter adapterJogadores;
    private TextView txv_info_lista_jogadores;

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
        recyclerViewJogadoresDeEquipe = v.findViewById(R.id.recyclerview_lista_jogadores);
        txv_info_lista_jogadores = v.findViewById(R.id.txv_msg_jogadores_salvos);
        listarJogadores();
        //Listeners

        return v;
    }
    private void listarJogadores() {
        if (!listaJogadores.isEmpty()) {
            //ativarAddBtnNovaEquipe();
            txv_info_lista_jogadores.setVisibility(View.GONE);
            povoarRecycleView();
        }
    }

    private void povoarRecycleView(){
        recyclerViewJogadoresDeEquipe.setLayoutManager(new LinearLayoutManager(context));
        adapterJogadores = new JogadoresAdapter(context, listaJogadores);
        recyclerViewJogadoresDeEquipe.setAdapter(adapterJogadores);
        construirListenersAdapterJogadores();
    }

    private void construirListenersAdapterJogadores() {
        /*adapterJogadores.setOnClickListener(v -> abrirEquipe(listaEquipes.get(recyclerViewEquipesDoTorneio.getChildAdapterPosition(v)).getId()));

        adapterJogadores.setOnLongClickListener(v -> {
            Olimpia.printteste(context, "Clicou no Longo");
            //montarAlertaExcluirEquipe(recyclerViewEquipesDoTorneio.getChildAdapterPosition(v));
            return true;
        });*/
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}