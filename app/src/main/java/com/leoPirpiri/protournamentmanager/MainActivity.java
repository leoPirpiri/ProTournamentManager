package com.leoPirpiri.protournamentmanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;

import adapters.NavegacaoPorPaginasAdapter;
import control.CarrierSemiActivity;
import model.ArvoreTabela;
import model.Equipe;
import model.Olimpia;
import model.Torneio;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MAIN";

    private Olimpia santuarioOlimpia;
    private AlertDialog alertaDialog;
    private FirebaseUser nowUser;
    private FirebaseFirestore  firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nowUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        setContentView(R.layout.activity_principal);
        Button btn_logout_padrao = findViewById(R.id.btn_logout_padrao);

        //Listeners
        btn_logout_padrao.setOnClickListener(v -> efetuarLogout());
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
    protected void onDestroy() {
        super.onDestroy();
        if (nowUser!=null){
            for (Torneio torneio: santuarioOlimpia.getTorneiosGerenciados()) {
                if(torneio.getDataAtualizacaoRemota()!=torneio.getDataAtualizacaoLocal()) {
                    if(CarrierSemiActivity.salvarSantuarioRemoto(torneio, firestore)){
                        santuarioOlimpia.atualizar(true);
                    }
                }
            }
            if(!santuarioOlimpia.estaAtualizado()){
                CarrierSemiActivity.persistirSantuario(this, santuarioOlimpia);
            }
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        teste();
        metodoRaiz();
        setTabLayout();
    }

    private void metodoRaiz(){
        santuarioOlimpia = CarrierSemiActivity.carregarSantuario(this);
        santuarioOlimpia.atualizar(false);
    }

    private void setTabLayout() {
        NavegacaoPorPaginasAdapter adapter = new NavegacaoPorPaginasAdapter(
                this,
                Arrays.asList(new TorneiosGerenciadosFragment(santuarioOlimpia.getTorneiosGerenciados()),
                              new TorneiosSeguidosFragment(santuarioOlimpia.getTorneiosSeguidos()),
                              new TorneiosBuscadosFragment(new ArrayList<>()),
                              new InformacoesFragment(R.string.informacoes_tela_principal)),
                Arrays.asList(getResources().getStringArray(R.array.tab_bar_tela_principal_nomes))
        );

        ViewPager2 viewPager = findViewById(R.id.tab_conteudo_inicial);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getItemCount());

        TabLayoutMediator mediator = new TabLayoutMediator(findViewById(R.id.tab_bar_inicial), viewPager, (tab, posicao) -> tab.setText(adapter.getTitulo(posicao)));
        mediator.attach();
    }

    public Torneio criarNovoTorneio(String nomeNovoTorneio) {
        int idNovoTorneioLocal = santuarioOlimpia.getNovoTorneioId(nowUser.getUid());
        if (idNovoTorneioLocal != 0){
            Torneio novoTorneio = new Torneio(idNovoTorneioLocal, nomeNovoTorneio, nowUser.getUid());
            if (santuarioOlimpia.addTorneioGerenciado(novoTorneio) != -1){
                persistirDados();
                return novoTorneio;
            }
        }
        return null;
    }

    public void mostrarAlerta(AlertDialog.Builder builder) {
        mostrarAlerta(builder, R.drawable.background_alerta);
    }

    public void mostrarAlerta(AlertDialog.Builder builder, int background) {
        alertaDialog = builder.create();
        alertaDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, background));
        alertaDialog.show();
    }

    public void esconderAlerta(){
        alertaDialog.dismiss();
    }

    public void esconderTeclado(View editText) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public void abrirTorneio(String torneioUuid){
        Intent intent = new Intent(this, TorneioActivity.class);
        Bundle dados = new Bundle();
        //Passa alguns dados para a próxima activity
        dados.putString("torneio", torneioUuid);
        intent.putExtras(dados);
        startActivity(intent);
    }

    public boolean excluirTorneio(Torneio torneio) {
        if(torneio.getGerenciadores().contains(nowUser.getUid())){
            if(santuarioOlimpia.delTorneioGerenciado(torneio)){
                persistirDados();
                return true;
            } else {
                return false;
            }
        } else {
            if(santuarioOlimpia.delTorneioSeguido(torneio)){
                persistirDados();
                return true;
            } else {
                return false;
            }
        }
    }

    public void efetuarLogout(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    public String getUsuarioLogado(){
        return nowUser.getUid();
    }

    public void persistirDados(){
        santuarioOlimpia.atualizar(true);
    }

    private void teste(){
        ArvoreTabela teste = new ArvoreTabela();
        ArrayList<Equipe> equipes = new ArrayList<>();
        equipes.add(new Equipe(20100, "Leicam do Maciel de Baixo", "LMB"));
        equipes.add(new Equipe(20200, "Juventude do Pirpiri Futsal", "JPF"));
        equipes.add(new Equipe(20300, "Sítio Escrivão Futsal", "SEF"));
        equipes.add(new Equipe(20400, "Leicam do Maciel de Cima", "LMC"));
        teste.gerarTabela(equipes, new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.partida_nomes))));
        firestore.collection("teste")
            .document("novotest1")
            .set(teste)
            .addOnSuccessListener(aVoid -> {
                Log.d("Teste Aleatorio", "DocumentSnapshot successfully written!");
            })
            .addOnFailureListener(e -> Log.w("Teste Aleatorio", "Error writing document", e));

    }
}
