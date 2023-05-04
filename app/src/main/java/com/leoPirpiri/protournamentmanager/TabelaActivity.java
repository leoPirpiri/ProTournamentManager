package com.leoPirpiri.protournamentmanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import adapters.PartidasAdapter;
import control.CarrierSemiActivity;
import model.Olimpia;
import model.Equipe;
import model.Partida;
import model.Torneio;

public class TabelaActivity extends AppCompatActivity {
    private String torneioIndice;
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
            torneioIndice = dados.getString("torneio");
            efeitos_sonoros = MediaPlayer.create(TabelaActivity.this, R.raw.tema_tabela);
            play_efeito_sonoro();
        }

        ltv_oitavas_esquerda.setOnItemClickListener((parent, view, position, id) -> {
            montarAlertaAbrirPartida(partidasAdapterE.getItem(position));
        });

        ltv_oitavas_direita.setOnItemClickListener((parent, view, position, id) -> {
            montarAlertaAbrirPartida(partidasAdapterD.getItem(position));
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
        torneio = santuarioOlimpia.getTorneioGerenciado(torneioIndice);
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

    private void montarAlertaAbrirPartida(Partida partida){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        Equipe mandante = torneio.buscarEquipe(partida.getMandante());
        Equipe visitante = torneio.buscarEquipe(partida.getVisitante());

        View view = getLayoutInflater().inflate(R.layout.alerta_default, null);
        Button btn_confirmar = view.findViewById(R.id.btn_confirmar_default);
        Button btn_cancelar = view.findViewById(R.id.btn_cancelar_default);
        btn_confirmar.setVisibility(View.VISIBLE);
        btn_cancelar.setVisibility(View.VISIBLE);

        TextView msg = view.findViewById(R.id.msg_alerta_default);
        msg.setVisibility(View.VISIBLE);
        String alerta = getString(partida.estaEncerrada() ? R.string.lbl_msg_inicio_finalizada : R.string.lbl_msg_inicio_aberta)+
                          " "+partida.getNome()+"?";
        String nomeMandante = alerta + "\n\n" + mandante.getNome();
        String nomeVisitante = visitante.getNome();

        SpannableString textoNegrito = new SpannableString(nomeMandante + "\n" + getString(R.string.lbl_vs_padrao) + "\n" + nomeVisitante);
        textoNegrito.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), nomeMandante.length(), textoNegrito.length() - nomeVisitante.length(), 0);
        msg.setText(textoNegrito);

        btn_confirmar.setOnClickListener(arg0 -> {
            alertaDialog.dismiss();
            abrirPartida(partida.getId());
        });

        btn_cancelar.setOnClickListener(arg0 -> alertaDialog.dismiss());

        builder.setView(view);
        builder.setTitle(R.string.titulo_alerta_confir_abrir_partida);
        mostrarAlerta(builder);
    }

    private void mostrarAlerta(AlertDialog.Builder builder) {
        mostrarAlerta(builder, R.drawable.background_alerta);
    }

    private void mostrarAlerta(AlertDialog.Builder builder, int background) {
        alertaDialog = builder.create();
        alertaDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, background));
        alertaDialog.show();
    }

    private void listarPartidas() {
        int tamanhoTorneio = torneio.getEquipes().size();
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
                desenharChaveTabela(ltv_quartafinal4, torneio.getTabela().buscarPartida(28), precessoresQuartas);
                precessoresSemi = false;
            case 7:
                desenharChaveTabela(ltv_quartafinal2, torneio.getTabela().buscarPartida(12), precessoresQuartas);
            case 6:
                desenharChaveTabela(ltv_quartafinal3, torneio.getTabela().buscarPartida(20), precessoresQuartas);
            case 5:
                desenharChaveTabela(ltv_quartafinal1, torneio.getTabela().buscarPartida(4), precessoresQuartas);
            default:
                desenharChaveTabela(ltv_semifinal2, torneio.getTabela().buscarPartida(24), precessoresSemi);
                desenharChaveTabela(ltv_semifinal1, torneio.getTabela().buscarPartida(8), precessoresSemi);
                desenharChaveTabela(ltv_final, torneio.getTabela().buscarPartida(16),false);
                break;
        }
    }

    private void desenharChaveTabela(LinearLayout v, Partida partida, boolean separarPrecessores){
        int id = partida.getId();
        v.setTransitionName(String.valueOf(id));
        v.setVisibility(View.VISIBLE);
        LinearLayout ltn0 = (LinearLayout) v.getChildAt(id <= 16 ? 0 : 1);
        LinearLayout ltn1 = (LinearLayout) ltn0.getChildAt(0);
        if(separarPrecessores){
            if(partida.getMandante()<=0 ||
               partida.getMandante()<=0 ){
                LinearLayout lt_linha = (LinearLayout) ltn1.getChildAt(id <= 16 ? 0 : 2);
                lt_linha.setVisibility(View.INVISIBLE);
            }
        }
        LinearLayout ltn2 = (LinearLayout) ltn1.getChildAt(1);
        TextView mandanteSigla = (TextView) ltn2.getChildAt(id<=16 ? 0 : 1);
        TextView mandanteScore = (TextView) ltn2.getChildAt(id<=16 ? 1 : 0);

        ltn1 = (LinearLayout) ltn0.getChildAt(ltn0.getChildCount()-1);
        if(separarPrecessores){
            if(partida.getVisitante()<=0 ||
                    partida.getVisitante()<=0 ){
                LinearLayout lt_linha = (LinearLayout) ltn1.getChildAt(id <= 16 ? 0 : 2);
                lt_linha.setVisibility(View.INVISIBLE);
            }
        }
        ltn2 = (LinearLayout) ltn1.getChildAt(1);
        TextView visitanteSigla = (TextView) ltn2.getChildAt(id>=16 ? 1 : 0);
        TextView visitanteScore = (TextView) ltn2.getChildAt(id>=16 ? 0 : 1);

        if(partida.estaEncerrada() && partida.getMandante()>0 && partida.getVisitante()>0){
            /*HashMap<String, Integer> placar = partida.getDetalhesPartida();
            mandanteScore.setText(Integer.toString(
                    placar.getOrDefault("Mand_"+ Score.TIPO_PONTO, 0)+
                    placar.getOrDefault("Vist_"+ Score.TIPO_AUTO_PONTO, 0)
            ));
            visitanteScore.setText(Integer.toString(
                    placar.getOrDefault("Vist_"+Score.TIPO_PONTO, 0)+
                    placar.getOrDefault("Mand_"+ Score.TIPO_AUTO_PONTO, 0)
            ));*/
        } else {
            mandanteScore.setText(R.string.equipe_ponto_partida_aberta);
            visitanteScore.setText(R.string.equipe_ponto_partida_aberta);
        }
        if(partida.getMandante() >0){
            mandanteSigla.setText(torneio.buscarEquipe(partida.getMandante()).getSigla());
        } else {
            mandanteSigla.setText(R.string.equipe_sigla_partida_aberta);
        }
        if(partida.getVisitante() >0){
            visitanteSigla.setText(torneio.buscarEquipe(partida.getVisitante()).getSigla());
        }else {
            visitanteSigla.setText(R.string.equipe_sigla_partida_aberta);
        }
    }

    public void eventoLayoutParticaOnClick(View view) {
        int id = Integer.parseInt(view.getTransitionName());
        Partida partida = torneio.getTabela().buscarPartida(id);
        if (partida.getMandante() == partida.getVisitante()) {
            montarAlertaAbrirPartida(partida);
        }
    }

    private void abrirPartida(int idPartida){
        Intent intent = new Intent(getApplicationContext(), PartidaActivity.class);
        intent.putExtra("partida", torneio.buscarUuid()+(torneio.getId()+idPartida));
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