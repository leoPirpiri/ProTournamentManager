package com.leoPirpiri.protournamentmanager;

import com.google.android.material.tabs.TabLayoutMediator;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

import control.CarrierSemiActivity;
import model.Equipe;
import model.Olimpia;
import model.Torneio;
import pl.droidsonroids.gif.GifImageView;

public class TorneioActivity extends AppCompatActivity {
    private String torneioIndice;

    private AlertDialog alertaDialog;
    private Olimpia santuarioOlimpia;
    private Torneio torneio;
    private TextView txv_estado_torneio;
    private Button btn_gerar_tabela;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_torneio);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txv_estado_torneio = findViewById(R.id.txv_estado_torneio);
        btn_gerar_tabela = findViewById(R.id.btn_gerar_tabela);

        Intent intent = getIntent();
        Bundle dados = intent.getExtras();
        if (dados!=null) {
            torneioIndice = dados.getString("torneio");
        } else {
            finish();
        }

        btn_gerar_tabela.setOnClickListener(v -> {
            if(torneio.estarFechado()){
                abrirTabela();
            } else if (torneio.fecharTorneio(getResources().getStringArray(R.array.partida_nomes))) {
                persistirDados();
                montarAlertaSorteio();
            } else {
                finish();
            }
        });
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
    protected void onResume(){
        super.onResume();
        metodoRaiz();
        setTabLayout();
    }

    private void metodoRaiz(){
        santuarioOlimpia = CarrierSemiActivity.carregarSantuario(TorneioActivity.this);
        torneio = santuarioOlimpia.getTorneioGerenciado(torneioIndice);
        santuarioOlimpia.atualizar(false);

        if(torneio != null){
            setTitle(torneio.getNome());
            txv_estado_torneio.setText(getResources().getStringArray(R.array.torneio_status)[torneio.pegarStatus()]);
            atualizarBotaoTabela();
        } else {
            Toast.makeText(TorneioActivity.this, R.string.dados_erro_transitar_em_activity, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setTabLayout() {
        NavegacaoPorPaginasAdapter adapter = new NavegacaoPorPaginasAdapter(
                this,
                Arrays.asList(new EquipesDeTorneioFragment(torneio.getEquipes()),
                        new EstatisticasDeTorneioFragment(torneio),
                        new InformacoesFragment(R.string.informacoes_tela_torneio)),
                Arrays.asList(getResources().getStringArray(R.array.tab_bar_tela_torneio_nomes))
        );

        ViewPager2 viewPager = findViewById(R.id.tab_conteudo_torneio);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getItemCount());

        TabLayoutMediator mediator = new TabLayoutMediator(
                                            findViewById(R.id.tab_bar_torneio),
                                            viewPager,
                                            (tab, posicao) -> tab.setText(adapter.getTitulo(posicao)));
        mediator.attach();
    }
    /*private void montarAlertaAbrirEquipe(final int posEquipe){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //define o titulo
        builder.setTitle(R.string.titulo_alerta_confir_abrir_equipe);
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
        builder.setTitle(R.string.titulo_alerta_confir_excluir_equipe);
        mostrarAlerta(builder);
    }

*/
    private void montarAlertaSorteio(){
        final Handler handler = new Handler();
        final Runnable runnable = () -> {
            // verificar se a caixa de diálogo está visível
            if (alertaDialog.isShowing()) {
                // fecha a caixa de diálogo
                alertaDialog.dismiss();
            }
        };

        handler.postDelayed(runnable, 3000);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.alerta_default, null);
        GifImageView img = view.findViewById(R.id.img_alerta_default);
        img.setVisibility(View.VISIBLE);
        builder.setOnDismissListener(dialog -> {
            handler.removeCallbacks(runnable);
            abrirTabela();
        });
        builder.setView(view);
        builder.setCustomTitle(null);
        builder.setCancelable(false);
        mostrarAlerta(builder, R.color.cor_transparente);
    }

    public void mostrarAlerta(AlertDialog.Builder builder){
        mostrarAlerta(builder, R.drawable.background_alerta);
    }

    public void mostrarAlerta(AlertDialog.Builder builder, int background){
        alertaDialog = builder.create();
        alertaDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, background));
        alertaDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertaDialog.show();
    }

    public void abrirEquipe(int idEquipe){
        Bundle dados = new Bundle();
        dados.putString("equipe", torneio.buscarDonoDoTorneio() + idEquipe);
        abrirTela(dados, EquipeActivity.class);
    }

    private void abrirTabela() {
        Bundle dados = new Bundle();
        dados.putString("torneio", torneio.buscarUuid());
        abrirTela(dados, TabelaActivity.class);
    }

    private void abrirTela(Bundle dados, Class<?> classe){
        Intent intent = new Intent(getApplicationContext(), classe);
        intent.putExtras(dados);
        startActivity(intent);
    }

    public void esconderTeclado(View editText) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public void esconderAlerta(){
        alertaDialog.dismiss();
    }

    public boolean adicionarEquipe(String nome, String sigla){
        int idNovaEquipe = torneio.pegarIdParaNovaEquipe();
        if (idNovaEquipe==0) {
            Toast.makeText(this, R.string.erro_grave_padrao, Toast.LENGTH_SHORT).show();
            finish();
        } else if (torneio.estarCheio()){
            String msg = getString(R.string.erro_adicionar_equipe) +"\n"+ getString(R.string.torneio_cheio);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        } else if (torneio.estarFechado()){
            String msg = getString(R.string.erro_adicionar_equipe) +"\n"+ getString(R.string.torneio_fechado);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        } else if (!torneio.siglaDisponivel(sigla)){
            Toast.makeText(this, R.string.msg_alerta_erro_sigla_usada, Toast.LENGTH_SHORT).show();
        } else if (!torneio.addEquipe(new Equipe(idNovaEquipe, nome, sigla))) {
            Toast.makeText(this, R.string.erro_adicionar_equipe, Toast.LENGTH_SHORT).show();
        } else {
            atualizouTorneio();
            return true;
        }
        return false;
    }

    public boolean excluirEquipe() {
        atualizarBotaoTabela();
        return false;
    }

    private void atualizarBotaoTabela(){
        if(torneio.getEquipes().size() < Torneio.MIN_EQUIPE) {
            btn_gerar_tabela.setEnabled(false);
            btn_gerar_tabela.setBackground(ContextCompat.getDrawable(this, R.drawable.button_shape_desabled));
            btn_gerar_tabela.setText(R.string.lbl_btn_tabela_desabled);
        } else {
            if(torneio.estarFechado()) {
                btn_gerar_tabela.setText(R.string.lbl_btn_tabela_visualizar);
            } else {
                btn_gerar_tabela.setText(R.string.lbl_btn_tabela_fechar);
            }
            btn_gerar_tabela.setEnabled(true);
            btn_gerar_tabela.setBackground(ContextCompat.getDrawable(this, R.drawable.button_shape_enabled));
        }
    }

    private void atualizouTorneio(){
        atualizarBotaoTabela();
        torneio.setDataAtualizacaoLocal(System.currentTimeMillis());
        persistirDados();
    }

    public int estadoDoTorneio(){
        return torneio.pegarStatus();
    }

    public void persistirDados(){
        santuarioOlimpia.atualizar(true);
    }
}
