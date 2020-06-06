package com.leoPirpiri.protournamentmanager;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import model.Jogador;

public class JogadoresAdapter extends BaseAdapter {
    private ArrayList<Jogador> jogadores;
    private Context ctx;

    public JogadoresAdapter(Context ctx, ArrayList<Jogador> jogadores) {
        this.jogadores = jogadores;
        this.ctx = ctx;
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
//        TextView itemNome = (TextView) v.findViewById(R.id.adp_equipe_nome);
//        TextView itemQjogadores = (TextView) v.findViewById(R.id.adp_equipe_quant);
//
//        itemNome.setText(equipe.getNome()+" - "+equipe.getSigla());
//        String label = ctx.getResources().getString(R.string.hint_quant_jogadores);
//        itemQjogadores.setText( label +": "+ Integer.toString(equipe.getTamanhoEquipe()));
        return v;
    }
}
