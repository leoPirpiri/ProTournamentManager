package com.leoPirpiri.protournamentmanager;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import model.Equipe;
import model.Torneio;


public class EstatisticasDeEquipeFragment extends Fragment {

    private EquipeActivity superActivity;
    private Equipe equipe;

    public EstatisticasDeEquipeFragment() {/*Required empty public constructor*/}

    public EstatisticasDeEquipeFragment(Equipe equipe) {
        this.equipe = equipe;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.superActivity = (EquipeActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_estatisticas_equipe, container, false);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}