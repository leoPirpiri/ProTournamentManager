package com.leoPirpiri.protournamentmanager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;
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
    private MediaPlayer efeitos_sonoros;
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
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                montarAlertaAcaoPartida(true, jam.getItem(position), jam.getAcoesIndividuais(position));
            }
        });

        ltv_jogadores_visitantes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                montarAlertaAcaoPartida(false, jav.getItem(position), jav.getAcoesIndividuais(position));
            }
        });

        ltv_jogadores_mandantes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                montarAlertaInfromacoesIndividuais(true, jam.getItem(position), jam.getAcoesIndividuais(position));
                return true;
            }
        });

        ltv_jogadores_visitantes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                montarAlertaInfromacoesIndividuais(false, jav.getItem(position), jav.getAcoesIndividuais(position));
                return true;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!relogio_parado) {
            relogio.stop();
            deslocamento = SystemClock.elapsedRealtime() - relogio.getBase();
            partida.setTempo(deslocamento);
            atualizar = true;
        }
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
        deslocamento = partida.getTempo();
        relogio.setBase(SystemClock.elapsedRealtime() - deslocamento);
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void montarAlertaAcaoPartida(boolean isMandante, Jogador j, HashMap<Integer, Integer> acoes_jogador) {
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
        if( acoes_jogador.getOrDefault(Score.TIPO_AMARELO, 0) >= 2
           || acoes_jogador.containsKey(Score.TIPO_VERMELHO)){
            //Jogador expulso: NÃO recebe ponto, falta ou novo cartão
            btn_add_gol.setBackground(ic_gol_desabled);
            btn_add_gol.setEnabled(false);
            btn_add_falta.setBackground(ic_falta);
            btn_add_falta.setEnabled(false);
            btn_add_vermelho.setBackground(ic_cartao_desabled);
            btn_add_vermelho.setEnabled(false);
            btn_add_amarelo.setBackground(ic_cartao_desabled);
            btn_add_amarelo.setEnabled(false);
            btn_del_gol.setBackground(ic_gol_desabled);
            btn_del_gol.setForeground(ic_del_desabled);
            btn_del_gol.setEnabled(false);
            btn_del_falta.setBackground(ic_falta);
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
            //Linha dos pontos
            if(acoes_jogador.containsKey(Score.TIPO_PONTO)) {
                btn_del_gol.setBackground(ic_gol);
                btn_del_gol.setForeground(ic_del_default);
                btn_del_gol.setEnabled(true);
            } else {
                btn_del_gol.setBackground(ic_gol_desabled);
                btn_del_gol.setForeground(ic_del_desabled);
                btn_del_gol.setEnabled(false);
            }
            btn_add_gol.setBackground(ic_gol);
            btn_add_gol.setEnabled(true);
            //Linha das faltas
            if(acoes_jogador.containsKey(Score.TIPO_FALTA_INDIVIDUAL)){
                btn_del_falta.setBackground(ic_falta);
                btn_del_falta.setForeground(ic_del_default);
                btn_del_falta.setEnabled(true);
            } else {
                btn_del_falta.setBackground(ic_falta);
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
            btn_add_amarelo.setEnabled(true);
            btn_add_amarelo.setBackground(ic_cartao_amarelo);
        }

        btn_add_gol.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onClick(View arg0) {
                efeitos_sonoros = MediaPlayer.create(PartidaActivity.this, R.raw.aviso_gol);
                play_efeito_sonoro();
                adicionarAcaoJogador(isMandante, new Score(partidaIndice, j.getId(), Score.TIPO_PONTO));
                alertaDialog.dismiss();
            }
        });

        btn_add_falta.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onClick(View arg0) {
                efeitos_sonoros = MediaPlayer.create(PartidaActivity.this, R.raw.aviso_falta);
                play_efeito_sonoro();
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void montarAlertaInfromacoesIndividuais(boolean isMandante, Jogador j, HashMap<Integer, Integer> acoes_jogador) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.alerta_novo_jogador, null);
        //Preenchendo atributos pessoais do jogador.
        EditText etx_nome_jogador = view.findViewById(R.id.etx_nome_novo_jogador);
        Button btn_confirma_jogador = view.findViewById(R.id.btn_confirmar_jogador);
        Button btn_deletar_jogador = view.findViewById(R.id.btn_del_jogador);
        Spinner spnr_posicao = view.findViewById(R.id.spr_pos_novo_jogador);
        Spinner spnr_numero = view.findViewById(R.id.spr_num_novo_jogador);
        spnr_posicao.setAdapter(new ArrayAdapter(this, R.layout.spinner_item_style, getResources().getStringArray(R.array.posicoes_jogador)));

        ArrayList<Integer> numeros;
        etx_nome_jogador.setText(j.getNome());
        spnr_posicao.setSelection(j.getPosicao());
        if(isMandante) {
            numeros = mandante.getNumeracaoLivrePlantel(j.getNumero());
        } else {
            numeros = visitante.getNumeracaoLivrePlantel(j.getNumero());
        }
        spnr_numero.setAdapter(new ArrayAdapter(this, R.layout.spinner_item_style, numeros));
        spnr_numero.setSelection(0);

        btn_confirma_jogador.setEnabled(true);
        btn_confirma_jogador.setText(R.string.btn_editar);
        btn_confirma_jogador.setBackground(getDrawable(R.drawable.button_shape_enabled));

        if(acoes_jogador.isEmpty()) {
            btn_deletar_jogador.setVisibility(View.VISIBLE);
        }
        //Preenchendo informações do jogador na partida.
        view.findViewById(R.id.quadro_info_acoes_jogador).setVisibility(View.VISIBLE);
        TextView txv_jogador_pontos = view.findViewById(R.id.txv_jogador_pontos);
        TextView txv_jogador_faltas = view.findViewById(R.id.txv_jogador_faltas);
        TextView txv_jogador_divisor = view.findViewById(R.id.txv_jogador_divisor_cartoes);
        ImageView img_sem_cartao = view.findViewById(R.id.img_jogador_sem_cartao);
        ImageView img_primeiro_amarelo = view.findViewById(R.id.img_jogador_amarelo1);
        ImageView img_cartao_vermelho = view.findViewById(R.id.img_jogador_vermelho);
        LinearLayout img_segundo_amarelo = view.findViewById(R.id.img_jogador_amarelo2);

        txv_jogador_pontos.setText(Integer.toString(acoes_jogador.getOrDefault(Score.TIPO_PONTO, 0)));
        txv_jogador_faltas.setText(Integer.toString(acoes_jogador.getOrDefault(Score.TIPO_FALTA_INDIVIDUAL, 0)));
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

        btn_confirma_jogador.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                String nome = etx_nome_jogador.getText().toString().trim();
                int numero = Integer.parseInt(spnr_numero.getSelectedItem().toString());
                int posicao = spnr_posicao.getSelectedItemPosition();
                boolean mudou = true;
                if(!nome.isEmpty()) {
                    if (!nome.equals(j.getNome()) || numero != j.getNumero() || posicao != j.getPosicao()) {
                        j.setNome(nome);
                        j.setNumero(numero);
                        j.setPosicao(posicao);
                        atualizar = true;
                        atualizarCamposTime(isMandante);
                    } else {
                        Toast.makeText(PartidaActivity.this, R.string.erro_atualizar_mesmo_jogador_em_partida, Toast.LENGTH_LONG).show();
                    }
                    alertaDialog.dismiss();
                } else {
                    Toast.makeText(PartidaActivity.this, R.string.erro_atualizar_jogador_em_partida, Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_deletar_jogador.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if(isMandante){
                    if(atualizar = mandante.delJogador(j)){
                        Toast.makeText(PartidaActivity.this, R.string.del_jogador_sucesso, Toast.LENGTH_LONG).show();
                        atualizarCamposTime(isMandante);
                    }
                } else {
                    if (atualizar = visitante.delJogador(j)) {
                        Toast.makeText(PartidaActivity.this, R.string.del_jogador_sucesso, Toast.LENGTH_LONG).show();
                        atualizarCamposTime(isMandante);
                    }
                }
                alertaDialog.dismiss();
            }
        });

        view.findViewById(R.id.btn_cancelar_jogador).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                alertaDialog.dismiss();
            }
        });

        builder.setView(view);
        //define o titulo
        builder.setTitle(R.string.title_alerta_partida_detalhes_jogador);
        mostrarAlerta(builder);
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
                    4,
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
            partida.setTempo(deslocamento);
            atualizar = true;
            ativarFinalizarPartida();
            relogio_parado = true;
        }
    }

    private void play_efeito_sonoro(){
        efeitos_sonoros.setLooping(false);
        efeitos_sonoros.seekTo(0);
        efeitos_sonoros.start();
    }
}
