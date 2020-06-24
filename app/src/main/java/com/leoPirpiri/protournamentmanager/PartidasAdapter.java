package com.leoPirpiri.protournamentmanager;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import model.Equipe;
import model.Partida;
import model.Torneio;

public class PartidasAdapter extends BaseAdapter {
    private ArrayList<Partida> partidas;
    private Torneio major;
    private boolean flagLadoEsquerdo;
    private Context ctx;

    public PartidasAdapter(Context ctx, Torneio major, boolean flagLadoEsquerdo) {
        this.ctx = ctx;
        this.major = major;
        this.partidas = major.getPartidas();
        this.flagLadoEsquerdo = flagLadoEsquerdo;
    }

    @Override
    public int getCount() {
        return partidas.size();
    }

    @Override
    public Partida getItem(int position) {
        return partidas.get(position);
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
            if(flagLadoEsquerdo){
                v = inflater.inflate(R.layout.item_lista_partida8e, null);
            } else {
                v = inflater.inflate(R.layout.item_lista_partida8d, null);
            }
        } else {
            v = convertView;
        }
        Partida p = getItem(position); 
        Equipe mandante = major.getTime(p.getMandante());
        Equipe visitante = major.getTime(p.getVisitante());

        TextView mandanteNome = (TextView) v.findViewById(R.id.adp_homer_nome);
        TextView mandantePonto = (TextView) v.findViewById(R.id.adp_homer_ponto);
        TextView visitanteNome = (TextView) v.findViewById(R.id.adp_visitor_nome);
        TextView visitantePonto = (TextView) v.findViewById(R.id.adp_visitor_ponto);

        int pontos[] = p.getPlacarPontos();
        if (pontos != null) {
            mandantePonto.setText(Integer.toString(pontos[0]));
            visitantePonto.setText(Integer.toString(pontos[1]));
            mandanteNome.setText(mandante.getSigla());
            visitanteNome.setText(visitante.getSigla());
        } else {
            CarrierSemiActivity.exemplo(ctx, ctx.getResources().getString(R.string.dados_erro_inconsistencia));
            mandantePonto.setText(R.string.erro_default_string);
            visitantePonto.setText(R.string.erro_default_string);
            mandanteNome.setText(R.string.erro_default_string);
            visitanteNome.setText(R.string.erro_default_string);
        }
        return v;
    }
}
