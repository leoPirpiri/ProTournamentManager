package com.leoPirpiri.protournamentmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import adapters.JogadoresAdapter;
import control.CarrierSemiActivity;
import model.Olimpia;
import model.Equipe;
import model.Jogador;
import model.Partida;
import model.Score;
import model.Torneio;
import pl.droidsonroids.gif.GifImageView;

public class PartidaActivity extends AppCompatActivity {
    private static final int PADRAO_PARTIDA_SIMULACAO = 0;
    private boolean relogio_parado;
    private long deslocamento;

    private Olimpia santuarioOlimpia;
    private AlertDialog alertaDialog;
    private Torneio torneio;
    private Partida partida;
    private Equipe cobrador;
    private Equipe mandante;
    private Equipe visitante;
    private Chronometer relogio;
    private MediaPlayer efeitos_sonoros;
    private RecyclerView recyclerViewJogadoresMandantes;
    private RecyclerView recyclerViewJogadoresVisitantes;
    private JogadoresAdapter jam;
    private JogadoresAdapter jav;
    private ArrayList<Score> acoesMandantes;
    private ArrayList<Score> acoesVisitantes;
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
    private FloatingActionButton btn_novo_jogador_mandante;
    private FloatingActionButton btn_novo_jogador_visitante;

    private Drawable ic_gol_pro;
    private Drawable ic_gol_contra;
    private Drawable ic_falta;
    private Drawable ic_falta_desabled;
    private Drawable ic_cartao_vermelho;
    private Drawable ic_cartao_amarelo;
    private Drawable ic_del_default;
    private Drawable ic_del_desabled;
    private Drawable ic_gol_desabled;
    private Drawable ic_cartao_desabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partida);
        relogio_parado = true;
        relogio = findViewById(R.id.cronometro);

        recyclerViewJogadoresMandantes = findViewById(R.id.recyclerview_lista_jogadores_mandantes);
        recyclerViewJogadoresVisitantes = findViewById(R.id.recyclerview_lista_jogadores_visitates);

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
        btn_novo_jogador_mandante = findViewById(R.id.btn_novo_jogador_mandante);
        btn_novo_jogador_visitante = findViewById(R.id.btn_novo_jogador_visitante);

        ic_gol_pro = ContextCompat.getDrawable(this, R.drawable.acao_add_ponto_pro);
        ic_gol_contra = ContextCompat.getDrawable(this, R.drawable.acao_add_ponto_contra);
        ic_falta = ContextCompat.getDrawable(this, R.drawable.acao_add_falta);
        ic_cartao_vermelho = ContextCompat.getDrawable(this, R.drawable.acao_add_card_vermelho);
        ic_cartao_amarelo = ContextCompat.getDrawable(this, R.drawable.acao_add_card_amarelo);
        ic_cartao_desabled = ContextCompat.getDrawable(this, R.drawable.acao_add_card_desabled);
        ic_gol_desabled = ContextCompat.getDrawable(this, R.drawable.acao_add_ponto_desabled);
        ic_falta_desabled = ContextCompat.getDrawable(this, R.drawable.acao_add_falta_desabled);
        ic_del_default = ContextCompat.getDrawable(this, R.drawable.acao_del_default);
        ic_del_desabled = ContextCompat.getDrawable(this, R.drawable.acao_del_default_desabled);

        metodoRaiz();

        btn_novo_jogador_mandante.setOnClickListener(v -> montarAlertaAdicionarJogador(true));

        btn_novo_jogador_visitante.setOnClickListener(v -> montarAlertaAdicionarJogador(false));

        txv_partida_nome_mandante.setOnClickListener(v -> montarAlertaEditarEquipe(true));

        txv_partida_nome_visitante.setOnClickListener(v -> montarAlertaEditarEquipe(false));

        btn_finalizar_partida.setOnClickListener(v -> montarAlertaFinalizarPartida());

        findViewById(R.id.btn_cron_pause).setOnLongClickListener(v -> {
            if(isSimulacao() && relogio_parado){
                santuarioOlimpia.setSimulacaoDePelada(null);
                desativarFinalizarPartida();
                CarrierSemiActivity.persistirSantuario(PartidaActivity.this, santuarioOlimpia);
                metodoRaiz();
            }
            return false;
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!santuarioOlimpia.estaAtualizado()){
            CarrierSemiActivity.persistirSantuario(this, santuarioOlimpia);
            santuarioOlimpia.atualizar(false);
        }
        esconderAlerta();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!relogio_parado) {
            relogio.stop();
            deslocamento = SystemClock.elapsedRealtime() - relogio.getBase();
            partida.setTempo(deslocamento);
            CarrierSemiActivity.persistirSantuario(PartidaActivity.this, santuarioOlimpia);
        }
    }

    private boolean isSimulacao() {
        return partida.getId() == PADRAO_PARTIDA_SIMULACAO;
    }

    private boolean haEquipeVazia() {
        return mandante.getJogadores().isEmpty() || visitante.getJogadores().isEmpty();
    }

    private void metodoRaiz() {
        Intent intent = getIntent();
        String partidaIndice = intent.getStringExtra("partida");
        santuarioOlimpia = CarrierSemiActivity.carregarSantuario(PartidaActivity.this);
        // A partida é uma simulação se o índice vindo da Activity anterior for nulo
        if (partidaIndice == null) {
            torneio = santuarioOlimpia.getSimulacaoDePelada();
            btn_finalizar_partida.setText(R.string.encerrar_pelada);
            if (torneio == null){
                torneio = new Torneio(990000, getResources().getString(R.string.novo_torneio), "");
                torneio.addEquipe(new Equipe(torneio.pegarIdParaNovaEquipe(),
                        getResources().getString(R.string.equipe_nome_padrao_mandante),
                        Equipe.formatarSigla(getString(R.string.equipe_nome_padrao_mandante))));
                torneio.addEquipe(new Equipe(torneio.pegarIdParaNovaEquipe(),
                        getResources().getString(R.string.equipe_nome_padrao_visitante),
                        Equipe.formatarSigla(getString(R.string.equipe_nome_padrao_visitante))));
                String nome_padrao = getResources().getStringArray(R.array.partida_nomes)[PADRAO_PARTIDA_SIMULACAO];
                partida = new Partida(PADRAO_PARTIDA_SIMULACAO, nome_padrao);
                partida.setMandante(torneio.getEquipes().get(0).getId());
                partida.setVisitante(torneio.getEquipes().get(1).getId());
                torneio.buscarTabela().addPartida(partida.getId(), partida);
                santuarioOlimpia.setSimulacaoDePelada(torneio);
                persistirDados();
            } else {
                btn_finalizar_partida.setText(R.string.encerrar_pelada);
                partida = torneio.buscarTabela().buscarPartida(PADRAO_PARTIDA_SIMULACAO);
            }
        } else {
            torneio = santuarioOlimpia.getTorneio(Olimpia.extrairUuidTorneioDeIndices(partidaIndice));
            if(torneio==null) finish();
            partida = torneio.buscarTabela().buscarPartida((Olimpia.extrairIdInteiroDeIndices(partidaIndice) - torneio.getId()));
        }
        mandante = torneio.buscarEquipe(partida.getMandante());
        visitante = torneio.buscarEquipe(partida.getVisitante());
        povoarRecycleView();
        deslocamento = partida.getTempo();
        relogio.setBase(SystemClock.elapsedRealtime() - deslocamento);
        if(partida.estaEncerrada()){
            btn_finalizar_partida.setText(R.string.btn_partida_encerrada);
        }
    }

    private void atualizarNomesEquipes(){
        txv_partida_nome_mandante.setText(mandante.getNome());
        txv_partida_nome_visitante.setText(visitante.getNome());
        txv_partida_sigla_mandante.setText(mandante.getSigla());
        txv_partida_sigla_visitante.setText(visitante.getSigla());
    }

    private void atualizarCampos() {
        txv_partida_nome.setText(partida.getNome());
        atualizarNomesEquipes();
        atualizarPlacarEletronico();
    }

    private void atualizarPlacarEletronico() {
        ArrayList<List> acoesGerais = partida.buscarScoreGeral();
        atualizarAcoesDeJogadores(acoesGerais);
        HashMap<String, Integer> placar = (HashMap) acoesGerais.get(2);
        txv_partida_score_ponto_mandante.setText(String.valueOf(
                placar.getOrDefault("Mand_"+Score.TIPO_PONTO, 0) + placar.getOrDefault("Vist_"+Score.TIPO_AUTO_PONTO, 0)
            ));
        txv_partida_score_ponto_visitante.setText(String.valueOf(
                placar.getOrDefault("Vist_"+Score.TIPO_PONTO, 0) + placar.getOrDefault("Mand_"+Score.TIPO_AUTO_PONTO, 0)
        ));
        txv_partida_score_falta_mandante.setText(String.valueOf(
                placar.getOrDefault("Mand_"+Score.TIPO_FALTA_INDIVIDUAL, 0)
            ));
        txv_partida_score_falta_visitante.setText(String.valueOf(
                placar.getOrDefault("Vist_"+Score.TIPO_FALTA_INDIVIDUAL, 0)
        ));
    }

    private void atualizarAcoesDeJogadores(ArrayList<List> acoesGerais) {
        acoesMandantes = (ArrayList<Score>) acoesGerais.get(0);
        acoesVisitantes = (ArrayList<Score>) acoesGerais.get(1);
        listarJogadores(jam);
        listarJogadores(jav);
    }

    private void atualizarCamposTime(boolean isMandante) {
        /*if(isMandante){
            atualizarCamposMandante(partida.getScoreGeral());
        } else {
            atualizarCamposVisitante(partida.getScoreGeral());
        }*/

    }

    private void povoarRecycleView(){
        recyclerViewJogadoresMandantes.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewJogadoresVisitantes.setLayoutManager(new LinearLayoutManager(this));
        acoesMandantes = new ArrayList<>();
        acoesVisitantes = new ArrayList<>();
        jam = new JogadoresAdapter(PartidaActivity.this, mandante.getJogadores(), acoesMandantes);
        jav = new JogadoresAdapter(PartidaActivity.this, visitante.getJogadores(), acoesVisitantes);
        recyclerViewJogadoresMandantes.setAdapter(jam);
        recyclerViewJogadoresVisitantes.setAdapter(jav);
        construirListenersAdapterJogadores();
        atualizarCampos();
        if(haEquipeVazia()){
            montarAlertaEquipeImcompleta();
        }
    }

    private void construirListenersAdapterJogadores() {
        jam.setOnClickListener(v -> {
            Olimpia.printteste(this, "clique curto no jogador mandante");
            //montarAlertaAcaoPartida(true, jam.getItem(position), jam.getAcoesIndividuais(position));
            /*int position = recyclerViewJogadoresDeEquipe.getChildAdapterPosition(v);
            montarAlertaNovoOuEditarJogador(listaJogadores.get(position), position);*/
        });
        jam.setOnLongClickListener(v -> {
            Olimpia.printteste(this, "clique longo no jogador mandante");
            //montarAlertaInfromacoesIndividuais(true, jam.getItem(position), jam.getAcoesIndividuais(position));
//            montarAlertaExcluirJogador(recyclerViewJogadoresDeEquipe.getChildAdapterPosition(v));
            return true;
        });
        jav.setOnClickListener(v -> {
            Olimpia.printteste(this, "clique curto no jogador visitatne");
            //montarAlertaAcaoPartida(false, jav.getItem(position), jav.getAcoesIndividuais(position));
            /*int position = recyclerViewJogadoresDeEquipe.getChildAdapterPosition(v);
            montarAlertaNovoOuEditarJogador(listaJogadores.get(position), position);*/
        });
        jav.setOnLongClickListener(v -> {
            Olimpia.printteste(this, "clique longo no jogador visitante");
            //montarAlertaInfromacoesIndividuais(false, jav.getItem(position), jav.getAcoesIndividuais(position));
//            montarAlertaExcluirJogador(recyclerViewJogadoresDeEquipe.getChildAdapterPosition(v));
            return true;
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void listarJogadores(@NonNull JogadoresAdapter ja) {
        ja.notifyDataSetChanged();
    }

    private void montarAlertaEquipeImcompleta() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.alerta_default, null);

        Button btn_confirmar = view.findViewById(R.id.btn_confirmar_default);
        Button btn_cancelar = view.findViewById(R.id.btn_cancelar_default);
        TextView msg = view.findViewById(R.id.msg_alerta_default);
        btn_cancelar.setVisibility(View.VISIBLE);
        msg.setVisibility(View.VISIBLE);

        if(isSimulacao()) {
            btn_confirmar.setVisibility(View.VISIBLE);
            btn_confirmar.setText(R.string.btn_individual);
            btn_cancelar.setText(R.string.btn_aleatorio);
            msg.setText(R.string.lbl_msg_partida_equipe_vazia_pelada);
            btn_confirmar.setOnClickListener(arg0 -> {
                esconderAlerta();
                montarAlertaAbrirSorteioJogadores();
            });

            btn_cancelar.setOnClickListener(arg0 -> {
                preencherEquipe(mandante);
                preencherEquipe(visitante);
                atualizarCampos();
                esconderAlerta();
            });
            builder.setCancelable(false);
        } else {
            msg.setText(R.string.lbl_msg_partida_equipe_vazia_partida);
            btn_cancelar.setText(R.string.btn_confirmar);

            btn_cancelar.setOnClickListener(arg0 -> {
                atualizarCampos();
                esconderAlerta();
            });
        }

        builder.setView(view);
        builder.setTitle(R.string.titulo_alerta_equipe_vazia);
        mostrarAlerta(builder);
    }

    private void montarAlertaAbrirSorteioJogadores() {
        ArrayList<String> nomes_jogadores = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.alerta_lista_jogadores_sorteio, null);
        ArrayAdapter<String> adapterJogadores = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nomes_jogadores);
        ListView lv_jogadores = view.findViewById(R.id.ltv_sorteio_jogadores);
        lv_jogadores.setAdapter(adapterJogadores);
        TextView txv_nome_jogador = view.findViewById(R.id.txv_nome_sorteio);
        txv_nome_jogador.requestFocus();

        view.findViewById(R.id.btn_add_nome_sorteio).setOnClickListener(arg0 -> {
            String nome = txv_nome_jogador.getText().toString().trim();
            if(!nome.isEmpty() && nomes_jogadores.size()<=50) {
                nomes_jogadores.add(0, (nomes_jogadores.size()+1) + " - " + nome);
                txv_nome_jogador.setText("");
                adapterJogadores.notifyDataSetChanged();
            }
        });

        view.findViewById(R.id.btn_rmv_nome_sorteio).setOnClickListener(arg0 -> {
            if(!nomes_jogadores.isEmpty()){
                nomes_jogadores.remove(0);
                adapterJogadores.notifyDataSetChanged();
            }
        });

        view.findViewById(R.id.btn_sortear).setOnClickListener(arg0 -> {
            if (nomes_jogadores.size()>=2) {
                int j = 2;
                int k = 2;
                mandante.getJogadores().clear();
                visitante.getJogadores().clear();
                Collections.shuffle(nomes_jogadores);
                for (int i = 0; i < nomes_jogadores.size(); i++) {
                    String nome = nomes_jogadores.get(i).split(" - ")[1];
                    if (i % 2 == 0) {
                        mandante.addJogador(new Jogador(mandante.bucarIdParaNovoJogador(), nome,4, j++));
                    } else {
                        visitante.addJogador(new Jogador(visitante.bucarIdParaNovoJogador(), nome,4, k++ ));
                    }
                }
                persistirDados();
                esconderAlerta();
                montarAlertaSorteioJogadores();
            } else {
                txv_nome_jogador.setError(getString(R.string.msg_jogadores_insuficientes));
            }
        });

        builder.setView(view);
        builder.setTitle(R.string.titulo_alerta_sorteio_pelada);
        builder.setCancelable(false);
        mostrarAlerta(builder);
    }

    private void montarAlertaSorteioJogadores() {
        final Handler handler = new Handler();
        final Runnable runnable = this::esconderAlerta;

        handler.postDelayed(runnable, 3000);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.alerta_default, null);
        GifImageView img = view.findViewById(R.id.img_alerta_default);
        img.setVisibility(View.VISIBLE);

        builder.setOnDismissListener(dialog -> {
            handler.removeCallbacks(runnable);
            atualizarCampos();
        });

        builder.setView(view);
        builder.setCustomTitle(null);
        builder.setCancelable(false);
        mostrarAlerta(builder, R.color.cor_transparente);
    }

    private void montarAlertaFinalizarPartida() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.alerta_default, null);

        Button btn_confirmar = view.findViewById(R.id.btn_confirmar_default);
        Button btn_cancelar = view.findViewById(R.id.btn_cancelar_default);
        TextView msg = view.findViewById(R.id.msg_alerta_default);
        btn_confirmar.setVisibility(View.VISIBLE);
        btn_cancelar.setVisibility(View.VISIBLE);
        msg.setVisibility(View.VISIBLE);

        msg.setText(isSimulacao() ? R.string.lbl_msg_finalizar_pelada: R.string.lbl_msg_finalizar_partida);

        btn_confirmar.setOnClickListener(arg0 -> {
            efeitos_sonoros = MediaPlayer.create(PartidaActivity.this, R.raw.aviso_fim_jogo);
            play_efeito_sonoro();
            esconderAlerta();
            finalizarPartida();
        });

        btn_cancelar.setOnClickListener(arg0 -> esconderAlerta());

        builder.setView(view);
        builder.setTitle(isSimulacao() ? R.string.titulo_alerta_confir_finalizar_pelada : R.string.titulo_alerta_confir_finalizar_partida);
        mostrarAlerta(builder);
    }

    private void montarAlertaPremiacao() {
        Equipe campeao = mandante.getId() == partida.getCampeaoId() ? mandante : visitante;
        btn_finalizar_partida.setText(R.string.btn_partida_encerrada);
        desativarFinalizarPartida();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.alerta_default, null);

        Button btn_confirmar = view.findViewById(R.id.btn_confirmar_default);
        TextView msg = view.findViewById(R.id.msg_alerta_default);
        GifImageView img = view.findViewById(R.id.img_alerta_default);


        btn_confirmar.setVisibility(View.VISIBLE);
        msg.setVisibility(View.VISIBLE);
        img.setVisibility(View.VISIBLE);

        SpannableString textoNegrito = new SpannableString(campeao.getNome() + "\n\n" +
                getString(R.string.msg_alerta_saldar_campeao) + "\n");
        textoNegrito.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, campeao.getNome().length(), 0);
        msg.setText(textoNegrito);
        img.setImageResource(R.drawable.fogos);

        btn_confirmar.setOnClickListener(arg0 -> esconderAlerta());

        builder.setView(view);
        builder.setTitle(R.string.titulo_alerta_partida_campeao);
        mostrarAlerta(builder);
    }

    private void montarAlertaPerguntarDesempate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.alerta_default, null);

        Button btn_confirmar = view.findViewById(R.id.btn_confirmar_default);
        Button btn_cancelar = view.findViewById(R.id.btn_cancelar_default);
        TextView msg = view.findViewById(R.id.msg_alerta_default);
        btn_confirmar.setVisibility(View.VISIBLE);
        btn_cancelar.setVisibility(View.VISIBLE);
        msg.setVisibility(View.VISIBLE);
        msg.setText(getString(R.string.lbl_msg_partida_empatada) + "\n\n" + getString(R.string.lbl_msg_inicio_desempate));

        btn_confirmar.setOnClickListener(arg0 -> montarAlertaAbrirDesempate());

        btn_cancelar.setOnClickListener(arg0 -> {
            CarrierSemiActivity.exemplo(this, getString(R.string.msg_alerta_desempate_campeao_por_sorteio));
            partida.setCampeaoId(new Random().nextInt(24)%2 == 0 ? mandante.getId() : visitante.getId());
            esconderAlerta();
            montarAlertaPremiacao();
        });

        builder.setView(view);
        builder.setTitle(R.string.titulo_alerta_confir_finalizar_pelada);
        mostrarAlerta(builder);
    }

    private void montarAlertaAbrirDesempate() {
        esconderAlerta();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.alerta_penaltis, null);

        TextView txv_sigla_mandante = view.findViewById(R.id.sigla_desempate_mandante);
        TextView txv_sigla_visitante = view.findViewById(R.id.sigla_desempate_visitante);
        TextView txv_msg = view.findViewById(R.id.msg_alerta_desempate);

        LinearLayout lista_cobranca_mandante = view.findViewById(R.id.lista_desempate_cobrancas_mandante);
        LinearLayout lista_cobranca_visitante = view.findViewById(R.id.lista_desempate_cobrancas_visitante);

        TextView txv_placar_mandante = view.findViewById(R.id.placar_desempate_mandante);
        TextView txv_placar_visitante = view.findViewById(R.id.placar_desempate_visitante);

        Button btn_mandante_primeiro = view.findViewById(R.id.btn_desempate_mandante);
        Button btn_visitante_primeiro = view.findViewById(R.id.btn_desempate_visitante);
        Button btn_min_3 = view.findViewById(R.id.btn_desempate_3);
        Button btn_min_5 = view.findViewById(R.id.btn_desempate_5);
        Button btn_erro = view.findViewById(R.id.btn_desempate_erro);
        Button btn_acerto = view.findViewById(R.id.btn_desempate_acerto);

        txv_sigla_mandante.setText(mandante.getSigla());
        txv_sigla_visitante.setText(visitante.getSigla());

        AtomicInteger min_cobranca = new AtomicInteger();
        int corCobrador = getColor(R.color.midle);

        ArrayList<Score> score_parcial = new ArrayList<>();
        ArrayList<String> score_parcial_mandante = new ArrayList(Arrays.asList("n", "n", "n", "n", "n"));
        ArrayList<String> score_parcial_visitante = new ArrayList(Arrays.asList("n", "n", "n", "n", "n"));

        btn_mandante_primeiro.setOnClickListener(arg0 -> {
            cobrador = mandante;
            txv_sigla_mandante.setTextColor(corCobrador);
            txv_msg.setText(R.string.msg_alerta_desempate_quantidade);
            btn_mandante_primeiro.setVisibility(View.GONE);
            btn_visitante_primeiro.setVisibility(View.GONE);
            btn_min_3.setVisibility(View.VISIBLE);
            btn_min_5.setVisibility(View.VISIBLE);
        });

        btn_visitante_primeiro.setOnClickListener(arg0 -> {
            cobrador=visitante;
            txv_sigla_visitante.setTextColor(corCobrador);
            txv_msg.setText(R.string.msg_alerta_desempate_quantidade);
            btn_mandante_primeiro.setVisibility(View.GONE);
            btn_visitante_primeiro.setVisibility(View.GONE);
            btn_min_3.setVisibility(View.VISIBLE);
            btn_min_5.setVisibility(View.VISIBLE);
        });

        btn_min_3.setOnClickListener(arg0 -> {
            min_cobranca.set(3);
            score_parcial_mandante.remove(0);
            score_parcial_mandante.remove(0);
            score_parcial_visitante.remove(0);
            score_parcial_visitante.remove(0);
            mudarPlacarDesempate(lista_cobranca_mandante, score_parcial_mandante);
            mudarPlacarDesempate(lista_cobranca_visitante, score_parcial_visitante);
            txv_msg.setText(R.string.msg_alerta_desempate_cobranças);
            btn_min_3.setVisibility(View.GONE);
            btn_min_5.setVisibility(View.GONE);
            btn_erro.setVisibility(View.VISIBLE);
            btn_acerto.setVisibility(View.VISIBLE);
        });

        btn_min_5.setOnClickListener(arg0 -> {
            min_cobranca.set(5);
            txv_msg.setText(R.string.msg_alerta_desempate_cobranças);
            btn_min_3.setVisibility(View.GONE);
            btn_min_5.setVisibility(View.GONE);
            btn_erro.setVisibility(View.VISIBLE);
            btn_acerto.setVisibility(View.VISIBLE);
        });

        btn_acerto.setOnClickListener(arg0 -> {
            score_parcial.add(new Score(partida.getId(), cobrador.getId(), Score.TIPO_PONTO_DESEMPATE));
            aplicarPontoDesempate(score_parcial,
                    score_parcial_mandante,
                    score_parcial_visitante,
                    lista_cobranca_mandante,
                    lista_cobranca_visitante,
                    txv_sigla_mandante,
                    txv_sigla_visitante,
                    "a", min_cobranca.get()
                );
            txv_placar_mandante.setText(String.valueOf(score_parcial.stream().filter(s -> s.getIdJogador() == mandante.getId()).count()));
            txv_placar_visitante.setText(String.valueOf(score_parcial.stream().filter(s -> s.getIdJogador() == visitante.getId()).count()));
        });

        btn_erro.setOnClickListener(arg0 -> aplicarPontoDesempate(score_parcial,
                score_parcial_mandante,
                score_parcial_visitante,
                lista_cobranca_mandante,
                lista_cobranca_visitante,
                txv_sigla_mandante,
                txv_sigla_visitante,
                "e", min_cobranca.get()
        ));

        builder.setView(view);
        builder.setTitle(R.string.titulo_alerta_partida_desempate);
        builder.setCancelable(false);
        mostrarAlerta(builder);
    }

    private void aplicarPontoDesempate(ArrayList<Score> score_parcial,
                                       ArrayList<String> score_parcial_mandante,
                                       ArrayList<String> score_parcial_visitante,
                                       LinearLayout lista_cobranca_mandante,
                                       LinearLayout lista_cobranca_visitante,
                                       TextView txv_sigla_mandante,
                                       TextView txv_sigla_visitante,
                                       String str, int min_cobranca){
        int corCobrador = getColor(R.color.midle);
        int corDefensor = getColor(R.color.text_default);
        if(cobrador.getId() == mandante.getId()){
            score_parcial_mandante.set(score_parcial_mandante.indexOf("n"), str);
            txv_sigla_visitante.setTextColor(corCobrador);
            txv_sigla_mandante.setTextColor(corDefensor);
            mudarPlacarDesempate(lista_cobranca_mandante, score_parcial_mandante);
            cobrador=visitante;
        } else {
            score_parcial_visitante.set(score_parcial_visitante.indexOf("n"), str);
            txv_sigla_mandante.setTextColor(corCobrador);
            txv_sigla_visitante.setTextColor(corDefensor);
            mudarPlacarDesempate(lista_cobranca_visitante, score_parcial_visitante);
            cobrador=mandante;
        }
        int im = score_parcial_mandante.indexOf("n");
        int iv = score_parcial_visitante.indexOf("n");
        long golsM = score_parcial.stream().filter(s -> s.getIdJogador() == mandante.getId()).count();
        long golsV = score_parcial.stream().filter(s -> s.getIdJogador() == visitante.getId()).count();
        if(im<0 || iv<0){
            if(im == iv){
                if (golsM==golsV){
                    score_parcial_mandante.add("n");
                    score_parcial_visitante.add("n");
                    if(score_parcial_mandante.size() > min_cobranca+1){
                        score_parcial_mandante.remove(0);
                        score_parcial_visitante.remove(0);
                    }
                    mudarPlacarDesempate(lista_cobranca_mandante, score_parcial_mandante);
                    mudarPlacarDesempate(lista_cobranca_visitante, score_parcial_visitante);
                } else {
                    if(golsM>golsV) {
                        partida.setCampeaoId(mandante.getId());
                    } else {
                        partida.setCampeaoId(visitante.getId());
                    }
                }
            } else if(im > iv){
                if(golsV - golsM > 1){
                    partida.setCampeaoId(visitante.getId());
                } else if(golsV<golsM){
                    partida.setCampeaoId(mandante.getId());
                }
            } else {
                if(golsM-golsV> 1){
                    partida.setCampeaoId(mandante.getId());
                } else if(golsM<golsV){
                    partida.setCampeaoId(visitante.getId());
                }
            }
        } else if(im>min_cobranca/2 && iv>min_cobranca/2){
            if(golsM - golsV > min_cobranca - iv){
                partida.setCampeaoId(mandante.getId());
            } else if (golsV - golsM > min_cobranca - im){
                partida.setCampeaoId(visitante.getId());
            }
        }
        if(partida.estaEncerrada()){
//            CarrierSemiActivity.exemplo(this, "Campeão: " + partida.getCampeaoId());
//            partida.setCampeaoId(0);
//            esconderAlerta();
            montarAlertaPremiacao();
        }
    }

    private void mudarPlacarDesempate(LinearLayout layout, ArrayList<String> placar){
        for(int i=0; i<layout.getChildCount(); i++){
            ImageView img = (ImageView) layout.getChildAt(i);
            if(placar.size()>i){
                img.setVisibility(View.VISIBLE);
                switch (placar.get(i)){
                    case "a":
                        img.setImageResource(R.drawable.acao_add_ponto_pro);
                        break;
                    case "e":
                        img.setImageResource(R.drawable.acao_add_ponto_contra);
                        break;
                    default:
                        img.setImageResource(R.drawable.acao_add_ponto_desabled);
                        break;
                }
            } else {
                img.setVisibility(View.GONE);
            }
        }
    }

    private void montarAlertaAcaoPartida(boolean isMandante, Jogador j, HashMap<Integer, Integer> acoes_jogador) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.alerta_acao_placar, null);

        TextView acao_jogador_number = view.findViewById(R.id.acao_jogador_number);
        TextView acao_jogador_posicao = view.findViewById(R.id.acao_jogador_posicao);
        TextView acao_jogador_nome = view.findViewById(R.id.acao_jogador_nome);
        LinearLayout acao_jogador_segundo_amarelo = view.findViewById(R.id.acao_jogador_segundo_amarelo);

        Button btn_del_gol_pro = view.findViewById(R.id.btn_del_acao_gol_pro);
        Button btn_del_gol_contra = view.findViewById(R.id.btn_del_acao_gol_contra);
        Button btn_del_falta = view.findViewById(R.id.btn_del_acao_falta);
        Button btn_del_vermelho = view.findViewById(R.id.btn_del_acao_cartao_vermelho);
        Button btn_del_amarelo = view.findViewById(R.id.btn_del_acao_cartao_amarelo);
        Button btn_add_gol_pro = view.findViewById(R.id.btn_add_acao_gol_pro);
        Button btn_add_gol_contra = view.findViewById(R.id.btn_add_acao_gol_contra);
        Button btn_add_falta = view.findViewById(R.id.btn_add_acao_falta);
        Button btn_add_vermelho = view.findViewById(R.id.btn_add_acao_cartao_vermelho);
        Button btn_add_amarelo = view.findViewById(R.id.btn_add_acao_cartao_amarelo);

        acao_jogador_number.setText(String.valueOf(j.getNumero()));
        acao_jogador_posicao.setText(getResources().getStringArray(R.array.posicoes_jogador)[j.getPosicao()].substring(0, 3));
        acao_jogador_nome.setText(j.getNome());

        if( acoes_jogador.getOrDefault(Score.TIPO_AMARELO, 0) >= 2
           || acoes_jogador.containsKey(Score.TIPO_VERMELHO)){
            //Jogador expulso: NÃO recebe ponto, falta ou novo cartão
            btn_add_gol_pro.setBackground(ic_gol_desabled);
            btn_add_gol_pro.setEnabled(false);
            btn_add_gol_contra.setBackground(ic_gol_desabled);
            btn_add_gol_contra.setEnabled(false);
            btn_add_falta.setBackground(ic_falta_desabled);
            btn_add_falta.setEnabled(false);
            btn_add_vermelho.setBackground(ic_cartao_desabled);
            btn_add_vermelho.setEnabled(false);
            btn_add_amarelo.setBackground(ic_cartao_desabled);
            btn_add_amarelo.setEnabled(false);
            btn_del_gol_pro.setBackground(ic_gol_desabled);
            btn_del_gol_pro.setForeground(ic_del_desabled);
            btn_del_gol_pro.setEnabled(false);
            btn_del_gol_contra.setBackground(ic_gol_desabled);
            btn_del_gol_contra.setForeground(ic_del_desabled);
            btn_del_gol_contra.setEnabled(false);
            btn_del_falta.setBackground(ic_falta_desabled);
            btn_del_falta.setForeground(ic_del_desabled);
            btn_del_falta.setEnabled(false);
            if(acoes_jogador.containsKey(Score.TIPO_VERMELHO)) {
                btn_del_vermelho.setBackground(ic_cartao_vermelho);
                btn_del_vermelho.setForeground(ic_del_default);
                btn_del_vermelho.setEnabled(true);
                btn_del_amarelo.setBackground(ic_cartao_desabled);
                btn_del_amarelo.setForeground(ic_del_desabled);
                btn_del_amarelo.setEnabled(false);
            } else {
                btn_del_amarelo.setBackground(ic_cartao_amarelo);
                btn_del_amarelo.setForeground(ic_del_default);
                btn_del_amarelo.setEnabled(true);
                btn_del_vermelho.setBackground(ic_cartao_desabled);
                btn_del_vermelho.setForeground(ic_del_desabled);
                btn_del_vermelho.setEnabled(false);
            }
        } else {
            //Linha dos pontos a favor
            if(partida.estaEncerrada()){
                btn_del_gol_pro.setBackground(ic_gol_desabled);
                btn_del_gol_pro.setForeground(ic_del_desabled);
                btn_del_gol_pro.setEnabled(false);
                btn_add_gol_pro.setBackground(ic_gol_desabled);
                btn_add_gol_pro.setEnabled(false);
                btn_del_gol_contra.setBackground(ic_gol_desabled);
                btn_del_gol_contra.setForeground(ic_del_desabled);
                btn_del_gol_contra.setEnabled(false);
                btn_add_gol_contra.setBackground(ic_gol_desabled);
                btn_add_gol_contra.setEnabled(false);
            } else {
                btn_add_gol_pro.setBackground(ic_gol_pro);
                btn_add_gol_pro.setEnabled(true);
                btn_add_gol_contra.setBackground(ic_gol_contra);
                btn_add_gol_contra.setEnabled(true);
                if(acoes_jogador.containsKey(Score.TIPO_PONTO)) {
                    btn_del_gol_pro.setBackground(ic_gol_pro);
                    btn_del_gol_pro.setForeground(ic_del_default);
                    btn_del_gol_pro.setEnabled(true);
                } else {
                    btn_del_gol_pro.setBackground(ic_gol_desabled);
                    btn_del_gol_pro.setForeground(ic_del_desabled);
                    btn_del_gol_pro.setEnabled(false);
                }
                if(acoes_jogador.containsKey(Score.TIPO_AUTO_PONTO)) {
                    btn_del_gol_contra.setBackground(ic_gol_contra);
                    btn_del_gol_contra.setForeground(ic_del_default);
                    btn_del_gol_contra.setEnabled(true);
                } else {
                    btn_del_gol_contra.setBackground(ic_gol_desabled);
                    btn_del_gol_contra.setForeground(ic_del_desabled);
                    btn_del_gol_contra.setEnabled(false);
                }
            }
            //Linha das faltas
            if(acoes_jogador.containsKey(Score.TIPO_FALTA_INDIVIDUAL)){
                btn_del_falta.setBackground(ic_falta);
                btn_del_falta.setForeground(ic_del_default);
                btn_del_falta.setEnabled(true);
            } else {
                btn_del_falta.setBackground(ic_falta_desabled);
                btn_del_falta.setForeground(ic_del_desabled);
                btn_del_falta.setEnabled(false);
            }
            btn_add_falta.setBackground(ic_falta);
            btn_add_falta.setEnabled(true);
            //Linha do cartão vermelho
            btn_del_vermelho.setBackground(ic_cartao_desabled);
            btn_del_vermelho.setForeground(ic_del_desabled);
            btn_del_vermelho.setEnabled(false);
            btn_add_vermelho.setBackground(ic_cartao_vermelho);
            btn_add_vermelho.setEnabled(true);
            //Linha do cartão amarelo
            btn_add_amarelo.setEnabled(true);
            btn_add_amarelo.setBackground(ic_cartao_amarelo);
            if(acoes_jogador.containsKey(Score.TIPO_AMARELO)){
                btn_del_amarelo.setBackground(ic_cartao_amarelo);
                btn_del_amarelo.setForeground(ic_del_default);
                btn_del_amarelo.setEnabled(true);
                acao_jogador_segundo_amarelo.setVisibility(View.VISIBLE);
            } else {
                btn_del_amarelo.setBackground(ic_cartao_desabled);
                btn_del_amarelo.setForeground(ic_del_desabled);
                btn_del_amarelo.setEnabled(false);
            }
        }

        btn_add_gol_pro.setOnClickListener(arg0 -> {
            efeitos_sonoros = MediaPlayer.create(PartidaActivity.this, R.raw.aviso_gol);
            play_efeito_sonoro();
            adicionarAcaoJogador(isMandante, new Score(partida.getId(), j.getId(), Score.TIPO_PONTO));
            esconderAlerta();
        });

        btn_add_gol_contra.setOnClickListener(arg0 -> {
            efeitos_sonoros = MediaPlayer.create(PartidaActivity.this, R.raw.aviso_gol);
            play_efeito_sonoro();
            adicionarAcaoJogador(isMandante, new Score(partida.getId(), j.getId(), Score.TIPO_AUTO_PONTO));
            esconderAlerta();
        });

        btn_add_falta.setOnClickListener(arg0 -> {
            efeitos_sonoros = MediaPlayer.create(PartidaActivity.this, R.raw.aviso_falta);
            play_efeito_sonoro();
            adicionarAcaoJogador(isMandante, new Score(partida.getId(), j.getId(), Score.TIPO_FALTA_INDIVIDUAL));
            esconderAlerta();
        });

        btn_add_vermelho.setOnClickListener(arg0 -> {
            adicionarAcaoJogador(isMandante, new Score(partida.getId(), j.getId(), Score.TIPO_VERMELHO));
            esconderAlerta();
        });

        btn_add_amarelo.setOnClickListener(arg0 -> {
            adicionarAcaoJogador(isMandante, new Score(partida.getId(), j.getId(), Score.TIPO_AMARELO));
            esconderAlerta();
        });

        btn_del_gol_pro.setOnClickListener(arg0 -> {
            apagarAcaoJogador(isMandante, new Score(partida.getId(), j.getId(), Score.TIPO_PONTO));
            esconderAlerta();
        });

        btn_del_gol_contra.setOnClickListener(arg0 -> {
            apagarAcaoJogador(isMandante, new Score(partida.getId(), j.getId(), Score.TIPO_AUTO_PONTO));
            esconderAlerta();
        });

        btn_del_falta.setOnClickListener(arg0 -> {
            apagarAcaoJogador(isMandante, new Score(partida.getId(), j.getId(), Score.TIPO_FALTA_INDIVIDUAL));
            esconderAlerta();
        });

        btn_del_vermelho.setOnClickListener(arg0 -> {
            apagarAcaoJogador(isMandante, new Score(partida.getId(), j.getId(), Score.TIPO_VERMELHO));
            esconderAlerta();
        });

        btn_del_amarelo.setOnClickListener(arg0 -> {
            apagarAcaoJogador(isMandante, new Score(partida.getId(), j.getId(), Score.TIPO_AMARELO));
            esconderAlerta();
        });

        view.findViewById(R.id.btn_cancelar_acao).setOnClickListener(arg0 -> alertaDialog.dismiss());

        builder.setView(view);
        builder.setTitle(R.string.titulo_alerta_partida_acao_jogador);
        mostrarAlerta(builder);
    }

    private void montarAlertaAdicionarJogador(boolean isMandante){
        montarAlertaAdicionarOuEditarOuInfromacoesJogador(isMandante, null, null, isMandante?mandante.getJogadores().size():visitante.getJogadores().size());
    }

    private void montarAlertaAdicionarOuEditarOuInfromacoesJogador(boolean isMandante, Jogador jogador, HashMap<Integer, Integer> acoes_jogador, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.alerta_novo_jogador, null);
        //Preenchendo atributos pessoais do jogador.
        EditText etx_nome_jogador = view.findViewById(R.id.etx_nome_novo_jogador);
        Button btn_confirma_jogador = view.findViewById(R.id.btn_confirmar_jogador);
        Button btn_deletar_jogador = view.findViewById(R.id.btn_del_jogador);
        Spinner spnr_posicao = view.findViewById(R.id.spr_pos_novo_jogador);
        Spinner spnr_numero = view.findViewById(R.id.spr_num_novo_jogador);
        spnr_posicao.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item_style, getResources().getStringArray(R.array.posicoes_jogador)));
        ArrayList<Integer> numeros;

        if (jogador == null) {
            builder.setTitle(R.string.titulo_alerta_novo_jogador);
            if(isMandante) {
                numeros = mandante.buscarNumerosLivresNoPlantel(-1);
            } else {
                numeros = visitante.buscarNumerosLivresNoPlantel(-1);
            }
            btn_confirma_jogador.setText(R.string.btn_adicionar);
        } else {
            builder.setTitle(R.string.titulo_alerta_partida_detalhes_jogador);
            etx_nome_jogador.setText(jogador.getNome());
            spnr_posicao.setSelection(jogador.getPosicao());
            if (isMandante) {
                numeros = mandante.buscarNumerosLivresNoPlantel(jogador.getNumero());
            } else {
                numeros = visitante.buscarNumerosLivresNoPlantel(jogador.getNumero());
            }
            btn_confirma_jogador.setText(R.string.btn_editar);
            if (isSimulacao()){
                if (acoes_jogador.isEmpty()) {
                    btn_deletar_jogador.setVisibility(View.VISIBLE);
                }
            } else {
                if (acoes_jogador.isEmpty() &&
                    !torneio.ParticipacaoAcoesTorneio(jogador.getId())){
                    btn_deletar_jogador.setVisibility(View.VISIBLE);
                }
            }
            //Preenchendo informações do jogador na partida.
            view.findViewById(R.id.quadro_info_acoes_jogador).setVisibility(View.VISIBLE);
            TextView txv_jogador_pontos = view.findViewById(R.id.txv_jogador_gols_pro);
            TextView txv_jogador_pontos_contra = view.findViewById(R.id.txv_jogador_gols_contra);
            TextView txv_jogador_faltas = view.findViewById(R.id.txv_jogador_faltas);
            TextView txv_jogador_divisor = view.findViewById(R.id.txv_jogador_divisor_cartoes);
            ImageView img_sem_cartao = view.findViewById(R.id.img_jogador_sem_cartao);
            ImageView img_primeiro_amarelo = view.findViewById(R.id.img_jogador_amarelo1);
            ImageView img_cartao_vermelho = view.findViewById(R.id.img_jogador_vermelho);
            LinearLayout img_segundo_amarelo = view.findViewById(R.id.img_jogador_amarelo2);

            txv_jogador_pontos.setText(String.valueOf(acoes_jogador.getOrDefault(Score.TIPO_PONTO, 0)));
            txv_jogador_pontos_contra.setText(String.valueOf(acoes_jogador.getOrDefault(Score.TIPO_AUTO_PONTO, 0)));
            txv_jogador_faltas.setText(String.valueOf(acoes_jogador.getOrDefault(Score.TIPO_FALTA_INDIVIDUAL, 0)));
            if(acoes_jogador.containsKey(Score.TIPO_VERMELHO)){
                img_cartao_vermelho.setVisibility(View.VISIBLE);
                img_segundo_amarelo.setVisibility(View.GONE);
                if(acoes_jogador.containsKey(Score.TIPO_AMARELO)){
                    img_primeiro_amarelo.setVisibility(View.VISIBLE);
                    txv_jogador_divisor.setVisibility(View.VISIBLE);
                } else {
                    img_primeiro_amarelo.setVisibility(View.GONE);
                    txv_jogador_divisor.setVisibility(View.GONE);
                }
            } else {
                txv_jogador_divisor.setVisibility(View.GONE);
                switch (acoes_jogador.getOrDefault(Score.TIPO_AMARELO, 0)){
                    case 2:
                        img_primeiro_amarelo.setVisibility(View.VISIBLE);
                        img_segundo_amarelo.setVisibility(View.VISIBLE);
                        img_cartao_vermelho.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        img_primeiro_amarelo.setVisibility(View.VISIBLE);
                        img_segundo_amarelo.setVisibility(View.GONE);
                        img_cartao_vermelho.setVisibility(View.GONE);
                        break;
                    default:
                        img_primeiro_amarelo.setVisibility(View.GONE);
                        img_segundo_amarelo.setVisibility(View.GONE);
                        img_cartao_vermelho.setVisibility(View.GONE);
                        img_sem_cartao.setVisibility(View.VISIBLE);
                        break;
                }
            }
        }
        etx_nome_jogador.requestFocus();
        spnr_numero.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item_style, numeros));
        spnr_numero.setSelection(0);

        btn_confirma_jogador.setEnabled(true);
        btn_confirma_jogador.setBackground(ContextCompat.getDrawable(this, R.drawable.button_shape_enabled));
        btn_confirma_jogador.setOnClickListener(arg0 -> {
            String nome = etx_nome_jogador.getText().toString().trim();
            if(!nome.isEmpty()) {
                int numero = Integer.parseInt(spnr_numero.getSelectedItem().toString());
                int posicao = spnr_posicao.getSelectedItemPosition();
                if(jogador==null) {
                    if (adicionarJogadorValidacao((isMandante ? mandante: visitante), nome, numero, posicao)){
                        Toast.makeText(this, R.string.jogador_adicionado, Toast.LENGTH_SHORT).show();
                        persistirDados();
                        if (isMandante) jam.notifyItemInserted(position);
                        else jav.notifyItemInserted(position);
                    } else {
                        Toast.makeText(this, R.string.jogador_erro_adicionar, Toast.LENGTH_SHORT).show();
                    }
                } else if (!jogador.getNome().equals(nome) ||
                            jogador.getNumero() != numero ||
                            jogador.getPosicao() != posicao) {
                    jogador.setNome(nome);
                    jogador.setNumero(numero);
                    jogador.setPosicao(posicao);
                    persistirDados();
                    if (isMandante) jam.notifyItemChanged(position);
                    else jav.notifyItemChanged(position);
                    Toast.makeText(this, R.string.jogador_editado, Toast.LENGTH_SHORT).show();
                }
                esconderAlerta();
            } else {
                etx_nome_jogador.setError(getString(R.string.erro_campo_texto_vazio));
            }
        });

        btn_deletar_jogador.setOnClickListener(arg0 -> {
            if(isMandante){
                if(false){ //ajustar lógica visitante.delJogador(j)
                    Toast.makeText(PartidaActivity.this, R.string.msg_alerta_sucesso_excluir_jogador, Toast.LENGTH_LONG).show();
                    atualizarCamposTime(isMandante);
                }
            } else {
                if (false) { //ajustar lógica visitante.delJogador(j)
                    Toast.makeText(PartidaActivity.this, R.string.msg_alerta_sucesso_excluir_jogador, Toast.LENGTH_LONG).show();
                    atualizarCamposTime(isMandante);
                }
            }
            esconderAlerta();
        });

        view.findViewById(R.id.btn_cancelar_jogador).setOnClickListener(arg0 -> alertaDialog.dismiss());

        builder.setView(view);
        mostrarAlerta(builder);
        if(jogador!=null)alertaDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void montarAlertaEditarEquipe(boolean isMandante) {
        Equipe equipeAtualizando = isMandante ? mandante : visitante;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.alerta_nova_equipe, null);
        EditText etx_nome_equipe = view.findViewById(R.id.etx_nome_nova_equipe);
        EditText etx_sigla_equipe = view.findViewById(R.id.etx_sigla_nova_equipe);
        Button btn_confirma_equipe = view.findViewById(R.id.btn_confirmar_nova_equipe);
        btn_confirma_equipe.setEnabled(true);
        btn_confirma_equipe.setBackground(ContextCompat.getDrawable(this, R.drawable.button_shape_enabled));
        btn_confirma_equipe.setText(R.string.btn_editar);

        etx_nome_equipe.setText(equipeAtualizando.getNome());
        etx_sigla_equipe.setText(equipeAtualizando.getSigla());
        etx_nome_equipe.requestFocus();

        //Listeners possíveis do alerta
        btn_confirma_equipe.setOnClickListener(arg0 -> {
            String nome = etx_nome_equipe.getText().toString().trim();
            String sigla = etx_sigla_equipe.getText().toString().trim().toUpperCase();
            boolean mudou = false;
            if (!nome.isEmpty() && !sigla.isEmpty()) {
                if(!equipeAtualizando.getNome().equals(nome)) {
                    equipeAtualizando.setNome(nome);
                    mudou = true;
                }
                if(sigla.length()>1 && !equipeAtualizando.getSigla().equals(sigla) && torneio.siglaDisponivel(sigla)) {
                    equipeAtualizando.setSigla(sigla);
                    mudou = true;
                }
                if (mudou){
                    persistirDados();
                    atualizarNomesEquipes();
                } else {
                    CarrierSemiActivity.exemplo(PartidaActivity.this, getString(R.string.erro_atualizar_informacoes_equipe));
                }
                esconderAlerta();
            }
        });

        view.findViewById(R.id.btn_cancelar_equipe).setOnClickListener(arg0 -> alertaDialog.dismiss());

        etx_nome_equipe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String nome = etx_nome_equipe.getText().toString().trim();
                String sigla = etx_sigla_equipe.getText().toString();
                if (nome.isEmpty()) {
                    etx_sigla_equipe.setText("");
                } else {
                    String sigla_bot = nome.contains(" ") ? Equipe.formatarSigla(nome) : nome.substring(0, 1).toUpperCase();
                    if (!sigla.trim().equals(sigla_bot)) {
                        etx_sigla_equipe.setText(sigla_bot);
                    }
                }
            }
        });

        builder.setView(view);
        builder.setTitle(R.string.titulo_alerta_nova_equipe);
        mostrarAlerta(builder);
    }

    private void mostrarAlerta(AlertDialog.Builder builder) {
        mostrarAlerta(builder, R.drawable.background_alerta);
    }

    private void mostrarAlerta(AlertDialog.Builder builder, int background) {
        esconderAlerta();
        alertaDialog = builder.create();
        alertaDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertaDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, background));
        alertaDialog.show();
    }

    private void adicionarAcaoJogador(boolean isMandante, Score s) {
//        partida.addScore(s);
        persistirDados();
        atualizarAposAcao(isMandante, s);
    }

    private void apagarAcaoJogador(boolean isMandante, Score s) {
//        partida.delScore(s);
        persistirDados();
        atualizarAposAcao(isMandante, s);
    }

    private void atualizarAposAcao(boolean isMandante, Score s){
        if(s.getTipo()==Score.TIPO_AUTO_PONTO){
            atualizarCampos();
        } else {
            atualizarCamposTime(isMandante);
        }
    }

    private void preencherEquipe(Equipe equipe) {
        equipe.getJogadores().clear();
        for (int i=1; i<=11; i++) {
            equipe.addJogador(new Jogador(equipe.bucarIdParaNovoJogador(),
                    "Jogador"+i,
                    4,
                    i
                ));
        }
    }

    private void finalizarPartida() {
        /*if (partida.setEncerrada()){
            montarAlertaPremiacao();
        } else {
            if(isSimulacao()){
                montarAlertaPerguntarDesempate();
            } else {
                montarAlertaAbrirDesempate();
            }
        }*/
    }

    private void toggleBtnsAdicionarJogadores(){
        if(btn_novo_jogador_mandante.getVisibility()==View.VISIBLE){
            btn_novo_jogador_mandante.setVisibility(View.GONE);
            btn_novo_jogador_visitante.setVisibility(View.GONE);
        } else {
            btn_novo_jogador_mandante.setVisibility(View.VISIBLE);
            btn_novo_jogador_visitante.setVisibility(View.VISIBLE);
        }
    }

    private void ativarFinalizarPartida() {
        btn_finalizar_partida.setEnabled(true);
        btn_finalizar_partida.setBackground(ContextCompat.getDrawable(this, R.drawable.button_shape_enabled));
    }

    private void desativarFinalizarPartida() {
        btn_finalizar_partida.setEnabled(false);
        btn_finalizar_partida.setBackground(ContextCompat.getDrawable(this, R.drawable.button_shape_desabled));
    }

    public void zerarCronometro(View v) {
        if(relogio_parado) {
            relogio.setBase(SystemClock.elapsedRealtime());
            deslocamento = 0;
        }
    }

    public void rodarCronometro(View v) {
        if(!partida.estaEncerrada()) {
            if (relogio_parado) {
                if (haEquipeVazia()) {
                    montarAlertaEquipeImcompleta();
                } else {
                    v.setBackground(ContextCompat.getDrawable(this, android.R.drawable.ic_media_pause));
                    relogio.setBase(SystemClock.elapsedRealtime() - deslocamento);
                    relogio.start();
                    desativarFinalizarPartida();
                    relogio_parado = false;
                    toggleBtnsAdicionarJogadores();
                }
            } else {
                v.setBackground(ContextCompat.getDrawable(this, android.R.drawable.ic_media_play));
                relogio.stop();
                deslocamento = SystemClock.elapsedRealtime() - relogio.getBase();
                partida.setTempo(deslocamento);
                persistirDados();
                ativarFinalizarPartida();
                relogio_parado = true;
                toggleBtnsAdicionarJogadores();
            }
        }
    }

    private void play_efeito_sonoro(){
        efeitos_sonoros.setLooping(false);
        efeitos_sonoros.seekTo(0);
        efeitos_sonoros.start();
    }

    public void esconderAlerta(){
        if (alertaDialog != null && alertaDialog.isShowing()) alertaDialog.dismiss();
    }

    public void esconderTeclado(@NonNull View editText) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public void persistirDados(){
        torneio.setDataAtualizacaoLocal(System.currentTimeMillis());
        santuarioOlimpia.atualizar(true);
    }

    private boolean adicionarJogadorValidacao(@NonNull Equipe equipe, String nome, int numero, int posicao){
        return equipe.addJogador(new Jogador(equipe.bucarIdParaNovoJogador(), nome, posicao, numero)) != -1;
    }
}
