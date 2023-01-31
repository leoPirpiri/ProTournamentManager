package com.leoPirpiri.protournamentmanager;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import control.Olimpia;
import model.Torneio;

public class TorneiosSeguidosFragment extends Fragment {

    private Context context;
    private TorneiosAdapter adapterTorneio;
    private RecyclerView recyclerViewTorneiosGerenciados;
    private TextView txv_torneios_recentes;
    private ArrayList<Torneio> listaTorneios;
    private Olimpia santuarioOlimpia;
    private int aux = 1;

    public TorneiosSeguidosFragment() {/* Required empty public constructor*/}

    public TorneiosSeguidosFragment(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_torneios_seguidos, container, false);
        Button btn_teste = v.findViewById(R.id.btn_teste);
        TextView txv_teste = v.findViewById(R.id.txv_teste);

        //Listeners
        btn_teste.setOnClickListener(v1 -> {
            txv_teste.setText(String.valueOf(++aux));
        });

        return v;
    }
}