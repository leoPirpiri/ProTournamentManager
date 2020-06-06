package com.leoPirpiri.protournamentmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
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
    private AlertDialog alertaDialog;
    private Olimpia santuarioOlimpia;
    private int torneioIndice;
    private Torneio torneio;
    private ListView ltv_equipes_torneio;
    private TextView txv_estado_torneio;
    private TextView txv_equipes_salvas;
    private EditText etx_sigla_equipe;
    private EditText etx_nome_equipe;
    private Button btn_confirma_equipe;
    private EquipesAdapter equipesAdapter;
    private boolean atualizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_torneio);

        txv_estado_torneio = findViewById(R.id.txv_estado_torneio);
        txv_equipes_salvas = findViewById(R.id.txv_equipes_salvas);
        ltv_equipes_torneio = findViewById(R.id.list_equipes);

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
            torneioIndice = dados.getInt("torneio");
            metodoRaiz();
            setTitle(torneio.getNome());
        }

        ltv_equipes_torneio.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mostrarAlertaBasico(position);
            }
        });

        ltv_equipes_torneio.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                excluirEquipe(position);
                return true;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(atualizar){
            CarrierSemiActivity.persistirSantuario(TorneioActivity.this, santuarioOlimpia);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        metodoRaiz();
    }

    private void metodoRaiz(){
        santuarioOlimpia = CarrierSemiActivity.carregarSantuario(TorneioActivity.this);
        atualizar=false;
        torneio = santuarioOlimpia.getTorneio(torneioIndice);
        txv_estado_torneio.setText(torneio.isFechado() ? R.string.estado_fechado : (
                torneio.getCampeao() == null ? R.string.estado_aberto : R.string.estado_encerrado));
        listarTimes(torneio.getTimes());
    }

    private void mostrarAlertaBasico(final int posEquipe){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //define o titulo
        builder.setTitle(R.string.title_alerta_confir_abrir_equipe);
        //define a mensagem
        builder.setMessage(R.string.msg_alerta_confir_equipe);
        //define um botão como positivo
        builder.setPositiveButton(R.string.btn_confirmar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                abrirEquipe(posEquipe);
            }
        });
        //define um botão como negativo.
        builder.setNegativeButton(R.string.btn_cancelar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        mostrarAlerta(builder);
    }

    private void mostrarAlertaNovaEquipe(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.alerta_nova_equipe, null);
        etx_nome_equipe = view.findViewById(R.id.etx_nome_nova_equipe);
        etx_sigla_equipe = view.findViewById(R.id.etx_sigla_nova_equipe);
        btn_confirma_equipe = view.findViewById(R.id.btn_confirmar_equipe);

        //Listeners possíveis do alerta
        view.findViewById(R.id.btn_confirmar_equipe).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                String nome = etx_nome_equipe.getText().toString();
                String sigla = etx_sigla_equipe.getText().toString();
                if (!nome.isEmpty() && !sigla.isEmpty()) {
                    torneio.addTime(new Equipe(nome, sigla));
                    Toast.makeText(TorneioActivity.this, R.string.equipe_adicionada, Toast.LENGTH_SHORT).show();
                    atualizar=true;
                    listarTimes(torneio.getTimes());
                }
                alertaDialog.dismiss();
            }
        });

        view.findViewById(R.id.btn_cancelar_equipe).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //desfaz o alerta.
                alertaDialog.dismiss();
            }
        });

        etx_nome_equipe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String nome = etx_nome_equipe.getText().toString().trim();
                String sigla = etx_sigla_equipe.getText().toString();
                if(nome.isEmpty()){
                    etx_sigla_equipe.setText("");
                    desativarOKalertaEquipe();
                } else {
                    String sigla_bot;
                    if(nome.contains(" ")) {
                         sigla_bot = siglatation(nome);
                    } else {
                        sigla_bot = nome.substring(0,1).toUpperCase();
                    }
                    if(!sigla.trim().equals(sigla_bot)) {
                        etx_sigla_equipe.setText(sigla_bot);
                    }
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
        builder.setView(view);
        builder.setTitle(R.string.title_alerta_nova_equipe);
        mostrarAlerta(builder);
    }

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

    private void ativarOKalertaEquipe(){
        btn_confirma_equipe.setEnabled(true);
        btn_confirma_equipe.setBackground(getDrawable(R.drawable.button_shape_enabled));
    }

    private void desativarOKalertaEquipe(){
        btn_confirma_equipe.setEnabled(false);
        btn_confirma_equipe.setBackground(getDrawable(R.drawable.button_shape_desabled));
    }

    private void mostrarAlerta(AlertDialog.Builder builder){
        alertaDialog = builder.create();
        alertaDialog.show();
    }

    private void listarTimes(ArrayList<Equipe> equipes){
        if (equipes.isEmpty()) {
            txv_equipes_salvas.setText(R.string.torneio_sem_equipes);
        } else {
            txv_equipes_salvas.setText(R.string.torneio_com_equipes);
            equipesAdapter = new EquipesAdapter(TorneioActivity.this, equipes);
            ltv_equipes_torneio.setAdapter(equipesAdapter);
            equipesAdapter.notifyDataSetChanged();
        }
    }

    private void abrirEquipe(int position) {
//        Intent intent = new Intent(getApplicationContext(), TorneioActivity.class);
//        Bundle dados = new Bundle();
//        //Passa alguns dados para a próxima activity
//        dados.putInt("torneio", torneio);
//        intent.putExtras(dados);
//        startActivity(intent);
    }

    private void excluirEquipe(int position) {
        if(torneio.delTime(position) != null){
            atualizar = true;
            listarTimes(torneio.getTimes());
        }
    }
}