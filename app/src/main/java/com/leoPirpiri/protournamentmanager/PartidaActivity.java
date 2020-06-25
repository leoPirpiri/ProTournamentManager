package com.leoPirpiri.protournamentmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;

public class PartidaActivity extends AppCompatActivity {
    private boolean relogio_parado;
    private long deslocamento;

    private Chronometer relogio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partida);
        relogio = findViewById(R.id.cronometro);
        relogio_parado = true;
        deslocamento = 0;
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
            relogio_parado = false;
        } else {
            v.setBackground(getDrawable(android.R.drawable.ic_media_play));
            relogio.stop();
            deslocamento = SystemClock.elapsedRealtime() - relogio.getBase();
            relogio_parado = true;
        }
    }
}
