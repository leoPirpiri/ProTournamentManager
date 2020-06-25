package com.leoPirpiri.protournamentmanager;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import model.Jogador;

public class JogadoresAdapter extends BaseAdapter {
    private ArrayList<Jogador> jogadores;
    private Context ctx;
    private boolean flagCompleto;

    public JogadoresAdapter(Context ctx, ArrayList<Jogador> jogadores, boolean flagCompleto) {
        Collections.sort(jogadores);
        this.jogadores = jogadores;
        this.ctx = ctx;
        this.flagCompleto = flagCompleto;
    }

    @Override
    public int getCount() {
        return jogadores.size();
    }

    @Override
    public Jogador getItem(int position) {
        return jogadores.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        if(convertView == null) {
            LayoutInflater inflater = ((Activity) ctx).getLayoutInflater();
            v = inflater.inflate(R.layout.item_lista_jogador, null);
        } else {
            v = convertView;
        }

        Jogador jogador = getItem(position);
        TextView itemNome = (TextView) v.findViewById(R.id.adp_jogador_nome);
        TextView itemNumber = (TextView) v.findViewById(R.id.adp_jogador_number);
        TextView itemPosicao = (TextView) v.findViewById(R.id.adp_jogador_posicao);

        if (flagCompleto) {
            itemPosicao.setText(ctx.getResources().getStringArray(R.array.posicoes_jogador)[jogador.getPosicao()].substring(0, 3));
            itemNome.setText(jogador.getNome());
        } else {
            itemPosicao.setVisibility(View.GONE);
            itemNome.setText(jogador.getNome().split(" ")[0]);
            itemNumber.setTextSize(18);
            itemNome.setTextSize(18);
        }

        int num = jogador.getNumero();
        itemNumber.setText(num<10 ? "0"+num : ""+num);
        return v;
    }
}
