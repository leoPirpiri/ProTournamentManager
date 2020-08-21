package com.leoPirpiri.protournamentmanager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import control.Olimpia;
import model.Equipe;
import model.Jogador;
import model.NoPartida;
import model.Score;
import model.Torneio;

public class PartidaActivity extends AppCompatActivity {
    private boolean relogio_parado;
    private boolean atualizar = false;
    private long deslocamento;
    private int partidaIndice;

    private Olimpia santuarioOlimpia;
    private NoPartida partida;
    private Equipe mandante;
    private Equipe visitante;
    private Chronometer relogio;
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
    private Button btn_finalizar_partida;
    private AlertDialog alertaDialog;
    private JogadoresAdapter jam;
    private JogadoresAdapter jav;

    private Drawable ic_gol;
    private Drawable ic_falta;
    private Drawable ic_cartao_vermelho;
    private Drawable ic_cartao_amarelo;
    private Drawable ic_del_default;
    private Drawable ic_del_desabled;
    private Drawable ic_gol_desabled;
    private Drawable ic_cartao_desabled;

    @RequiresApi(api = Build.VERSION_CODES.N)
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

        btn_finalizar_partida = findViewById(R.id.btn_encerrar_partida);

        ic_gol = getDrawable(R.drawable.acao_add_ponto);
        ic_falta = getDrawable(R.drawable.acao_add_falta);
        ic_cartao_vermelho = getDrawable(R.drawable.acao_add_card_vermelho);
        ic_cartao_amarelo = getDrawable(R.drawable.acao_add_card_amarelo);
        ic_cartao_desabled = getDrawable(R.drawable.acao_add_card_desabled);
        ic_gol_desabled = getDrawable(R.drawable.acao_add_ponto_desabled);
        ic_del_default = getDrawable(R.drawable.acao_del_default);
        ic_del_desabled = getDrawable(R.drawable.acao_del_default_desabled);

        Intent intent = getIntent();
        partidaIndice = intent.getIntExtra("partida", -1);
        metodoRaiz();
        atualizarCampos();

