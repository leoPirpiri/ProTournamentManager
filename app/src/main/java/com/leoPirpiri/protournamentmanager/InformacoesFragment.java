package com.leoPirpiri.protournamentmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class InformacoesFragment extends Fragment {

    private int informacoes;

    public InformacoesFragment() {/* Required empty public constructor*/}

    public InformacoesFragment(int informacoes) {
        this.informacoes = informacoes;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_informacoes, container, false);
        TextView msg_info = v.findViewById(R.id.msg_informacoes);
        msg_info.setText(informacoes);

        return v;
    }

}

