package com.leoPirpiri.protournamentmanager;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.HashMap;

import model.NoPartida;
import model.Score;
import model.Torneio;

public class PartidasAdapter extends BaseAdapter {
    private ArrayList<NoPartida> partidas;
    private Torneio major;
    private boolean flagLadoEsquerdo;
    private Context ctx;

    public PartidasAdapter(Context ctx, Torneio major, boolean flagLadoEsquerdo) {
        this.ctx = ctx;
        this.major = major;
        if (flagLadoEsquerdo){
            this.partidas = major.getPartidasOitavas(2);
        } else {
            this.partidas = major.getPartidasOitavas(18);
        }
        this.flagLadoEsquerdo = flagLadoEsquerdo;
    }

    @Override
    public int getCount() {
        return partidas.size();
    }

    @Override
    public NoPartida getItem(int position) {
        return partidas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
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
        LinearLayout chave = (LinearLayout) v.findViewById(R.id.layout_chave_oitava);
        TextView mandanteSigla = (TextView) v.findViewById(R.id.adp_homer_nome);
        TextView mandanteScore = (TextView) v.findViewById(R.id.adp_homer_ponto);
        TextView visitanteSigla = (TextView) v.findViewById(R.id.adp_visitor_nome);
        TextView visitanteScore = (TextView) v.findViewById(R.id.adp_visitor_ponto);

        NoPartida partida = getItem(position);
        System.out.println(partida.getMandante().getCampeaoId()+" e "+partida.getVisitante().getCampeaoId());
        if(partida.getMandante().getCampeaoId()>0 && partida.getVisitante().getCampeaoId()>0){
            if(partida.isEncerrada()) {
                HashMap<String, Integer> placar = partida.getDetalhesPartida();
                mandanteScore.setText(Integer.toString(placar.getOrDefault("Mand_"+ Score.TIPO_PONTO, 0)));
                visitanteScore.setText(Integer.toString(placar.getOrDefault("Vist_"+Score.TIPO_PONTO, 0)));
                v.setBackground(ctx.getDrawable(R.drawable.chave_background_desabled));
            } else {
                v.setBackground(ctx.getDrawable(R.drawable.chave_background_enabled));
                mandanteScore.setText(R.string.equipe_ponto_partida_aberta);
                visitanteScore.setText(R.string.equipe_ponto_partida_aberta);
            }
            v.setClickable(false);
            chave.setVisibility(View.VISIBLE);
        } else {
            v.setClickable(true);
        }

        if(partida.getMandante() != null && partida.getMandante().getCampeaoId()>0) {
            mandanteSigla.setText(major.getTime(partida.getMandante().getCampeaoId()).getSigla());
        } else {
            mandanteSigla.setText(R.string.equipe_sigla_partida_aberta);
        }

        if(partida.getVisitante() != null && partida.getVisitante().getCampeaoId()>0) {
            visitanteSigla.setText(major.getTime(partida.getVisitante().getCampeaoId()).getSigla());
        } else {
            visitanteSigla.setText(R.string.equipe_sigla_partida_aberta);
        }
        return v;
    }
}
