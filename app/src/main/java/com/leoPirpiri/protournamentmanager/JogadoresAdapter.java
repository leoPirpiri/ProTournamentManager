package com.leoPirpiri.protournamentmanager;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

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

    @RequiresApi(api = Build.VERSION_CODES.N)
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
        TextView itemPontos = v.findViewById(R.id.adp_jogador_points);
        ImageView itemPunicao = v.findViewById(R.id.adp_jogador_punicao);

        if (acoesTime == null) {
            itemPosicao.setText(ctx.getResources().getStringArray(R.array.posicoes_jogador)[jogador.getPosicao()].substring(0, 3));
            itemNome.setText(jogador.getNome());

        } else {
            itemPosicao.setVisibility(View.GONE);
            itemNome.setText(getNomeCompacto(jogador.getNome()));
            itemNumber.setTextSize(18);
            itemNome.setTextSize(18);
            HashMap<Integer, Integer> acoesIndividuais = getAcoesIndividuais(position);
            if (acoesIndividuais.containsKey(Score.TIPO_PONTO)){
                int points = acoesIndividuais.get(Score.TIPO_PONTO);
                if(points!=1){
                    itemPontos.setText(Integer.toString(points));
                }
                itemPontos.setVisibility(View.VISIBLE);
            } else {
                itemPontos.setVisibility(View.GONE);
            }
            int quantCartoes = acoesIndividuais.getOrDefault(Score.TIPO_AMARELO, 0)
                    + acoesIndividuais.getOrDefault(Score.TIPO_VERMELHO, 0)*2;
            switch (quantCartoes){
                case 0:
                    itemPunicao.setVisibility(View.GONE);
                    break;
                case 1:
                    itemPunicao.setBackground(ctx.getDrawable(R.drawable.acao_add_card_amarelo));
                    itemPunicao.setVisibility(View.VISIBLE);
                    break;
                default:
                    itemPunicao.setBackground(ctx.getDrawable(R.drawable.acao_add_card_vermelho));
                    itemPunicao.setVisibility(View.VISIBLE);
                    break;
            }
        }
        int num = jogador.getNumero();
        itemNumber.setText(num<10 ? "0"+num : ""+num);
        return v;
    }

    public HashMap getAcoesIndividuais(int position) {
        HashMap<Integer, Integer> acoes = new HashMap<>();
        for (Score s : acoesTime) {
            if (s.getIdJogador() == getItem(position).getId()){
                acoes.compute(s.getTipo(), (k, v) -> (v == null) ? 1 : ++v);
            }
        }
        return acoes;
    }

    @org.jetbrains.annotations.NotNull
    private String getNomeCompacto(String nomeBruto){
        ArrayList<String> pulos = new ArrayList(Arrays.asList("da", "do", "de", "das", "dos"));
        int limite = 10;
        int margemErro = limite+4;
        String nome = "";
        for(String s : nomeBruto.split(" ")){
            if(!pulos.contains(s)){
                if(nome.length() + s.length() <= limite){
                    nome = nome.concat(s.concat(" "));
                } else if (nome.length() + s.length() <= margemErro){
                    nome = nome.concat(s);
                    break;
                } else{
                    break;
                }
            }
        }
        return nome.trim();
    }
}
