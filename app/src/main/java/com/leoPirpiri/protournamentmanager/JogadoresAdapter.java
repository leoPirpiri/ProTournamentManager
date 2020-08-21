package com.leoPirpiri.protournamentmanager;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import model.Jogador;
import model.Score;

public class JogadoresAdapter extends BaseAdapter {
    private ArrayList<Jogador> jogadores;
    private Context ctx;
    ArrayList<Score> acoesTime;

    public JogadoresAdapter(Context ctx, ArrayList<Jogador> jogadores) {
        this(ctx, jogadores, null);
    }

    public JogadoresAdapter(Context ctx, ArrayList<Jogador> jogadores, ArrayList<Score> acoesTime) {
        Collections.sort(jogadores);
        this.jogadores = jogadores;
        this.ctx = ctx;
        this.acoesTime = acoesTime;
    }

    public ArrayList<Score> getAcoesTime(){
        return this.acoesTime;
    }

    public void setAcoesTime(ArrayList<Score> acoesTime) {
        this.acoesTime = acoesTime;
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
        TextView itemNome = v.findViewById(R.id.adp_jogador_nome);
        TextView itemNumber = v.findViewById(R.id.adp_jogador_number);
        TextView itemPosicao = v.findViewById(R.id.adp_jogador_posicao);
        ImageView itemPunicao = v.findViewById(R.id.adp_jogador_punicao);

        if (acoesTime == null) {
            itemPosicao.setText(ctx.getResources().getStringArray(R.array.posicoes_jogador)[jogador.getPosicao()].substring(0, 3));
            itemNome.setText(jogador.getNome());

        } else {
            itemPosicao.setVisibility(View.GONE);
            itemNome.setText(jogador.getNome().split(" ")[0]);
            itemNumber.setTextSize(18);
            itemNome.setTextSize(18);
            switch (hasHeCard(position)){
                case 1:
                    itemPunicao.setBackground(ctx.getDrawable(R.drawable.acao_add_card_amarelo));
                    itemPunicao.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    itemPunicao.setBackground(ctx.getDrawable(R.drawable.acao_add_card_vermelho));
                    itemPunicao.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }

        int num = jogador.getNumero();
        itemNumber.setText(num<10 ? "0"+num : ""+num);
        return v;
    }

    private int hasHeCard(int position) {
        int amarelos = 0;
        for (Score s : acoesTime) {
            if (s.getIdJogador() == getItem(position).getId()){
                if(s.getTipo() == s.TIPO_VERMELHO){
                    return 2;
                } else if(s.getTipo() == s.TIPO_AMARELO){
                    if(++amarelos==2) break;
                }
            }
        }
        return amarelos;
    }
}
