package com.leoPirpiri.protournamentmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.TextView;

import control.Olimpia;
import model.Equipe;
import model.Jogador;
import model.NoPartida;

public class PartidaActivity extends AppCompatActivity {
    private boolean relogio_parado;
    private long deslocamento;
    private int partidaIndice;

    private Chronometer relogio;
    private Olimpia santuarioOlimpia;
    private NoPartida partida;
    private Equipe mandante;
    private Equipe visitante;
    private ListView ltv_jogadores_mandantes;
    private ListView ltv_jogadores_visitantes;
    private TextView txv_partida_nome;
    private TextView txv_partida_sigla_mandante;
    private TextView txv_partida_sigla_visitante;
    private TextView txv_partida_score_ponto_mandante;
    private TextView txv_partida_score_ponto_visitante;
    private TextView txv_partida_score_falta_mandante;
    private TextView txv_partida_score_falta_visitante;
    private TextView txv_partida_nome_mandante;
    private TextView txv_partida_nome_visitante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partida);
        relogio_parado = true;
        deslocamento = 0;

        relogio = findViewById(R.id.cronometro);
        ltv_jogadores_mandantes = findViewById(R.id.list_partida_jogadores_mandantes);
        ltv_jogadores_visitantes = findViewById(R.id.list_partida_jogadores_visitantes);

        txv_partida_nome = findViewById(R.id.txv_partida_nome);
        txv_partida_sigla_mandante = findViewById(R.id.txv_partida_sigla_mandante);
        txv_partida_sigla_visitante = findViewById(R.id.txv_partida_sigla_visitante);
        txv_partida_score_ponto_mandante = findViewById(R.id.txv_partida_score_ponto_mandante);
        txv_partida_score_ponto_visitante = findViewById(R.id.txv_partida_score_ponto_visitante);
        txv_partida_score_falta_mandante = findViewById(R.id.txv_partida_score_falta_mandante);
        txv_partida_score_falta_visitante = findViewById(R.id.txv_partida_score_falta_visitante);
        txv_partida_nome_mandante = findViewById(R.id.txv_partida_nome_mandante);
        txv_partida_nome_visitante = findViewById(R.id.txv_partida_nome_visitante);

        Intent intent = getIntent();
        partidaIndice = intent.getIntExtra("partida", -1);
        if (partidaIndice==-1) {
            String nome_padrao = getResources().getStringArray(R.array.partida_nomes)[0];
            visitante = mandante = new Equipe(999900,
                                getResources().getString(R.string.hint_nome_equipe),
                                getResources().getString(R.string.hint_sigla_equipe));
            partida = new NoPartida(999902, nome_padrao);
            partida.setMandante(new NoPartida(999901, nome_padrao, mandante.getId()));
            partida.setVisitante(new NoPartida(999903, nome_padrao, visitante.getId()));

        } else {
            metodoRaiz();
        }
        atualizarCampos();
        listarJogadores();
    }

    private void metodoRaiz() {
        //Carrega ou inicia o santuário onde ocorre os jogos.
        santuarioOlimpia = CarrierSemiActivity.carregarSantuario(PartidaActivity.this);
    }

    private void atualizarCampos() {
        txv_partida_nome.setText(partida.getNome());
        txv_partida_sigla_mandante.setText(mandante.getSigla());
        txv_partida_sigla_visitante.setText(visitante.getSigla());
        int[] pontos = partida.getPlacarPontos();
        txv_partida_score_ponto_mandante.setText(Integer.toString(pontos[0]));
        txv_partida_score_ponto_visitante.setText(Integer.toString(pontos[1]));
        int[] faltas = partida.getPlacarFaltas();
        txv_partida_score_falta_mandante.setText(Integer.toString(faltas[0]));
        txv_partida_score_falta_visitante.setText(Integer.toString(faltas[1]));
        txv_partida_nome_mandante.setText(mandante.getNome());
        txv_partida_nome_visitante.setText(visitante.getNome());
    }

    private void listarJogadores(){
        if (mandante.getJogadores().isEmpty()) {
            preencherEquipe(mandante);
        }
        if (visitante.getJogadores().isEmpty()) {
            preencherEquipe(visitante);
        }
        JogadoresAdapter jam = new JogadoresAdapter(PartidaActivity.this, mandante.getJogadores(), false);
        JogadoresAdapter jav = new JogadoresAdapter(PartidaActivity.this, mandante.getJogadores(), false);
        ltv_jogadores_mandantes.setAdapter(jam);
        ltv_jogadores_visitantes.setAdapter(jav);
        jam.notifyDataSetChanged();
        jav.notifyDataSetChanged();
    }

    private void preencherEquipe(Equipe equipe) {
        int tamanhoArrayPosicoes = getResources().getStringArray(R.array.posicoes_jogador).length;
        for (int i=1; i<=11; i++) {
            equipe.addJogador(new Jogador(equipe.getNovoJogadorId(),
                    "Jogador"+i,
                    i%tamanhoArrayPosicoes,
                    i));
        }
    }

    public void zerarCronometro(View v) {
        if(relogio_parado) {
            relogio.setBase(SystemClock.elapsedRealtime());
            deslocamento = 0;
        }
    }

    public void rodarCronometro(View v) {
        if(relogio_parado) {
            v.setBackground(getDrawable(android.R.drawable.ic_media_pause));
            relogio.setBase(SystemClock.elapsedRealtime() - deslocamento);
            relogio.start();
            relogio_parado = false;
        } else {
            v.setBackground(getDrawable(android.R.drawable.ic_media_play));
            relogio.stop();
            deslocamento = SystemClock.elapsedRealtime() - relogio.getBase();
            relogio_parado = true;
        }
    }
}
