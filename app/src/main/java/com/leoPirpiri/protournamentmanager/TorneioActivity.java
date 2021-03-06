package com.leoPirpiri.protournamentmanager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import control.Olimpia;
import model.Equipe;
import model.Torneio;
import pl.droidsonroids.gif.GifImageView;

public class TorneioActivity extends AppCompatActivity {
    private int torneioIndice;
    private boolean atualizar = false;

    private AlertDialog alertaDialog;
    private Olimpia santuarioOlimpia;
    private Torneio torneio;
    private ListView ltv_equipes_torneio;
    private TextView txv_estado_torneio;
    private TextView txv_equipes_salvas;
    private EditText etx_sigla_equipe;
    private EditText etx_nome_equipe;
    private Button btn_confirma_equipe;
    private Button btn_gerar_tabela;
    private FloatingActionButton btn_add_equipe;
    private EquipesAdapter equipesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_torneio);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txv_estado_torneio = findViewById(R.id.txv_estado_torneio);
        txv_equipes_salvas = findViewById(R.id.txv_equipes_salvas);
        ltv_equipes_torneio = findViewById(R.id.list_equipes);
        btn_gerar_tabela = findViewById(R.id.btn_gerar_tabela);
        btn_add_equipe = findViewById(R.id.btn_nova_equipe);

        Intent intent = getIntent();
        Bundle dados = intent.getExtras();
        if (dados!=null) {
            torneioIndice = dados.getInt("torneio");
            metodoRaiz();
            setTitle(torneio.getNome());
        } else {
            finish();
        }

        btn_add_equipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                montarAlertaNovaEquipe();
            }
        });

        btn_gerar_tabela.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(torneio.isFechado()){
                    abrirTabela();
                } else if (torneio.fecharTorneio(getResources().getStringArray(R.array.partida_nomes))) {
                    atualizar = true;
                    montarAlertaSorteio();
                } else {
                    finish();
                }
            }
        });

        ltv_equipes_torneio.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                abrirEquipe(torneio.getTimes().get(position).getId());
                //montarAlertaAbrirEquipe(torneio.getTimes().get(position).getId());
            }
        });

        ltv_equipes_torneio.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(torneio.isFechado()){
                    CarrierSemiActivity.exemplo(TorneioActivity.this, getString(R.string.msg_alerta_erro_excluir_equipe));
                } else {
                    montarAlertaExcluirEquipe(position);
                }
                return true;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(atualizar){
            CarrierSemiActivity.persistirSantuario(TorneioActivity.this, santuarioOlimpia);
            atualizar = false;
        }
        if(alertaDialog!=null && alertaDialog.isShowing()){
            alertaDialog.dismiss();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        metodoRaiz();
    }

    private void metodoRaiz(){
        santuarioOlimpia = CarrierSemiActivity.carregarSantuario(TorneioActivity.this);
        torneio = santuarioOlimpia.getTorneio(torneioIndice);
        if(torneio != null){
            txv_estado_torneio.setText(torneio.isFechado() ? R.string.estado_fechado : (
                    torneio.getCampeao() == null ? R.string.estado_aberto : R.string.estado_encerrado));
            equipesAdapter = new EquipesAdapter(TorneioActivity.this, torneio.getTimes());
            ltv_equipes_torneio.setAdapter(equipesAdapter);
            listarTimes();
        } else {
            Toast.makeText(TorneioActivity.this, R.string.dados_erro_transitar_em_activity, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void montarAlertaAbrirEquipe(final int posEquipe){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //define o titulo
        builder.setTitle(R.string.title_alerta_confir_abrir_equipe);
        //define a mensagem
        builder.setMessage(R.string.msg_alerta_confir_abrir_equipe);
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

    private void montarAlertaExcluirEquipe(final int posEquipe){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.alerta_default, null);

        Button btn_confirmar = view.findViewById(R.id.btn_confirmar_default);
        Button btn_cancelar = view.findViewById(R.id.btn_cancelar_default);
        TextView msg = view.findViewById(R.id.msg_alerta_default);
        btn_confirmar.setVisibility(View.VISIBLE);
        btn_cancelar.setVisibility(View.VISIBLE);
        msg.setVisibility(View.VISIBLE);
        msg.setText(R.string.msg_alerta_confir_excluir_equipe);

        btn_confirmar.setOnClickListener(arg0 -> {
            alertaDialog.dismiss();
            excluirEquipe(posEquipe);
        });

        btn_cancelar.setOnClickListener(arg0 -> {
            alertaDialog.dismiss();
        });

        builder.setView(view);
        builder.setTitle(R.string.title_alerta_confir_excluir_equipe);
        mostrarAlerta(builder);
    }

    private void montarAlertaNovaEquipe(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.alerta_nova_equipe, null);
        etx_nome_equipe = view.findViewById(R.id.etx_nome_nova_equipe);
        etx_nome_equipe.requestFocus();
        etx_sigla_equipe = view.findViewById(R.id.etx_sigla_nova_equipe);
        btn_confirma_equipe = view.findViewById(R.id.btn_confirmar_equipe);

        //Listeners possíveis do alerta
        view.findViewById(R.id.btn_confirmar_equipe).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                String nome = etx_nome_equipe.getText().toString().trim();
                String sigla = etx_sigla_equipe.getText().toString().trim().toUpperCase();
                if (!nome.isEmpty() && !sigla.isEmpty()) {
                    torneio.addTime(new Equipe(torneio.getNovaEquipeId(), nome, sigla));
                    Toast.makeText(TorneioActivity.this, R.string.equipe_adicionada, Toast.LENGTH_SHORT).show();
                    atualizar = true;
                    listarTimes();
                }
                alertaDialog.dismiss();
            }
        });

        view.findViewById(R.id.btn_cancelar_equipe).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                alertaDialog.dismiss();
            }
        });

        etx_nome_equipe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String nome = etx_nome_equipe.getText().toString().trim();
                String sigla = etx_sigla_equipe.getText().toString().trim().toUpperCase();
                if (nome.isEmpty()) {
                    etx_sigla_equipe.setText("");
                    desativarOKalertaEquipe();
                } else {
                    String sigla_bot;
                    if (nome.contains(" ")) {
                        sigla_bot = siglatation(nome);
                    } else {
                        sigla_bot = nome.substring(0, 1).toUpperCase();
                    }
                    if (!sigla.equals(sigla_bot)) {
                        sigla = sigla_bot;
                        etx_sigla_equipe.setText(sigla);
                    }
                    if(sigla.length()>1 && !torneio.siglaUsada(sigla)){
                        ativarOKalertaEquipe();
                    }else {
                        desativarOKalertaEquipe();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etx_sigla_equipe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String sigla = etx_sigla_equipe.getText().toString();
                if (etx_nome_equipe.getText().toString().isEmpty() ||
                        sigla.length()<=1 || torneio.siglaUsada(sigla)) {
                    desativarOKalertaEquipe();
                } else {
                    ativarOKalertaEquipe();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        builder.setView(view);
        builder.setTitle(R.string.title_alerta_nova_equipe);
        mostrarAlerta(builder);
    }

    private void montarAlertaSorteio(){
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // verificar se a caixa de diálogo está visível
                if (alertaDialog.isShowing()) {
                    // fecha a caixa de diálogo
                    alertaDialog.dismiss();
                }
            }
        };

        handler.postDelayed(runnable, 3000);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.alerta_default, null);
        GifImageView img = view.findViewById(R.id.img_alerta_default);
        img.setVisibility(View.VISIBLE);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
                abrirTabela();
            }
        });
        builder.setView(view);
        builder.setCustomTitle(null);
        builder.setCancelable(false);
        mostrarAlerta(builder, R.color.cor_transparente);
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

    private void ativarOKalertaEquipe() {
        btn_confirma_equipe.setEnabled(true);
        btn_confirma_equipe.setBackground(getDrawable(R.drawable.button_shape_enabled));
    }

    private void desativarOKalertaEquipe() {
        btn_confirma_equipe.setEnabled(false);
        btn_confirma_equipe.setBackground(getDrawable(R.drawable.button_shape_desabled));
    }

    private void ativarAddBtnNovaEquipe() {
        btn_add_equipe.setEnabled(true);
        btn_add_equipe.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.core)));
    }

    private void desativarAddBtnNovaEquipe() {
        btn_add_equipe.setEnabled(false);
        btn_add_equipe.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.background_closed)));
    }

    private void mostrarAlerta(AlertDialog.Builder builder){
        mostrarAlerta(builder, R.drawable.background_alerta);
    }

    private void mostrarAlerta(AlertDialog.Builder builder, int background){
        alertaDialog = builder.create();
        alertaDialog.getWindow().setBackgroundDrawable(getDrawable(background));
        alertaDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertaDialog.show();
    }

    private void listarTimes(){
        if (torneio.getTimes().isEmpty()) {
            ativarAddBtnNovaEquipe();
            txv_equipes_salvas.setText(R.string.torneio_sem_equipes);
        } else {
            if(torneio.getTimes().size()<torneio.MAX_EQUIPE && !torneio.isFechado()){
                ativarAddBtnNovaEquipe();
            } else {
                desativarAddBtnNovaEquipe();
            }
            if(torneio.getTimes().size() < torneio.MIN_EQUIPE) {
                btn_gerar_tabela.setEnabled(false);
                btn_gerar_tabela.setBackground(getDrawable(R.drawable.button_shape_desabled));
                btn_gerar_tabela.setText(R.string.lbl_btn_tabela_desabled);
            } else {
                if(torneio.isFechado()) {
                    btn_gerar_tabela.setText(R.string.lbl_btn_tabela_visualizar);
                } else {
                    btn_gerar_tabela.setText(R.string.lbl_btn_tabela_fechar);
                }
                btn_gerar_tabela.setEnabled(true);
                btn_gerar_tabela.setBackground(getDrawable(R.drawable.button_shape_enabled));
            }
            txv_equipes_salvas.setText(R.string.torneio_com_equipes);
            equipesAdapter.notifyDataSetChanged();
        }
    }

    private void abrirEquipe(int idEquipe) {
        Intent intent = new Intent(getApplicationContext(), EquipeActivity.class);
        Bundle dados = new Bundle();
        //Passa alguns dados para a próxima activity
        dados.putInt("equipe", idEquipe);
        intent.putExtras(dados);
        startActivity(intent);
    }

    private void abrirTabela() {
        Intent intent = new Intent(getApplicationContext(), TabelaActivity.class);
        Bundle dados = new Bundle();
        //Passa alguns dados para a próxima activity
        dados.putInt("torneio", torneioIndice);
        intent.putExtras(dados);
        startActivity(intent);
    }

    private void excluirEquipe(int position) {
        if(torneio.delTime(position) != null){
            atualizar = true;
            listarTimes();
        }
    }
}
