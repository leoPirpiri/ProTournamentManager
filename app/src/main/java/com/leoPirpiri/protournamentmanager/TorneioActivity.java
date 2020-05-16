package com.leoPirpiri.protournamentmanager;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import control.Olimpia;
import model.Equipe;
import model.Torneio;

public class TorneioActivity extends AppCompatActivity {
    //atributo da classe.
    private AlertDialog alertaNovaEquipe;
    private Olimpia santuarioOlimpia;
    private Torneio torneio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_torneio);
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
        if(dados!=null){
            santuarioOlimpia = (Olimpia) dados.getSerializable("olimpia");
            torneio = (Torneio) dados.getSerializable("torneio");
            setTitle(torneio.getNome());
            if(santuarioOlimpia.getTorneio(torneio) != -1){
                santuarioOlimpia.addTorneio(torneio);
                persistirSantuario();
            }else{
                ArrayList<Equipe> equipes = torneio.getTimes();
                listarTimes(equipes);
            }
            Toast.makeText(TorneioActivity.this, torneio.getNome(), Toast.LENGTH_LONG).show();
        }
    }

    private void mostrarAlertaNovaEquipe(){
        View view = getLayoutInflater().inflate(R.layout.alerta_nova_equipe, null);
        //Listeners possíveis do alerta
        view.findViewById(R.id.btn_cancelar_equipe).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //exibe um Toast informativo.
                Toast.makeText(TorneioActivity.this, "Cancelar, tchau!", Toast.LENGTH_SHORT).show();
                //desfaz o alerta.
                alertaNovaEquipe.dismiss();
            }
        });
        mostrarAlerta("Informações da equipe:", view);
    }

    private void mostrarAlerta(String titulo, View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titulo);
        builder.setView(view);
        alertaNovaEquipe = builder.create();
        alertaNovaEquipe.show();
    }

    private void persistirSantuario (){
        try {
            santuarioOlimpia.salvarSantuario(santuarioOlimpia, openFileOutput(santuarioOlimpia.NOME_ARQUIVO_SERIALIZADO, MODE_PRIVATE));
            Toast.makeText(TorneioActivity.this, "Santuario Salvo.", Toast.LENGTH_LONG).show();;
        } catch (IOException ex) {
            System.out.println(ex.getMessage());//apagar quando der certo.
            Toast.makeText(TorneioActivity.this, R.string.erro_gravar_santuario, Toast.LENGTH_LONG).show();
        }
    }

    private void listarTimes(ArrayList<Equipe> equipes){
        torneiosAdapter = new TorneiosAdapter(MainActivity.this, torneios);
        ltv_torneios_recentes.setAdapter(torneiosAdapter);
        for (Torneio torneio:torneios){
            System.out.println("Nome: " + torneio.getNome() + " Fechado: " + torneio.isFechado());
        }
        torneiosAdapter.notifyDataSetChanged();
    }
}
