package com.leoPirpiri.protournamentmanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import control.Olimpia;
import model.Torneio;

public class MainActivity extends AppCompatActivity {
    private AlertDialog alertaDialog;
    private Olimpia santuarioOlimpia;
    private EditText nome_novo_torneio;
    private Button btn_novo_torneio;
    private Button btn_simulador_partida;
    private ListView ltv_torneios_recentes;
    private TextView txv_torneios_recentes;
    private TorneiosAdapter torneiosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nome_novo_torneio = findViewById(R.id.nome_novo_torneio);
        btn_novo_torneio = findViewById(R.id.btn_novo_tourneio);
        btn_simulador_partida = findViewById(R.id.btn_simulador);
        ltv_torneios_recentes = findViewById(R.id.list_torneios_recentes);
        txv_torneios_recentes = findViewById(R.id.txv_torneios_recentes);
        //Inicio padrão.
        metodoRaiz();

        //Listeners
        nome_novo_torneio.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(nome_novo_torneio.getText().toString().equals("")){
                    desarmaBTNnovoTorneio();
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
                int idNovoTorneio = santuarioOlimpia.getNovoTorneioId();
                if (idNovoTorneio != 0){
                    idNovoTorneio = santuarioOlimpia.addTorneio(new Torneio( idNovoTorneio,
                                                                    nome_novo_torneio.getText().toString())
                                                                );
                    if (idNovoTorneio != -1){
                        CarrierSemiActivity.persistirSantuario(MainActivity.this, santuarioOlimpia);
                        abrirTorneio(idNovoTorneio);
                    }
                }
                desarmaBTNnovoTorneio();
                nome_novo_torneio.setText("");
            }
        });

        btn_simulador_partida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nome_novo_torneio.setText("");
                desarmaBTNnovoTorneio();
                abrirSimulador();
            }
        });

        ltv_torneios_recentes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                abrirTorneio(santuarioOlimpia.getTorneios().get(position).getId());
            }
        });

        ltv_torneios_recentes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                montarAlertaExcluirTorneio(position);
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        metodoRaiz();
    }

    private void metodoRaiz(){
        //Carrega ou inicia o santuário onde ocorre os jogos.
        santuarioOlimpia = CarrierSemiActivity.carregarSantuario(MainActivity.this);
        torneiosAdapter = new TorneiosAdapter(MainActivity.this, santuarioOlimpia.getTorneios());
        ltv_torneios_recentes.setAdapter(torneiosAdapter);
        desarmaBTNnovoTorneio();
        //Lista os torneios salvos anteriormente.
        listarTorneios();
    }

    private void montarAlertaExcluirTorneio(final int posTorneio){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //define o titulo
        builder.setTitle(R.string.title_alerta_confir_excluir_torneio);
        //define a mensagem
        builder.setMessage(R.string.msg_alerta_confir_excluir_torneio);
        //define um botão como positivo
        builder.setPositiveButton(R.string.btn_confirmar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                excluirTorneio(posTorneio);
            }
        });
        //define um botão como negativo.
        builder.setNegativeButton(R.string.btn_cancelar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        mostrarAlerta(builder);
    }

    private void mostrarAlerta(AlertDialog.Builder builder){
        alertaDialog = builder.create();
        alertaDialog.show();
    }

    private void desarmaBTNnovoTorneio() {
        if(santuarioOlimpia.TORNEIO_MAX==santuarioOlimpia.getOcupacao()) {
            nome_novo_torneio.setHint(R.string.santuario_cheio);
        } else {
            nome_novo_torneio.setHint(R.string.novo_torneio);
        }
        btn_novo_torneio.setEnabled(false);
        btn_novo_torneio.setBackground(getDrawable(R.drawable.button_shape_desabled));
    }

    private void esconderTeclado(Context context, View editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    private void listarTorneios(){
        if(santuarioOlimpia.getTorneios().isEmpty()){
            txv_torneios_recentes.setText(R.string.santuario_vazio);
        } else {
            txv_torneios_recentes.setText(R.string.santuario_com_torneios);
            torneiosAdapter.notifyDataSetChanged();
        }
    }

    private void abrirTorneio(int torneio){
        Intent intent = new Intent(getApplicationContext(), TorneioActivity.class);
        Bundle dados = new Bundle();
        //Passa alguns dados para a próxima activity
        dados.putInt("torneio", torneio);
        intent.putExtras(dados);
        startActivity(intent);
    }

    private void excluirTorneio(int position) {
        if(santuarioOlimpia.delTorneio(position) != null){
            CarrierSemiActivity.persistirSantuario(MainActivity.this, santuarioOlimpia);
            listarTorneios();
        }
    }

    private void abrirSimulador(){
        Intent intent = new Intent(getApplicationContext(), PartidaActivity.class);
        intent.putExtra("partida", -1);
        startActivity(intent);
    }

}
