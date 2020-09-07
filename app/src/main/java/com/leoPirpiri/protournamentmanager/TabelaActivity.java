package com.leoPirpiri.protournamentmanager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import control.Olimpia;
import model.Equipe;
import model.NoPartida;
import model.Score;
import model.Torneio;

public class TabelaActivity extends AppCompatActivity {
    private int torneioIndice;
    private Olimpia santuarioOlimpia;
    private MediaPlayer efeitos_sonoros;
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
    private AlertDialog alertaDialog;

    @RequiresApi(api = Build.VERSION_CODES.N)
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
            efeitos_sonoros = MediaPlayer.create(TabelaActivity.this, R.raw.tema_tabela);
            play_efeito_sonoro();
        }

        ltv_oitavas_esquerda.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                montarAlertaAbrirPartida(partidasAdapterE.getItem(position));
            }
        });

        ltv_oitavas_direita.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                montarAlertaAbrirPartida(partidasAdapterD.getItem(position));
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        pause_efeito_sonoro();
    }

    @Override
    protected void onResume() {
        super.onResume();
        metodoRaiz();
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

    private void montarAlertaAbrirPartida(NoPartida partida){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        Equipe mandante = torneio.getTime(partida.getMandante().getCampeaoId());
        Equipe visitante = torneio.getTime(partida.getVisitante().getCampeaoId());

        View view = getLayoutInflater().inflate(R.layout.alerta_default, null);
        Button btn_confirmar = view.findViewById(R.id.btn_confirmar_default);
        Button btn_cancelar = view.findViewById(R.id.btn_cancelar_default);
        btn_confirmar.setVisibility(View.VISIBLE);
        btn_cancelar.setVisibility(View.VISIBLE);

        TextView msg = view.findViewById(R.id.msg_alerta_default);
        msg.setVisibility(View.VISIBLE);
        String alerta = getString(partida.isEncerrada() ? R.string.lbl_msg_inicio_finalizada : R.string.lbl_msg_inicio_aberta)+
                          " "+partida.getNome()+"?";
        String nomeMandante = alerta + "\n\n" + mandante.getNome();
        String nomeVisitante = visitante.getNome();

        SpannableString textoNegrito = new SpannableString(nomeMandante + "\n" + getString(R.string.lbl_vs_default) + "\n" + nomeVisitante);
        textoNegrito.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), nomeMandante.length(), textoNegrito.length() - nomeVisitante.length(), 0);
        msg.setText(textoNegrito);

        btn_confirmar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                alertaDialog.dismiss();
                abrirPartida(partida.getId());
            }
        });

        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //desfaz o alerta.
                alertaDialog.dismiss();
            }
        });

        builder.setView(view);
        builder.setTitle(R.string.title_alerta_confir_abrir_partida);
        mostrarAlerta(builder);
    }

    private void mostrarAlerta(AlertDialog.Builder builder) {
        mostrarAlerta(builder, R.drawable.background_alerta);
    }

    private void mostrarAlerta(AlertDialog.Builder builder, int background) {
        alertaDialog = builder.create();
        alertaDialog.getWindow().setBackgroundDrawable(getDrawable(background));
        alertaDialog.show();
    }

    private void listarPartidas() {
        int tamanhoTorneio = torneio.getTimes().size();
        boolean precessoresQuartas = tamanhoTorneio != 16;
        boolean precessoresSemi = true;
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
                desenharChaveTabela(ltv_quartafinal4, torneio.getTabela().getPartida(28), precessoresQuartas);
                precessoresSemi = false;
            case 7:
                desenharChaveTabela(ltv_quartafinal2, torneio.getTabela().getPartida(12), precessoresQuartas);
            case 6:
                desenharChaveTabela(ltv_quartafinal3, torneio.getTabela().getPartida(20), precessoresQuartas);
            case 5:
                desenharChaveTabela(ltv_quartafinal1, torneio.getTabela().getPartida(4), precessoresQuartas);
            default:
                desenharChaveTabela(ltv_semifinal2, torneio.getTabela().getPartida(24), precessoresSemi);
                desenharChaveTabela(ltv_semifinal1, torneio.getTabela().getPartida(8), precessoresSemi);
                desenharChaveTabela(ltv_final, torneio.getTabela().getPartida(16),false);
                break;
        }
    }

    private void desenharChaveTabela(LinearLayout v, NoPartida partida, boolean separarPrecessores){
        int id = partida.getId();
        v.setTransitionName(String.valueOf(id));
        v.setVisibility(View.VISIBLE);
        LinearLayout ltn0 = (LinearLayout) v.getChildAt(id <= 16 ? 0 : 1);
        LinearLayout ltn1 = (LinearLayout) ltn0.getChildAt(0);
        if(separarPrecessores){
            if(partida.getMandante().getMandante().getCampeaoId()<=0 ||
               partida.getMandante().getVisitante().getCampeaoId()<=0 ){
                LinearLayout lt_linha = (LinearLayout) ltn1.getChildAt(id <= 16 ? 0 : 2);
                lt_linha.setVisibility(View.INVISIBLE);
            }
        }
        LinearLayout ltn2 = (LinearLayout) ltn1.getChildAt(1);
        TextView mandanteSigla = (TextView) ltn2.getChildAt(id<=16 ? 0 : 1);
        TextView mandanteScore = (TextView) ltn2.getChildAt(id<=16 ? 1 : 0);

        ltn1 = (LinearLayout) ltn0.getChildAt(ltn0.getChildCount()-1);
        if(separarPrecessores){
            if(partida.getVisitante().getMandante().getCampeaoId()<=0 ||
                    partida.getVisitante().getVisitante().getCampeaoId()<=0 ){
                LinearLayout lt_linha = (LinearLayout) ltn1.getChildAt(id <= 16 ? 0 : 2);
                lt_linha.setVisibility(View.INVISIBLE);
            }
        }
        ltn2 = (LinearLayout) ltn1.getChildAt(1);
        TextView visitanteSigla = (TextView) ltn2.getChildAt(id>=16 ? 1 : 0);
        TextView visitanteScore = (TextView) ltn2.getChildAt(id>=16 ? 0 : 1);

        if(partida.isEncerrada() && partida.getMandante().getCampeaoId()>0 && partida.getVisitante().getCampeaoId()>0){
            HashMap<String, Integer> placar = partida.getDetalhesPartida();
            mandanteScore.setText(Integer.toString(placar.getOrDefault("Mand_"+ Score.TIPO_PONTO, 0)));
            visitanteScore.setText(Integer.toString(placar.getOrDefault("Vist_"+Score.TIPO_PONTO, 0)));
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

    public void eventoLayoutParticaOnClick(View view) {
        int id = Integer.parseInt(view.getTransitionName());
        NoPartida partida = torneio.getTabela().getPartida(id);
        if (partida.getMandante().isEncerrada() && partida.getVisitante().isEncerrada()) {
            montarAlertaAbrirPartida(partida);
        }
    }

    private void abrirPartida(int idPartida){
        Intent intent = new Intent(getApplicationContext(), PartidaActivity.class);
        intent.putExtra("partida", torneioIndice+idPartida);
        startActivity(intent);
    }

    private void play_efeito_sonoro(){
        efeitos_sonoros.setLooping(false);
        efeitos_sonoros.seekTo(0);
        efeitos_sonoros.start();
    }

    private void pause_efeito_sonoro(){
        if(efeitos_sonoros.isPlaying()){
            efeitos_sonoros.stop();
        }
    }
}