package com.leoPirpiri.protournamentmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import control.Olimpia;
import model.Equipe;
import model.Jogador;

public class EquipeActivity extends AppCompatActivity {
    private AlertDialog alertaDialog;
    private Olimpia santuarioOlimpia;
    private int equipeIndice;
    private Equipe equipe;
    private JogadoresAdapter jogadoresAdapter;

    private ListView ltv_jogadores_equipe;
    private TextView txv_sigla_equipe;
    private TextView txv_jogadores_inscritos;
    private EditText etx_nome_jogador;
    private Button btn_confirma_jogador;
    private Spinner spnr_numero;
    private Spinner spnr_posicao;

    private boolean atualizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipe);

        txv_jogadores_inscritos = findViewById(R.id.txv_jogadores_salvos);
        txv_sigla_equipe = findViewById(R.id.txv_sigla_equipe);
        ltv_jogadores_equipe = findViewById(R.id.list_jogadores);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton btn_add_jogador = findViewById(R.id.btn_novo_jogador);
        btn_add_jogador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                montarAlertaNovoEditaJogador(null);
            }
        });

        Intent intent = getIntent();
        Bundle dados = intent.getExtras();
        if (dados!=null) {
            equipeIndice = dados.getInt("equipe");
            metodoRaiz();
            setTitle(equipe.getNome());
        }

        ltv_jogadores_equipe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                montarAlertaNovoEditaJogador(equipe.getJogadores().get(position));
            }
        });

        ltv_jogadores_equipe.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                MontarAlertaDeletarJogador(position);
                return true;
            }
        });
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
        equipe = santuarioOlimpia
                .getTorneio(santuarioOlimpia.extrairIdEntidadeSuperiorLv0(equipeIndice))
                .getTime(equipeIndice);
        if(equipe != null) {
            txv_sigla_equipe.setText(equipe.getSigla());
            jogadoresAdapter = new JogadoresAdapter(EquipeActivity.this, equipe.getJogadores());
            ltv_jogadores_equipe.setAdapter(jogadoresAdapter);
            listarJogadores();
        } else {
            Toast.makeText(EquipeActivity.this, R.string.dados_erro_transitar_em_activity, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void MontarAlertaDeletarJogador(final int posJogador) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //define o titulo
        builder.setTitle(R.string.title_alerta_confir_excluir_jogador);
        //define a mensagem
        builder.setMessage(R.string.msg_alerta_confir_excluir_jogador);
        //define um botão como positivo
        builder.setPositiveButton(R.string.btn_confirmar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                excluirJogador(posJogador);
            }
        });
        //define um botão como negativo.
        builder.setNegativeButton(R.string.btn_cancelar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        mostrarAlerta(builder);
    }

    private void montarAlertaNovoEditaJogador(Jogador velhoJogador) {
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
        }
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
        builder.setTitle(R.string.title_alerta_novo_jogador);
        mostrarAlerta(builder);
    }

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
    }

    private void mostrarAlerta(AlertDialog.Builder builder) {
        alertaDialog = builder.create();
        alertaDialog.show();
    }

    private void listarJogadores() {
        if (equipe.getJogadores().isEmpty()) {
            txv_jogadores_inscritos.setText(R.string.equipe_sem_jogador);
        } else {
            txv_jogadores_inscritos.setText(R.string.equipe_com_jogador);
            jogadoresAdapter.notifyDataSetChanged();
        }
    }

    private void excluirJogador(int position) {
        if(equipe.delJogador(position) != null) {
            atualizar = true;
            listarJogadores();
        }
    }
}
