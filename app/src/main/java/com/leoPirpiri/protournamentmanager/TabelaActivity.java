package com.leoPirpiri.protournamentmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import control.Olimpia;
import model.Equipe;
import model.NoPartida;
import model.Torneio;

public class TabelaActivity extends AppCompatActivity {
    private int torneioIndice;
    private Olimpia santuarioOlimpia;
    private Torneio torneio;
    private LinearLayout ltv_final;
    private LinearLayout ltv_semifinal1;
    private LinearLayout ltv_semifinal2;
    private LinearLayout ltv_quartafinal1;
    private LinearLayout ltv_quartafinal2;
    private LinearLayout ltv_quartafinal3;
    private LinearLayout ltv_quartafinal4;
    private ListView ltv_oitavas_esquerda;
    private ListView ltv_oitavas_direita;
    private PartidasAdapter partidasAdapterE;
    private PartidasAdapter partidasAdapterD;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabela);
        ltv_final = findViewById(R.id.layout_final);
        ltv_semifinal1 = findViewById(R.id.layout_semi1);
        ltv_semifinal2 = findViewById(R.id.layout_semi2);
        ltv_quartafinal1 = findViewById(R.id.layout_quarta1);
        ltv_quartafinal2 = findViewById(R.id.layout_quarta2);
        ltv_quartafinal3 = findViewById(R.id.layout_quarta3);
        ltv_quartafinal4 = findViewById(R.id.layout_quarta4);
        ltv_oitavas_esquerda = findViewById(R.id.ltv_partidas_oitava_e);
        ltv_oitavas_direita = findViewById(R.id.ltv_partidas_oitava_d);
        Intent intent = getIntent();
        Bundle dados = intent.getExtras();
        if (dados!=null) {
            torneioIndice = dados.getInt("torneio");
            metodoRaiz();
        }
    }

    private void metodoRaiz() {
        santuarioOlimpia = CarrierSemiActivity.carregarSantuario(TabelaActivity.this);
        torneio = santuarioOlimpia.getTorneio(torneioIndice);
        if(torneio != null) {
            partidasAdapterE = new PartidasAdapter(TabelaActivity.this, torneio, true);
            partidasAdapterD = new PartidasAdapter(TabelaActivity.this, torneio, false);
            ltv_oitavas_esquerda.setAdapter(partidasAdapterE);
            ltv_oitavas_direita.setAdapter(partidasAdapterD);
            listarPartidas();
        } else {
            Toast.makeText(TabelaActivity.this, R.string.dados_erro_transitar_em_activity, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void listarPartidas() {
        int tamanhoTorneio = torneio.getTimes().size();
        if(tamanhoTorneio>9) {
            ltv_oitavas_direita.deferNotifyDataSetChanged();
            ltv_oitavas_direita.setVisibility(View.VISIBLE);
            tamanhoTorneio = 9;
        }
        switch (tamanhoTorneio){
            case 9:
                ltv_oitavas_esquerda.setVisibility(View.VISIBLE);
                ltv_oitavas_esquerda.deferNotifyDataSetChanged();
            case 8:
                desenharChaveTabela(ltv_quartafinal4, torneio.getTabela().getPartida(28));
            case 7:
                desenharChaveTabela(ltv_quartafinal2, torneio.getTabela().getPartida(12));
            case 6:
                desenharChaveTabela(ltv_quartafinal3, torneio.getTabela().getPartida(20));
            case 5:
                desenharChaveTabela(ltv_quartafinal1, torneio.getTabela().getPartida(4));
            default:
                desenharChaveTabela(ltv_semifinal2, torneio.getTabela().getPartida(24));
                desenharChaveTabela(ltv_semifinal1, torneio.getTabela().getPartida(8));
                desenharChaveTabela(ltv_final, torneio.getTabela().getPartida(16));
                break;
        }
    }

    private void desenharChaveTabela(LinearLayout v, NoPartida partida){
        int id = partida.getId();
        v.setVisibility(View.VISIBLE);
        LinearLayout ltn1 = (LinearLayout) v.getChildAt(0);
        if(partida.getMandante().getMandante().getCampeaoId()<=0||partida.getMandante().getVisitante().getCampeaoId()<=0) {
            LinearLayout lt_linha = (LinearLayout) ltn1.getChildAt(id <= 16 ? 0 : 2);
            lt_linha.setVisibility(View.INVISIBLE);
        }
        LinearLayout ltn2 = (LinearLayout) ltn1.getChildAt(1);
        TextView mandanteSigla = (TextView) ltn2.getChildAt(id<=16 ? 0 : 1);
        TextView mandanteScore = (TextView) ltn2.getChildAt(id<=16 ? 1 : 0);

        ltn1 = (LinearLayout) v.getChildAt(v.getChildCount()-1);
        if(partida.getMandante().getMandante().getCampeaoId()<=0 || partida.getMandante().getVisitante().getCampeaoId()<=0) {
            LinearLayout lt_linha = (LinearLayout) ltn1.getChildAt(id <= 16 ? 0 : 2);
            lt_linha.setVisibility(View.INVISIBLE);
        }
        ltn2 = (LinearLayout) ltn1.getChildAt(1);
        TextView visitanteSigla = (TextView) ltn2.getChildAt(id>=16 ? 1 : 0);
        TextView visitanteScore = (TextView) ltn2.getChildAt(id>=16 ? 0 : 1);

        if(partida.isEncerrada() && partida.getMandante().getCampeaoId()>0 && partida.getVisitante().getCampeaoId()>0){
            int[] pontos = partida.getPlacarPontos();
            mandanteScore.setText(Integer.toString(pontos[0]));
            visitanteScore.setText(Integer.toString(pontos[1]));
        } else {
            mandanteScore.setText(R.string.equipe_ponto_partida_aberta);
            visitanteScore.setText(R.string.equipe_ponto_partida_aberta);
        }
        if(partida.getMandante() != null && partida.getMandante().getCampeaoId() >0){
            mandanteSigla.setText(torneio.getTime(partida.getMandante().getCampeaoId()).getSigla());
        } else {
            mandanteSigla.setText(R.string.equipe_sigla_partida_aberta);
        }
        if(partida.getVisitante() != null && partida.getVisitante().getCampeaoId() >0){
            visitanteSigla.setText(torneio.getTime(partida.getVisitante().getCampeaoId()).getSigla());
        }else {
            visitanteSigla.setText(R.string.equipe_sigla_partida_aberta);
        }
    }

    public void exemplo(View view) {
        int id = Integer.parseInt(view.getTransitionName());
        LinearLayout ltn0 = (LinearLayout) view;
        LinearLayout ltn1 = (LinearLayout) ltn0.getChildAt(0);
        LinearLayout ltn2 = (LinearLayout) ltn1.getChildAt(1);
        TextView mandanteSigla = (TextView) ltn2.getChildAt(id<=16 ? 0 : 1);
        TextView mandanteScore = (TextView) ltn2.getChildAt(id<=16 ? 1 : 0);

        ltn1 = (LinearLayout) ltn0.getChildAt(ltn0.getChildCount()-1);
        ltn2 = (LinearLayout) ltn1.getChildAt(1);
        TextView visitanteSigla = (TextView) ltn2.getChildAt(id>=16 ? 1 : 0);
        TextView visitanteScore = (TextView) ltn2.getChildAt(id>=16 ? 0 : 1);
        CarrierSemiActivity.exemplo(this, mandanteSigla.getText()+" - " +mandanteScore.getText()+
                                    "\nVS\n"+visitanteSigla.getText()+" - " +visitanteScore.getText());
    }
}