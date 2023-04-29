package com.leoPirpiri.protournamentmanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Arrays;

import adapters.NavegacaoPorPaginasAdapter;
import control.CarrierSemiActivity;
import model.Jogador;
import model.Olimpia;
import model.Equipe;
import model.Torneio;

public class EquipeActivity extends AppCompatActivity {

    private AlertDialog alertaDialog;
    private Olimpia santuarioOlimpia;
    private String equipeIndice;
    private Equipe equipe;
    private Torneio torneio;
    private TextView txv_sigla_equipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipe);

        txv_sigla_equipe = findViewById(R.id.txv_sigla_equipe);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        Bundle dados = intent.getExtras();
        if (dados!=null) {
            equipeIndice = dados.getString("equipe");
        }

        findViewById(R.id.btn_edt_equipe).setOnClickListener(v -> montarAlertaEditarEquipe());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!santuarioOlimpia.estaAtualizado()){
            CarrierSemiActivity.persistirSantuario(this, santuarioOlimpia);
            santuarioOlimpia.atualizar(false);
        }
        if(alertaDialog!=null && alertaDialog.isShowing()){
            alertaDialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        metodoRaiz();
        setTabLayout();
    }

    private void erroPassagemParametros(){
        Toast.makeText(this, R.string.dados_erro_transitar_em_activity, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void metodoRaiz() {
        santuarioOlimpia = CarrierSemiActivity.carregarSantuario(EquipeActivity.this);
        santuarioOlimpia.atualizar(false);
        torneio = santuarioOlimpia.getTorneio(Olimpia.extrairUuidTorneioDeIndices(equipeIndice));
        if(torneio != null) {
            equipe = torneio.getEquipe(Olimpia.extrairIdInteiroDeIndices(equipeIndice));
            if(equipe != null) {
                atualizarNomesEquipes();
            } else {
                erroPassagemParametros();
            }
        }  else {
            erroPassagemParametros();
        }
    }

    private void setTabLayout() {
        NavegacaoPorPaginasAdapter adapter = new NavegacaoPorPaginasAdapter(
                this,
                Arrays.asList(new JogadoresDeEquipeFragment(equipe.getJogadores()),
                        new EstatisticasDeEquipeFragment(equipe),
                        new InformacoesFragment(R.string.informacoes_tela_equipe)),
                Arrays.asList(getResources().getStringArray(R.array.tab_bar_tela_equipe_nomes))
        );

        ViewPager2 viewPager = findViewById(R.id.tab_conteudo_equipe);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getItemCount());

        TabLayoutMediator mediator = new TabLayoutMediator(
                findViewById(R.id.tab_bar_equipe),
                viewPager,
                (tab, posicao) -> tab.setText(adapter.getTitulo(posicao)));
        mediator.attach();
    }

    private void atualizarNomesEquipes(){
        setTitle(equipe.getNome());
        txv_sigla_equipe.setText(equipe.getSigla());
    }

    private void montarAlertaEditarEquipe() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.alerta_nova_equipe, null);

        EditText etx_nome_equipe = view.findViewById(R.id.etx_nome_nova_equipe);
        EditText etx_sigla_equipe = view.findViewById(R.id.etx_sigla_nova_equipe);
        Button btn_confirma_equipe = view.findViewById(R.id.btn_confirmar_nova_equipe);
        btn_confirma_equipe.setEnabled(true);
        btn_confirma_equipe.setBackground(ContextCompat.getDrawable(this, R.drawable.button_shape_enabled));
        btn_confirma_equipe.setText(R.string.btn_editar);

        etx_nome_equipe.setText(equipe.getNome());
        etx_nome_equipe.requestFocus();
        etx_sigla_equipe.setText(equipe.getSigla());

        btn_confirma_equipe.setOnClickListener(arg0 -> {
            String nome = etx_nome_equipe.getText().toString().trim();
            String sigla = etx_sigla_equipe.getText().toString().trim().toUpperCase();
            boolean mudou = false;
            if (!nome.isEmpty() && !sigla.isEmpty()) {
                if(!equipe.getNome().equals(nome)) {
                    equipe.setNome(nome);
                    mudou = true;
                }
                if(sigla.length()>1 && !equipe.getSigla().equals(sigla) && torneio.siglaDisponivel(sigla)) {
                    equipe.setSigla(sigla);
                    mudou = true;
                }
                if (mudou){
                    atualizouEquipeDoTorneio();
                } else {
                    CarrierSemiActivity.exemplo(EquipeActivity.this, getString(R.string.erro_atualizar_informacoes_equipe));
                }
                esconderAlerta();
            }
        });

        view.findViewById(R.id.btn_cancelar_equipe).setOnClickListener(arg0 -> alertaDialog.dismiss());

        etx_nome_equipe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String nome = etx_nome_equipe.getText().toString().trim();
                String sigla = etx_sigla_equipe.getText().toString();
                if (nome.isEmpty()) {
                    etx_sigla_equipe.setText("");
                } else {
                    String sigla_bot = nome.contains(" ") ? Equipe.formatarSigla(nome) : nome.substring(0, 1).toUpperCase();
                    if (!sigla.trim().equals(sigla_bot)) {
                        etx_sigla_equipe.setText(sigla_bot);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        builder.setView(view);
        builder.setTitle(R.string.titulo_alerta_nova_equipe);
        mostrarAlerta(builder);
    }

    public ArrayList<Integer> numerosDisponiveisNaEquipe(int numAtual){
        return equipe.buscarNumerosLivresNoPlantel(numAtual);
    }

    public boolean validarParticipacaoAcoesTorneio(int idJogador){
        return torneio.ParticipacaoAcoesTorneio(idJogador);
    }

    public boolean adicionarJogador(String nome, int numero, int posicao){
        return equipe.addJogador(new Jogador(equipe.bucarIdParaNovoJogador(), nome, posicao, numero)) != -1;
    }

    public boolean excluirJogador(int position) {
        Jogador jogador = equipe.getJogadores().get(position);
        return (!torneio.ParticipacaoAcoesTorneio(jogador.getId()) && equipe.delJogador(position)!=null);
    }

    public void mostrarAlerta(AlertDialog.Builder builder) {
        mostrarAlerta(builder, R.drawable.background_alerta);
    }

    public void mostrarAlerta(AlertDialog.Builder builder, int background) {
        alertaDialog = builder.create();
        alertaDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, background));
        alertaDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertaDialog.show();
    }

    public void esconderAlerta(){
        alertaDialog.dismiss();
    }

    public void esconderTeclado(View editText) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    private void atualizouEquipeDoTorneio(){
        atualizarNomesEquipes();
        persistirDados();
    }

    public void persistirDados(){
        torneio.setDataAtualizacaoLocal(System.currentTimeMillis());
        santuarioOlimpia.atualizar(true);
    }
}
