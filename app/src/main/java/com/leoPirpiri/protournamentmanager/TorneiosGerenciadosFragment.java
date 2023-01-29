package com.leoPirpiri.protournamentmanager;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
            metodoRaiz();
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
        //desarmaBTNnovoTorneio();
        //Lista os torneios salvos anteriormente.
        listarTorneios();
    }

    private void listarTorneios(){
        if(santuarioOlimpia.getTorneios().isEmpty()){
            txv_torneios_recentes.setText(R.string.santuario_vazio);
        } else {
            listaTorneios = santuarioOlimpia.getTorneios();
            txv_torneios_recentes.setText(R.string.santuario_com_torneios);
            povoarRecycleView();
        }
    }

    private void povoarRecycleView(){
        recyclerViewTorneiosGerenciados.setLayoutManager(new LinearLayoutManager(context));
        adapterTorneio = new TorneiosAdapter(context, listaTorneios);
        recyclerViewTorneiosGerenciados.setAdapter(adapterTorneio);
    }
}