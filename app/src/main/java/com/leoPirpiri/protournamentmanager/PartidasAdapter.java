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
import model.NoPartida;
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
            this.partidas = new ArrayList<>(major.getPartidasOitavas().subList(0, 4));
        } else {
            this.partidas = new ArrayList<>(major.getPartidasOitavas().subList(4, 8));
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
        TextView mandanteNome = (TextView) v.findViewById(R.id.adp_homer_nome);
        TextView mandantePonto = (TextView) v.findViewById(R.id.adp_homer_ponto);
        TextView visitanteNome = (TextView) v.findViewById(R.id.adp_visitor_nome);
        TextView visitantePonto = (TextView) v.findViewById(R.id.adp_visitor_ponto);

        NoPartida p = getItem(position);
        Equipe mandante;
        Equipe visitante;
        int pontos[] = p.getPlacarPontos();
        if (pontos == null) {
            CarrierSemiActivity.exemplo(ctx, ctx.getResources().getString(R.string.dados_erro_inconsistencia));
            mandantePonto.setText(R.string.erro_default_string);
            visitantePonto.setText(R.string.erro_default_string);
            mandanteNome.setText(R.string.erro_default_string);
            visitanteNome.setText(R.string.erro_default_string);
        } else {
            if(p.getMandante() != null && p.getMandante().getCampeaoId() !=0){
                mandante = major.getTime(p.getMandante().getCampeaoId());
                mandantePonto.setText(Integer.toString(pontos[0]));
            }else {
                mandante = new Equipe(0, "", "");
                mandantePonto.setText("-");

            }
            if(p.getVisitante() != null && p.getVisitante().getCampeaoId() !=0){
                visitante = major.getTime(p.getVisitante().getCampeaoId());
                visitantePonto.setText(Integer.toString(pontos[1]));
            }else {
                visitante = new Equipe(0, "", "");
                visitantePonto.setText("-");
            }
            mandanteNome.setText(mandante.getSigla());
            visitanteNome.setText(visitante.getSigla());
        }
        return v;
    }
}
