package com.leoPirpiri.protournamentmanager;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public TorneiosSeguidosFragment() {
        // Required empty public constructor
    }

    public TorneiosSeguidosFragment(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_torneios_seguidos, container, false);
    }
}