package com.leoPirpiri.protournamentmanager;

import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.leoPirpiri.protournamentmanager.R.drawable;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import adapters.NavegacaoPorPaginasAdapter;
import control.CarrierSemiActivity;
import model.Equipe;
import model.Olimpia;
import model.Torneio;
import pl.droidsonroids.gif.GifImageView;

public class TorneioActivity extends AppCompatActivity {
    private final String TAG = "TORNEIO_ACTIVITY";

    private Olimpia santuarioOlimpia;
    private AlertDialog alertaDialog;
    private TextView txv_estado_torneio;
    private Button btn_gerar_tabela;

    private String torneioIndice;
    private Torneio torneio;
    private FirebaseUser nowUser;
    private FirebaseFirestore  firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nowUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

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
            //torneio.getEquipes().add(new Equipe(torneio.pegarIdParaNovaEquipe(), "Equipe teste "+new Random().nextInt(50), "A"+new Random().nextInt(99)));
            if(torneio.estarFechado()){
                abrirTabela();
            } else if (torneio.fecharTorneio(getResources().getStringArray(R.array.partida_nomes))) {
                persistirDadosSantuario();
                salvarPartidasRemotas();
                montarAlertaSorteio();
            } else {
                finish();
            }
        });
    }

    private void salvarPartidasRemotas() {
        WriteBatch batch = firestore.batch();
        torneio.buscarTabela().getPartidas().forEach((key, partida) -> {
            batch.set(firestore.collection("torneios").document(torneio.buscarUuid())
                            .collection("partidas").document(String.valueOf(key)),
                    partida);
        });
        batch.commit().addOnCompleteListener(task -> {
            if (!task.isSuccessful()){
                Toast.makeText(this, R.string.erro_grave_padrao, Toast.LENGTH_LONG).show();
                finish();
            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();
        metodoRaiz();
        setTabLayout();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!santuarioOlimpia.estaAtualizado()){
            CarrierSemiActivity.persistirSantuario(this, santuarioOlimpia);
            santuarioOlimpia.atualizar(false);
        }
        if(alertaDialog!=null){
            esconderAlerta();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("Destruindo Atividade.");
    }

    private void metodoRaiz(){
        santuarioOlimpia = CarrierSemiActivity.carregarSantuario(TorneioActivity.this);
        torneio = santuarioOlimpia.getTorneio(torneioIndice);
        santuarioOlimpia.atualizar(false);
        if(torneio != null){
            atualizarInformacoesInciais();
            //montarAlertaBuscarTorneioRemoto();
        } else {
            torneio = new Torneio(-1, "", getUsuarioLogado());
            montarAlertaBuscarTorneioRemoto();
        }
    }

    private void atualizarInformacoesInciais(){
        setTitle(torneio.getNome());
        txv_estado_torneio.setText(getResources().getStringArray(R.array.torneio_status)[torneio.pegarStatus()+1]);
        atualizarBotaoTabela();
    }

    private void setTabLayout() {
        List<Fragment> fragmentosList = new ArrayList<>(Arrays.asList(new EquipesDeTorneioFragment(torneio),
                                                      new EstatisticasDeTorneioFragment(torneio),
                                                      new InformacoesFragment(R.string.informacoes_tela_torneio)));
        List<String> titulosDosFragmentosList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.tab_bar_tela_torneio_nomes)));

        if (getUsuarioLogado().equals(torneio.buscarDonoDoTorneio())){
            fragmentosList.add(2, new GerenciarMesariosDeTorneioFragment(torneio));
            titulosDosFragmentosList.add(2, getString(R.string.tab_bar_tela_torneio_nomes_fragmento_mesario));
        }
        NavegacaoPorPaginasAdapter adapter = new NavegacaoPorPaginasAdapter(this, fragmentosList, titulosDosFragmentosList);

        ViewPager2 viewPager = findViewById(R.id.tab_conteudo_torneio);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getItemCount());

        TabLayoutMediator mediator = new TabLayoutMediator(
                                            findViewById(R.id.tab_bar_torneio),
                                            viewPager,
                                            (tab, posicao) -> tab.setText(adapter.getTitulo(posicao)));
        mediator.attach();
    }

    private void montarAlertaBuscarTorneioRemoto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.alerta_default, null);
        GifImageView img = view.findViewById(R.id.img_alerta_default);
        img.setImageResource(drawable.load);
        img.setVisibility(View.VISIBLE);
        builder.setView(view);
        builder.setCancelable(false);
        builder.setTitle(R.string.titulo_alerta_loading_torneio_remoto);
        mostrarAlerta(builder);
        buscarTorneioRemoto();
    }

    private void montarAlertaSorteio(){
        final Handler handler = new Handler();
        // verificar se existe caixa de diálogo visível e fecha a caixa de diálogo
        final Runnable runnable = this::esconderAlerta;

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
        if (alertaDialog.isShowing()) alertaDialog.dismiss();
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

    public boolean excluirEquipe(int posicao) {
        if(!torneio.estarFechado() && torneio.excluirEquipe(posicao)!=null){
            atualizouTorneio();
            return true;
        }
        return false;
    }

    private void atualizouTorneio(){
        atualizarBotaoTabela();
        persistirDadosSantuario();
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

    public String getUsuarioLogado(){
        return nowUser.getUid();
    }

    public boolean torneioFechado(){ return torneio.estarFechado(); }

    public void persistirDadosSantuario(){
        torneio.setDataAtualizacaoLocal(System.currentTimeMillis());
        santuarioOlimpia.atualizar(true);
    }

    private void buscarTorneioRemoto(){
        firestore.collection("torneios").
                document(torneioIndice).get().
                addOnCompleteListener(task -> {
                    esconderAlerta();
                    if(task.isSuccessful()){
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()){
                            Torneio torneioRemoto = doc.toObject(Torneio.class);
                            torneioEncontrado(torneioRemoto);
                        } else {
                            Toast.makeText(this, R.string.erro_torneio_nao_encontrado, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        Log.d(TAG, task.getResult().toString());
                    } else {
                        Log.d(TAG, task.getResult().toString());
                        finish();
                    }
                });
    }

    private void torneioEncontrado(Torneio torneioRemoto) {
        if(torneioRemoto.getGerenciadores().contains(getUsuarioLogado())){
            System.out.println("Torneio Gerenciado");
        } else {
            System.out.println("Torneio Seguido");
            if(torneioRemoto.getDataAtualizacaoRemota() != torneio.getDataAtualizacaoRemota()){
                santuarioOlimpia.delTorneioSeguido(torneio);
                torneio = torneioRemoto;
                santuarioOlimpia.addTorneioSeguido(torneioRemoto);
                persistirDadosSantuario();
                atualizarInformacoesInciais();
                setTabLayout();
            }
        }
    }

}