        ltv_jogadores_mandantes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                montarAlertaAcaoPartida(true, jam.getItem(position), jam.getAcoesTime());
            }
        });

        ltv_jogadores_visitantes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                montarAlertaAcaoPartida(false, jav.getItem(position), jav.getAcoesTime());
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(atualizar){
            CarrierSemiActivity.persistirSantuario(PartidaActivity.this, santuarioOlimpia);
            atualizar=false;
        }
    }

    private boolean isSimulacao(){
        return partidaIndice==-1;
    }

    private void metodoRaiz() {
        //Carrega ou inicia o santuário onde ocorre os jogos.
        Torneio t;
        santuarioOlimpia = CarrierSemiActivity.carregarSantuario(PartidaActivity.this);
        if (isSimulacao()) {
            t = santuarioOlimpia.getSimulacao();
            if (t == null){
                t = new Torneio(990000, getResources().getString(R.string.novo_torneio));
                for (int i=0; i<=1; i++) {
                    t.addTime(new Equipe(t.getNovaEquipeId(),
                            getResources().getString(R.string.hint_nome_equipe),
                            getResources().getString(R.string.hint_sigla_equipe)));
                }
                String nome_padrao = getResources().getStringArray(R.array.partida_nomes)[0];
                partida = new NoPartida(02, nome_padrao);
                partida.setMandante(new NoPartida(01, nome_padrao, 990100));
                partida.setVisitante(new NoPartida(03, nome_padrao, 990200));
                t.setTabela(partida);
                santuarioOlimpia.setSimulacao(t);
                atualizar=true;
            } else {
                partida = t.getTabela().getRaiz();
            }
        } else {
            t = santuarioOlimpia.getTorneio(santuarioOlimpia.extrairIdEntidadeSuperiorLv0(partidaIndice));
            partida = t.getTabela().getPartida(partidaIndice-t.getId());
        }
        mandante = t.getTime(partida.getMandante().getCampeaoId());
        visitante = t.getTime(partida.getVisitante().getCampeaoId());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void atualizarCampos() {
        txv_partida_nome.setText(partida.getNome());
        txv_partida_sigla_mandante.setText(mandante.getSigla());
        txv_partida_sigla_visitante.setText(visitante.getSigla());
        txv_partida_nome_mandante.setText(mandante.getNome());
        txv_partida_nome_visitante.setText(visitante.getNome());

        ArrayList<List> acoesGerais = partida.getScoreGeral();
        HashMap<String, Integer> placar = (HashMap) acoesGerais.get(2);
        atualizarCamposMandante(acoesGerais);
        atualizarCamposVisitante(acoesGerais);
        ativarFinalizarPartida();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void atualizarCamposMandante(ArrayList<List> acoesGerais) {
        HashMap<String, Integer> placar = (HashMap) acoesGerais.get(2);
        txv_partida_score_ponto_mandante.setText(Integer.toString(placar.getOrDefault("Mand_"+Score.TIPO_PONTO, 0)));
        txv_partida_score_falta_mandante.setText(Integer.toString(placar.getOrDefault("Mand_"+Score.TIPO_FALTA_INDIVIDUAL, 0)));

        listarJogadoresMandantes((ArrayList<Score>) acoesGerais.get(0));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void atualizarCamposVisitante(ArrayList<List> acoesGerais) {
        HashMap<String, Integer> placar = (HashMap) acoesGerais.get(2);
        txv_partida_score_ponto_visitante.setText(Integer.toString(placar.getOrDefault("Vist_"+Score.TIPO_PONTO, 0)));
        txv_partida_score_falta_visitante.setText(Integer.toString(placar.getOrDefault("Vist_"+Score.TIPO_FALTA_INDIVIDUAL, 0)));

        listarJogadoresVisitantes((ArrayList<Score>) acoesGerais.get(1));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void atualizarCamposTime(boolean isMandante) {
        if(isMandante){
            atualizarCamposMandante(partida.getScoreGeral());
        } else {
            atualizarCamposVisitante(partida.getScoreGeral());
        }

    }

    private void listarJogadoresMandantes(ArrayList<Score> acoesMandantes){
        if (isSimulacao()) {
            if (mandante.getJogadores().isEmpty()) {
                preencherEquipe(mandante);
            }
        }

        jam = new JogadoresAdapter(PartidaActivity.this, mandante.getJogadores(), acoesMandantes);
        ltv_jogadores_mandantes.setAdapter(jam);
        jam.notifyDataSetChanged();
    }
    private void listarJogadoresVisitantes(ArrayList<Score> acoesVisitantes){
        if (isSimulacao()) {
            if (visitante.getJogadores().isEmpty()) {
                preencherEquipe(visitante);
            }
        }
        jav = new JogadoresAdapter(PartidaActivity.this, visitante.getJogadores(), acoesVisitantes);
        ltv_jogadores_visitantes.setAdapter(jav);
        jav.notifyDataSetChanged();
    }

    private void montarAlertaEquipeImcompleta(){

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void montarAlertaAcaoPartida(boolean isMandante, Jogador j, ArrayList<Score> acoesTime) {
        int[] acoes_jogador = {0,0,0,0,0};
        for (Score s : acoesTime){
            if(s.getIdJogador() == j.getId()){
                acoes_jogador[s.getTipo()]+=1;
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.alerta_acao_placar, null);

        TextView acao_jogador_number = view.findViewById(R.id.acao_jogador_number);
        TextView acao_jogador_posicao = view.findViewById(R.id.acao_jogador_posicao);
        TextView acao_jogador_nome = view.findViewById(R.id.acao_jogador_nome);
        LinearLayout acao_jogador_segundo_amarelo = view.findViewById(R.id.acao_jogador_segundo_amarelo);

        Button btn_del_gol = view.findViewById(R.id.btn_del_acao_gol);
        Button btn_del_falta = view.findViewById(R.id.btn_del_acao_falta);
        Button btn_del_vermelho = view.findViewById(R.id.btn_del_acao_cartao_vermelho);
        Button btn_del_amarelo = view.findViewById(R.id.btn_del_acao_cartao_amarelo);
        Button btn_add_gol = view.findViewById(R.id.btn_add_acao_gol);
        Button btn_add_falta = view.findViewById(R.id.btn_add_acao_falta);
        Button btn_add_vermelho = view.findViewById(R.id.btn_add_acao_cartao_vermelho);
        Button btn_add_amarelo = view.findViewById(R.id.btn_add_acao_cartao_amarelo);

        acao_jogador_number.setText(Integer.toString(j.getNumero()));
        acao_jogador_posicao.setText(getResources().getStringArray(R.array.posicoes_jogador)[j.getPosicao()].substring(0, 3));
        acao_jogador_nome.setText(j.getNome());

        btn_del_falta.setBackground(ic_falta);
        btn_del_falta.setForeground(ic_del_default);
        btn_del_amarelo.setBackground(ic_cartao_amarelo);
        btn_del_amarelo.setForeground(ic_del_default);
        btn_del_vermelho.setBackground(ic_cartao_vermelho);
        btn_del_vermelho.setForeground(ic_del_default);

        if(acoes_jogador[Score.TIPO_AMARELO]>=2 || acoes_jogador[Score.TIPO_VERMELHO] !=0){
            //Jogador expulso: NÃO recebe ponto, falta ou novo cartão
            btn_add_gol.setBackground(ic_gol_desabled);
            btn_add_falta.setBackground(ic_falta);
            btn_add_vermelho.setBackground(ic_cartao_desabled);
            btn_add_amarelo.setBackground(ic_cartao_desabled);
            btn_del_gol.setBackground(ic_gol_desabled);
            btn_del_gol.setForeground(ic_del_desabled);
            btn_del_falta.setBackground(ic_falta);
            btn_del_falta.setForeground(ic_del_desabled);
            if(acoes_jogador[Score.TIPO_VERMELHO] !=0) {
                btn_del_vermelho.setBackground(ic_cartao_vermelho);
                btn_del_vermelho.setForeground(ic_del_default);
                btn_del_vermelho.setEnabled(true);
                btn_del_amarelo.setBackground(ic_cartao_desabled);
                btn_del_amarelo.setForeground(ic_del_desabled);
            } else {
                btn_del_amarelo.setBackground(ic_cartao_amarelo);
                btn_del_amarelo.setForeground(ic_del_default);
                btn_del_amarelo.setEnabled(true);
                btn_del_vermelho.setBackground(ic_cartao_desabled);
                btn_del_vermelho.setForeground(ic_del_desabled);
            }
        } else {
            //Linha dos pontos
            if(acoes_jogador[Score.TIPO_PONTO]==0){
                btn_del_gol.setBackground(ic_gol_desabled);
                btn_del_gol.setForeground(ic_del_desabled);
            } else {
                btn_del_gol.setBackground(ic_gol);
                btn_del_gol.setForeground(ic_del_default);
                btn_del_gol.setEnabled(true);
            }
            btn_add_gol.setBackground(ic_gol);
            btn_add_gol.setEnabled(true);
            //Linha das faltas
            if(acoes_jogador[Score.TIPO_FALTA_INDIVIDUAL]==0){
                btn_del_falta.setBackground(ic_falta);
                btn_del_falta.setForeground(ic_del_desabled);
            } else {
                btn_del_falta.setBackground(ic_falta);
                btn_del_falta.setForeground(ic_del_default);
                btn_del_falta.setEnabled(true);
            }
            btn_add_falta.setBackground(ic_falta);
            btn_add_falta.setEnabled(true);
            //Linha do cartão vermelho
            btn_del_vermelho.setBackground(ic_cartao_desabled);
            btn_del_vermelho.setForeground(ic_del_desabled);
            btn_add_vermelho.setBackground(ic_cartao_vermelho);
            btn_add_vermelho.setEnabled(true);
            //Linha do cartão amarelo
            if(acoes_jogador[Score.TIPO_AMARELO]==0){
                btn_del_amarelo.setBackground(ic_cartao_desabled);
                btn_del_amarelo.setForeground(ic_del_desabled);
            } else {
                btn_del_amarelo.setBackground(ic_cartao_amarelo);
                btn_del_amarelo.setForeground(ic_del_default);
                btn_del_amarelo.setEnabled(true);
                acao_jogador_segundo_amarelo.setVisibility(View.VISIBLE);
            }
            btn_add_amarelo.setEnabled(true);
            btn_add_amarelo.setBackground(ic_cartao_amarelo);
        }

        btn_add_gol.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onClick(View arg0) {
                adicionarAcaoJogador(isMandante, new Score(partidaIndice, j.getId(), Score.TIPO_PONTO));
                alertaDialog.dismiss();
            }
        });

        btn_add_falta.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onClick(View arg0) {
                adicionarAcaoJogador(isMandante, new Score(partidaIndice, j.getId(), Score.TIPO_FALTA_INDIVIDUAL));
                alertaDialog.dismiss();
            }
        });

        btn_add_vermelho.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onClick(View arg0) {
                adicionarAcaoJogador(isMandante, new Score(partidaIndice, j.getId(), Score.TIPO_VERMELHO));
                alertaDialog.dismiss();
            }
        });

        btn_add_amarelo.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onClick(View arg0) {
                adicionarAcaoJogador(isMandante, new Score(partidaIndice, j.getId(), Score.TIPO_AMARELO));
                alertaDialog.dismiss();
            }
        });

        btn_del_gol.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onClick(View arg0) {
                apagarAcaoJogador(isMandante, new Score(partidaIndice, j.getId(), Score.TIPO_PONTO));
                alertaDialog.dismiss();
            }
        });

        btn_del_falta.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onClick(View arg0) {
                apagarAcaoJogador(isMandante, new Score(partidaIndice, j.getId(), Score.TIPO_FALTA_INDIVIDUAL));
                alertaDialog.dismiss();
            }
        });

        btn_del_vermelho.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onClick(View arg0) {
                apagarAcaoJogador(isMandante, new Score(partidaIndice, j.getId(), Score.TIPO_VERMELHO));
                alertaDialog.dismiss();
            }
        });

        btn_del_amarelo.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onClick(View arg0) {
                apagarAcaoJogador(isMandante, new Score(partidaIndice, j.getId(), Score.TIPO_AMARELO));
                alertaDialog.dismiss();
            }
        });

        view.findViewById(R.id.btn_cancelar_acao).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                alertaDialog.dismiss();
            }
        });

        builder.setView(view);
        //define o titulo
        builder.setTitle(R.string.title_alerta_partida_acao_jogador);
        mostrarAlerta(builder);
    }

    private void montarAlertaDetalhesJogador(){

    }

    private void mostrarAlerta(AlertDialog.Builder builder){
        alertaDialog = builder.create();
        alertaDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void adicionarAcaoJogador(boolean isMandante, Score s) {
        partida.addScore(s);
        atualizar=true;
        atualizarCamposTime(isMandante);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void apagarAcaoJogador(boolean isMandante, Score s) {
        partida.delScore(s);
        atualizar=true;
        atualizarCamposTime(isMandante);
    }

    private void preencherEquipe(Equipe equipe) {
        int tamanhoArrayPosicoes = getResources().getStringArray(R.array.posicoes_jogador).length;
        for (int i=1; i<=11; i++) {
            equipe.addJogador(new Jogador(equipe.getNovoJogadorId(),
                    "Jogador"+i,
                    i%tamanhoArrayPosicoes,
                    i
                ));
        }
    }

    private void ativarFinalizarPartida() {
        if(mandante.getJogadores().isEmpty() || visitante.getJogadores().isEmpty()){
            montarAlertaEquipeImcompleta();
        } else {
            btn_finalizar_partida.setEnabled(true);
            btn_finalizar_partida.setBackground(getDrawable(R.drawable.button_shape_enabled));
        }
    }

    private void desativarFinalizarPartida() {
        btn_finalizar_partida.setEnabled(false);
        btn_finalizar_partida.setBackground(getDrawable(R.drawable.button_shape_desabled));
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
            desativarFinalizarPartida();
            relogio_parado = false;
        } else {
            v.setBackground(getDrawable(android.R.drawable.ic_media_play));
            relogio.stop();
            deslocamento = SystemClock.elapsedRealtime() - relogio.getBase();
            ativarFinalizarPartida();
            relogio_parado = true;
        }
    }
}
