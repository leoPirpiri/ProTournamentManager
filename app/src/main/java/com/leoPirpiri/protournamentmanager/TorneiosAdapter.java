package com.leoPirpiri.protournamentmanager;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import model.Torneio;

public class TorneiosAdapter extends BaseAdapter {
    private ArrayList<Torneio> torneios;
    private Context ctx;

    public TorneiosAdapter(Context ctx, ArrayList<Torneio> torneios){
        this.ctx = ctx;
        this.torneios = torneios;
    }
    @Override
    public int getCount() {
        return torneios.size();
    }

    @Override
    public Torneio getItem(int position) {
        return torneios.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        if(convertView == null){
            LayoutInflater inflater = ((Activity) ctx).getLayoutInflater();
            v = inflater.inflate(R.layout.item_lista_torneio, null);
        }else {
            v = convertView;
        }
        Torneio torneio = getItem(position);
        TextView itemNome = (TextView) v.findViewById(R.id.nome_torneio);
        TextView itemEstado = (TextView) v.findViewById(R.id.situacao_torneio);

        itemNome.setText(torneio.getNome());
        itemEstado.setText(torneio.isFechado() ? R.string.estado_fechado : (
                torneio.getCampeao() == null ? R.string.estado_aberto : R.string.estado_encerrado));
        return v;
    }
}
