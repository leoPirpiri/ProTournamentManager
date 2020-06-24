package com.leoPirpiri.protournamentmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import control.Olimpia;
import model.Torneio;

public class TabelaActivity extends AppCompatActivity {
    private int torneioIndice;
    private Olimpia santuarioOlimpia;
    private Torneio torneio;
    private ListView ltv_oitavas_esquerda;
    private ListView ltv_oitavas_direita;
    private PartidasAdapter partidasAdapterE;
    private PartidasAdapter partidasAdapterD;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabela);
        ltv_oitavas_direita = findViewById(R.id.ltv_partidas_oitava_d);
        ltv_oitavas_esquerda = findViewById(R.id.ltv_partidas_oitava_e);

        Intent intent = getIntent();
        Bundle dados = intent.getExtras();
        if (dados!=null) {
            torneioIndice = dados.getInt("torneio");
            metodoRaiz();
        }
    }

    public void metodoRaiz() {
        santuarioOlimpia = CarrierSemiActivity.carregarSantuario(TabelaActivity.this);
        torneio = santuarioOlimpia.getTorneio(torneioIndice);
        if(torneio != null) {
            partidasAdapterE = new PartidasAdapter(TabelaActivity.this, torneio, true);
            partidasAdapterD = new PartidasAdapter(TabelaActivity.this, torneio, false);
            ltv_oitavas_esquerda.setAdapter(partidasAdapterE);
            ltv_oitavas_direita.setAdapter(partidasAdapterD);
            listarPartidas();
        } else {
            Toast.makeText(TabelaActivity.this, R.string.dados_erro_transitar_em_activity, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void listarPartidas() {
        ltv_oitavas_direita.deferNotifyDataSetChanged();
        ltv_oitavas_esquerda.deferNotifyDataSetChanged();
    }
    public void exemplo(View view) {
        CarrierSemiActivity.exemplo(this, "Clicou em "+view.getTransitionName());
    }
}