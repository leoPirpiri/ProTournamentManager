package com.leoPirpiri.protournamentmanager;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

public class TorneioActivity extends AppCompatActivity {
    //atributo da classe.
    private AlertDialog alertaNovaEquipe;
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
            Toast.makeText(TorneioActivity.this, dados.getString("nome_torneio"), Toast.LENGTH_LONG).show();
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
}
