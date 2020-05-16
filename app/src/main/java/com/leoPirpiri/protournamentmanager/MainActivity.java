package com.leoPirpiri.protournamentmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import control.Olimpia;
import model.Partida;
import model.Torneio;

public class MainActivity extends AppCompatActivity {
    private Olimpia santuarioOlimpia;
    private EditText nome_novo_torneio;
    private Button btn_novo_torneio;
    private Button btn_simulador_partida;
    private ListView ltv_torneios_recentes;
    private TorneiosAdapter torneiosAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nome_novo_torneio = findViewById(R.id.nome_novo_torneio);
        btn_novo_torneio = findViewById(R.id.btn_novo_tourneio);
        btn_simulador_partida = findViewById(R.id.btn_simulador);
        ltv_torneios_recentes = findViewById(R.id.list_torneios_recentes);
        carregarSantuario();

        //Listeners
        nome_novo_torneio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(nome_novo_torneio.getText().toString().equals("")){
                    btn_novo_torneio.setEnabled(false);
                    btn_novo_torneio.setBackground(getDrawable(R.drawable.button_shape_desabled));
                }else{
                    btn_novo_torneio.setEnabled(true);
                    btn_novo_torneio.setBackground(getDrawable(R.drawable.button_shape_enabled));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        nome_novo_torneio.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus==false) {
                    esconderTeclado(MainActivity.this, nome_novo_torneio);
                }
            }
        });
        btn_novo_torneio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                esconderTeclado(MainActivity.this, nome_novo_torneio);
                abrirTorneio(new Torneio(nome_novo_torneio.getText().toString()));
            }
        });
        btn_simulador_partida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirSimulador();
            }
        });
        ltv_torneios_recentes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Torneio t = (Torneio) parent.getAdapter().getItem(position);
                abrirTorneio(t);
            }
        });
    }

    //Carrega ou inicia o santuário onde ocorre os jogos.
    private void carregarSantuario(){
        if (new File(getFileStreamPath(Olimpia.NOME_ARQUIVO_SERIALIZADO).toString()).exists()) {
            try {
                santuarioOlimpia = Olimpia.carregarSantuario(openFileInput(Olimpia.NOME_ARQUIVO_SERIALIZADO));
                listarTorneios(santuarioOlimpia.getTorneios());
            } catch (IOException ex) {
                //System.out.println(ex.getMessage());
                Toast.makeText(MainActivity.this, R.string.erro_leitura_santuario, Toast.LENGTH_LONG).show();
            }
        }else{
            //Não existe torneios anteriores, Informar na lista.
        }
        if (santuarioOlimpia == null) {
            santuarioOlimpia = new Olimpia();
        }
    }
    private void esconderTeclado(Context context, View editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    private void abrirTorneio(Torneio torneio){
        Intent intent = new Intent(getApplicationContext(), TorneioActivity.class);
        Bundle dados = new Bundle();
        //Passa alguns dados para a próxima activity
        dados.putSerializable("torneio", torneio);
        dados.putSerializable("olimpia", santuarioOlimpia);
        intent.putExtras(dados);
        startActivity(intent);
    }

    private void listarTorneios(ArrayList<Torneio> torneios){
        torneiosAdapter = new TorneiosAdapter(MainActivity.this, torneios);
        ltv_torneios_recentes.setAdapter(torneiosAdapter);
        for (Torneio torneio:torneios){
            System.out.println("Nome: " + torneio.getNome() + " Fechado: " + torneio.isFechado());
        }
        torneiosAdapter.notifyDataSetChanged();
    }

    private void abrirSimulador(){
        Intent intent = new Intent(getApplicationContext(), PartidaActivity.class);
        intent.putExtra("partida", new Partida());
        startActivity(intent);
    }
}
