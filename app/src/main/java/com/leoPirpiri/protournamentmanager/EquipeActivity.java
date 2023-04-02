package com.leoPirpiri.protournamentmanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Arrays;

import control.CarrierSemiActivity;
import model.Olimpia;
import model.Equipe;
import model.Torneio;

public class EquipeActivity extends AppCompatActivity {
    private AlertDialog alertaDialog;
    private Olimpia santuarioOlimpia;
    private int equipeIndice;
    private Equipe equipe;
    private Torneio torneio;
    //private JogadoresAdapter jogadoresAdapter;

    //private ListView ltv_jogadores_equipe;
    private TextView txv_sigla_equipe;
    //private TextView txv_jogadores_inscritos;
    //private EditText etx_nome_jogador;
    //private Button btn_confirma_jogador;
    //private Spinner spnr_numero;
    //private Spinner spnr_posicao;

    private boolean atualizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipe);

        //txv_jogadores_inscritos = findViewById(R.id.txv_jogadores_salvos);
        txv_sigla_equipe = findViewById(R.id.txv_sigla_equipe);
        //ltv_jogadores_equipe = findViewById(R.id.list_jogadores);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //FloatingActionButton btn_add_jogador = findViewById(R.id.btn_novo_jogador);
        /*btn_add_jogador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                montarAlertaNovoEditarJogador(null);
            }
        });*/

        Intent intent = getIntent();
        Bundle dados = intent.getExtras();
        if (dados!=null) {
            equipeIndice = dados.getInt("equipe");
            metodoRaiz();
        }

        setTabLayout();

        /*ltv_jogadores_equipe.setOnItemClickListener((parent, view, position, id) -> {
            montarAlertaNovoEditarJogador(equipe.getJogadores().get(position));
        });

        ltv_jogadores_equipe.setOnItemLongClickListener((parent, view, position, id) -> {
            montarAlertaDeletarJogador(jogadoresAdapter.getItem(position));
            return true;
        });*/

        findViewById(R.id.btn_edt_equipe).setOnClickListener(v -> montarAlertaEditarEquipe());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(atualizar) {
            CarrierSemiActivity.persistirSantuario(EquipeActivity.this, santuarioOlimpia);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        metodoRaiz();
    }

    private void metodoRaiz() {
        santuarioOlimpia = CarrierSemiActivity.carregarSantuario(EquipeActivity.this);
        atualizar=false;
        torneio = santuarioOlimpia.getTorneioGerenciado(Olimpia.extrairIdEntidadeSuperiorLv0(equipeIndice));
        equipe = torneio.getEquipe(equipeIndice);
        /*if(equipe != null) {
            atualizarNomesEquipes();
            jogadoresAdapter = new JogadoresAdapter(EquipeActivity.this, equipe.getJogadores());
            ltv_jogadores_equipe.setAdapter(jogadoresAdapter);
            listarJogadores();
        } else {
            Toast.makeText(EquipeActivity.this, R.string.dados_erro_transitar_em_activity, Toast.LENGTH_SHORT).show();
            finish();
        }*/
    }

    private void setTabLayout() {
        NavegacaoPorPaginasAdapter adapter = new NavegacaoPorPaginasAdapter(
                this,
                Arrays.asList(new JogadoresDeEquipeFragment(this, equipe.getJogadores()),
                        new EstatisticasDeEquipeFragment(this),
                        new InformacoesFragment(R.string.informacoes_tela_equipe)),
                Arrays.asList(getResources().getStringArray(R.array.tab_bar_tela_equipe_nomes))
        );

        ViewPager2 viewPager = findViewById(R.id.tab_conteudo_equipe);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getItemCount());

        TabLayoutMediator mediator = new TabLayoutMediator(
                findViewById(R.id.tab_bar_equipe),
                viewPager,
                (tab, posicao) -> tab.setText(adapter.getTitulo(posicao)));
        mediator.attach();
    }

    private void atualizarNomesEquipes(){
        setTitle(equipe.getNome());
        txv_sigla_equipe.setText(equipe.getSigla());
    }

    private void montarAlertaEditarEquipe() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.alerta_nova_equipe, null);

        EditText etx_nome_equipe = view.findViewById(R.id.etx_nome_nova_equipe);
        EditText etx_sigla_equipe = view.findViewById(R.id.etx_sigla_nova_equipe);
        Button btn_confirma_equipe = view.findViewById(R.id.btn_confirmar_nova_equipe);
        btn_confirma_equipe.setEnabled(true);
        btn_confirma_equipe.setBackground(getDrawable(R.drawable.button_shape_enabled));
        btn_confirma_equipe.setText(R.string.btn_editar);

        etx_nome_equipe.setText(equipe.getNome());
        etx_nome_equipe.requestFocus();
        etx_sigla_equipe.setText(equipe.getSigla());
        //Listeners possíveis do alerta
        btn_confirma_equipe.setOnClickListener(arg0 -> {
            String nome = etx_nome_equipe.getText().toString().trim();
            String sigla = etx_sigla_equipe.getText().toString().trim().toUpperCase();
            boolean mudou = false;
            if (!nome.isEmpty() && !sigla.isEmpty()) {
                if(!equipe.getNome().equals(nome)) {
                    equipe.setNome(nome);
                    mudou = true;
                }
                if(sigla.length()>1 && !equipe.getSigla().equals(sigla) && torneio.siglaDisponivel(sigla)) {
                    equipe.setSigla(sigla);
                    mudou = true;
                }
                if (mudou){
                    atualizar = true;
                    atualizarNomesEquipes();
                } else {
                    CarrierSemiActivity.exemplo(EquipeActivity.this, getString(R.string.erro_atualizar_informacoes_equipe));
                }
                alertaDialog.dismiss();
            }
        });

        view.findViewById(R.id.btn_cancelar_equipe).setOnClickListener(arg0 -> alertaDialog.dismiss());

        etx_nome_equipe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String nome = etx_nome_equipe.getText().toString().trim();
                String sigla = etx_sigla_equipe.getText().toString();
                if (nome.isEmpty()) {
                    etx_sigla_equipe.setText("");
                } else {
                    String sigla_bot;
                    if (nome.contains(" ")) {
                        sigla_bot = siglatation(nome);
                    } else {
                        sigla_bot = nome.substring(0, 1).toUpperCase();
                    }
                    if (!sigla.trim().equals(sigla_bot)) {
                        etx_sigla_equipe.setText(sigla_bot);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        builder.setView(view);
        builder.setTitle(R.string.titulo_alerta_nova_equipe);
        mostrarAlerta(builder);
    }
/*
    private void montarAlertaDeletarJogador(Jogador j) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.alerta_default, null);

        Button btn_confirmar = view.findViewById(R.id.btn_confirmar_default);
        Button btn_cancelar = view.findViewById(R.id.btn_cancelar_default);
        TextView msg = view.findViewById(R.id.msg_alerta_default);
        btn_confirmar.setVisibility(View.VISIBLE);
        btn_cancelar.setVisibility(View.VISIBLE);
        msg.setVisibility(View.VISIBLE);
        msg.setText(R.string.msg_alerta_confir_excluir_jogador);

        btn_confirmar.setOnClickListener(arg0 -> {
            alertaDialog.dismiss();
            excluirJogador(j);
        });

        btn_cancelar.setOnClickListener(arg0 -> {
            alertaDialog.dismiss();
        });

        builder.setView(view);
        builder.setTitle(R.string.titulo_alerta_confir_excluir_jogador);
        mostrarAlerta(builder);
    }*/

    /*private void montarAlertaNovoEditarJogador(Jogador velhoJogador) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.alerta_novo_jogador, null);
        etx_nome_jogador = view.findViewById(R.id.etx_nome_novo_jogador);
        btn_confirma_jogador = view.findViewById(R.id.btn_confirmar_jogador);
        spnr_posicao = view.findViewById(R.id.spr_pos_novo_jogador);
        spnr_numero = view.findViewById(R.id.spr_num_novo_jogador);
        spnr_posicao.setAdapter(new ArrayAdapter(this, R.layout.spinner_item_style, getResources().getStringArray(R.array.posicoes_jogador)));
        ArrayList<Integer> numeros;
        if (velhoJogador == null) {
            numeros = equipe.getNumeracaoLivrePlantel(-1);
            spnr_posicao.setSelection(0);
        } else {
            numeros = equipe.getNumeracaoLivrePlantel(velhoJogador.getNumero());
            btn_confirma_jogador.setText(R.string.btn_editar);
            etx_nome_jogador.setText(velhoJogador.getNome());
            spnr_posicao.setSelection(velhoJogador.getPosicao());
            if(!torneio.atoJogador(velhoJogador.getId())){
                view.findViewById(R.id.btn_del_jogador).setVisibility(View.VISIBLE);
            }
        }
        etx_nome_jogador.requestFocus();
        spnr_numero.setAdapter(new ArrayAdapter(this, R.layout.spinner_item_style, numeros));
        spnr_numero.setSelection(0);
        //Listeners possíveis do alerta
        view.findViewById(R.id.btn_confirmar_jogador).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                String nome = etx_nome_jogador.getText().toString().trim();
                int numero = Integer.parseInt(spnr_numero.getSelectedItem().toString());
                int posicao = spnr_posicao.getSelectedItemPosition();
                if(velhoJogador==null) {
                    equipe.addJogador(new Jogador(equipe.getNovoJogadorId(), nome, posicao, numero));
                    Toast.makeText(EquipeActivity.this, R.string.jogador_adicionado, Toast.LENGTH_SHORT).show();
                } else {
                    velhoJogador.setNome(nome);
                    velhoJogador.setNumero(numero);
                    velhoJogador.setPosicao(posicao);
                }

                listarJogadores();
                atualizar=true;
                alertaDialog.dismiss();
            }
        });

        view.findViewById(R.id.btn_cancelar_jogador).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //desfaz o alerta.
                alertaDialog.dismiss();
            }
        });

        etx_nome_jogador.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validarEdicao(velhoJogador);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        spnr_numero.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                validarEdicao(velhoJogador);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spnr_posicao.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                validarEdicao(velhoJogador);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        builder.setView(view);
        builder.setTitle(R.string.titulo_alerta_novo_jogador);
        mostrarAlerta(builder);
    }*/

    private String siglatation(String entrada) {
        String sigla = "";
        for (String word : entrada.split(" ")) {
            if (word.length()>2){
                sigla = sigla.concat(word.substring(0,1));
            }
            if(sigla.length()>4){
                break;
            }
        }
        return sigla.toUpperCase();
    }
/*
    private void validarEdicao(Jogador pl1) {
        String nome = etx_nome_jogador.getText().toString().trim();
        if(!nome.isEmpty()) {
            if(pl1 == null) {
                ativarOKalertaJogador();
            }else if (!pl1.getNome().equals(nome) ||
                    pl1.getNumero() != Integer.parseInt(spnr_numero.getSelectedItem().toString()) ||
                    pl1.getPosicao() != spnr_posicao.getSelectedItemPosition()) {
                ativarOKalertaJogador();
            } else {
                desativarOKalertaJogador();
            }
        } else {
            desativarOKalertaJogador();
        }
    }

    private void ativarOKalertaJogador() {
        btn_confirma_jogador.setEnabled(true);
        btn_confirma_jogador.setBackground(getDrawable(R.drawable.button_shape_enabled));
    }

    private void desativarOKalertaJogador() {
        btn_confirma_jogador.setEnabled(false);
        btn_confirma_jogador.setBackground(getDrawable(R.drawable.button_shape_desabled));
    }*/

    private void mostrarAlerta(AlertDialog.Builder builder) {
        mostrarAlerta(builder, R.drawable.background_alerta);
    }

    private void mostrarAlerta(AlertDialog.Builder builder, int background) {
        alertaDialog = builder.create();
        alertaDialog.getWindow().setBackgroundDrawable(getDrawable(background));
        alertaDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertaDialog.show();
    }
/*

    private void listarJogadores() {
        if (equipe.getJogadores().isEmpty()) {
            txv_jogadores_inscritos.setText(R.string.equipe_sem_jogador);
        } else {
            txv_jogadores_inscritos.setText(R.string.equipe_com_jogador);
            jogadoresAdapter.notifyDataSetChanged();
        }
    }

    private void excluirJogador(Jogador jogador) {
        if(!torneio.atoJogador(jogador.getId()) && equipe.delJogador(jogador)) {
            atualizar = true;
            CarrierSemiActivity.exemplo(this, getString(R.string.msg_alerta_sucesso_excluir_jogador));
            listarJogadores();
        } else {
            CarrierSemiActivity.exemplo(this, getString(R.string.msg_alerta_erro_excluir_jogador));
        }
    }
*/
}
