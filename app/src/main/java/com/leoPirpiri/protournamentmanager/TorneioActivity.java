package com.leoPirpiri.protournamentmanager;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import control.Olimpia;
import model.Equipe;
import model.Torneio;

public class TorneioActivity extends AppCompatActivity {
    private AlertDialog alertaNovaEquipe;
    private Olimpia santuarioOlimpia;
    private Torneio torneio;
    private ListView ltv_equipes_torneio;
    private TextView txv_estado_torneio;
    private TextView txv_equipes_salvas;
    private EditText etx_sigla_equipe;
    private EditText etx_nome_equipe;
    private Button btn_confirma_equipe;
    private EquipesAdapter equipesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_torneio);

        txv_estado_torneio = findViewById(R.id.txv_estado_torneio);
        txv_equipes_salvas = findViewById(R.id.txv_equipes_salvas);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton btn_add_equipe = findViewById(R.id.btn_nova_equipe);
        btn_add_equipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            mostrarAlertaNovaEquipe();
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                  //      .setAction("Action", null).show();
            }
        });

        Intent intent = getIntent();
        Bundle dados = intent.getExtras();
        if (dados!=null) {
            santuarioOlimpia = (Olimpia) dados.getSerializable("olimpia");
            torneio = santuarioOlimpia.getTorneio(dados.getInt("torneio"));
            setTitle(torneio.getNome());
            txv_estado_torneio.setText(torneio.isFechado() ? R.string.estado_fechado : (
                    torneio.getCampeao() == null ? R.string.estado_aberto : R.string.estado_encerrado));
            listarTimes(torneio.getTimes());
        }
    }

    private void mostrarAlertaNovaEquipe(){
        View view = getLayoutInflater().inflate(R.layout.alerta_nova_equipe, null);
        etx_nome_equipe = view.findViewById(R.id.etx_nome_nova_equipe);
        etx_sigla_equipe = view.findViewById(R.id.etx_sigla_nova_equipe);
        btn_confirma_equipe = view.findViewById(R.id.btn_confirmar_equipe);
        //Listeners possíveis do alerta
        view.findViewById(R.id.btn_confirmar_equipe).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //exibe um Toast informativo.
                Toast.makeText(TorneioActivity.this, R.string.equipe_adicionada, Toast.LENGTH_SHORT).show();
                alertaNovaEquipe.dismiss();
            }
        });

        view.findViewById(R.id.btn_cancelar_equipe).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //desfaz o alerta.
                alertaNovaEquipe.dismiss();
            }
        });

        etx_nome_equipe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(etx_nome_equipe.getText().toString().isEmpty() ||
                        etx_sigla_equipe.getText().toString().isEmpty()){
                    desativarOKalertaEquipe();
                } else {
                    ativarOKalertaEquipe();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etx_sigla_equipe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(etx_nome_equipe.getText().toString().isEmpty() ||
                        etx_sigla_equipe.getText().toString().isEmpty()){
                    desativarOKalertaEquipe();
                } else {
                    ativarOKalertaEquipe();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        mostrarAlerta("Informações da equipe:", view);
    }

    private void ativarOKalertaEquipe(){
        btn_confirma_equipe.setEnabled(true);
        btn_confirma_equipe.setBackground(getDrawable(R.drawable.button_shape_enabled));
    }

    private void desativarOKalertaEquipe(){
        btn_confirma_equipe.setEnabled(false);
        btn_confirma_equipe.setBackground(getDrawable(R.drawable.button_shape_desabled));
    }

    private void mostrarAlerta(String titulo, View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titulo);
        builder.setView(view);
        alertaNovaEquipe = builder.create();
        alertaNovaEquipe.show();
    }

    private void listarTimes(ArrayList<Equipe> equipes){
        if (equipes.size()!=0) {
            txv_equipes_salvas.setText(R.string.torneio_com_equipes);
            equipesAdapter = new EquipesAdapter(TorneioActivity.this, equipes);
            ltv_equipes_torneio.setAdapter(equipesAdapter);
            equipesAdapter.notifyDataSetChanged();
        } else {
            txv_equipes_salvas.setText(R.string.torneio_sem_equipes);
        }
    }
}
