package com.leoPirpiri.protournamentmanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import control.Olimpia;
import model.Equipe;
import model.Jogador;
import model.NoPartida;
import model.Torneio;

public class PartidaActivity extends AppCompatActivity {
    private boolean relogio_parado;
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

        Intent intent = getIntent();
        partidaIndice = intent.getIntExtra("partida", -1);
        metodoRaiz();

        atualizarCampos();
        listarJogadores();

        ltv_jogadores_mandantes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                montarAlertaAcaoPartida(jam.getItem(position));
            }
        });
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

    private void montarAlertaEquipeImcompleta(){
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//        View view = getLayoutInflater().inflate(R.layout.alerta_abrir_partida, null);
//        TextView msgAlerta = view.findViewById(R.id.msg_alerta_partida);
//
//        msgAlerta.setText(getString(partida.isEncerrada() ? R.string.lbl_msg_inicio_finalizada : R.string.lbl_msg_inicio_aberta)+
//                " "+partida.getNome()+"?");
//
//        view.findViewById(R.id.btn_confirmar_abrir_partida).setOnClickListener(new View.OnClickListener() {
//            public void onClick(View arg0) {
//                alertaDialog.dismiss();
//            }
//        });
//
//        builder.setView(view);
//        //define o titulo
//        builder.setTitle(R.string.title_alerta_partida_equipes_incompletas);
//        mostrarAlerta(builder);
    }

    private void montarAlertaAcaoPartida(Jogador j) {
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

        btn_add_vermelho.setEnabled(true);
        btn_add_vermelho.setBackground(getDrawable(R.drawable.acao_add_card_vermelho));

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
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//        View view = getLayoutInflater().inflate(R.layout.alerta_abrir_partida, null);
//        TextView msgAlerta = view.findViewById(R.id.msg_alerta_partida);
//
//        msgAlerta.setText(getString(partida.isEncerrada() ? R.string.lbl_msg_inicio_finalizada : R.string.lbl_msg_inicio_aberta)+
//                " "+partida.getNome()+"?");
//
//        view.findViewById(R.id.btn_confirmar_abrir_partida).setOnClickListener(new View.OnClickListener() {
//            public void onClick(View arg0) {
//                alertaDialog.dismiss();
//            }
//        });
//
//        builder.setView(view);
//        //define o titulo
//        builder.setTitle(R.string.title_alerta_partida_equipes_incompletas);
//        mostrarAlerta(builder);
    }

    private void mostrarAlerta(AlertDialog.Builder builder){
        alertaDialog = builder.create();
        alertaDialog.show();
    }

    private void listarJogadores(){
        if (isSimulacao()) {
            if (mandante.getJogadores().isEmpty()) {
                preencherEquipe(mandante);
            }
            if (visitante.getJogadores().isEmpty()) {
                preencherEquipe(visitante);
            }
        }
        ativarFinalizarPartida();

        jam = new JogadoresAdapter(PartidaActivity.this, mandante.getJogadores(), false);
        jav = new JogadoresAdapter(PartidaActivity.this, visitante.getJogadores(), false);
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
