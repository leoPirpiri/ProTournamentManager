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
import android.widget.LinearLayout;
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


public class EstatisticasDeTorneioFragment extends Fragment {

    private Context context;
    private Torneio torneio;

    public EstatisticasDeTorneioFragment() {/*Required empty public constructor*/}

    public EstatisticasDeTorneioFragment(Context context, Torneio torneio) {
        this.context = context;
        this.torneio = torneio;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_estatisticas_torneio, container, false);
        //Preenche estatísticas de campeão
        Equipe campeao = torneio.getCampeao();
//        if (campeao != null){
//            LinearLayout ll_campeao = v.findViewById(R.id.layout_estatica_campeao);
//            TextView txv_campeao = v.findViewById(R.id.txv_estatistica_campeao);
//            ll_campeao.setVisibility(View.VISIBLE);
//            txv_campeao.setText(campeao.getNome() + " - " + campeao.getSigla());
//        }
        //Preenche estatísticas de artilharia
        //Preenche estatísticas de falta
        //Preenche estatísticas de cartões
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}