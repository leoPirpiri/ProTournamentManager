package com.leoPirpiri.protournamentmanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

import control.Olimpia;
import model.Torneio;

public class MainActivity extends AppCompatActivity {

    private Olimpia santuarioOlimpia;
    private AlertDialog alertaDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        Button btn_logout_padrao = findViewById(R.id.btn_logout_padrao);

        metodoRaiz();
        setTabLayout();
//Listeners
        btn_logout_padrao.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });
    }

    private void metodoRaiz(){
        //Carrega ou inicia o santuário onde ocorre os jogos.
        santuarioOlimpia = CarrierSemiActivity.carregarSantuario(this);
    }

    private void setTabLayout() {
        NavegacaoPorPaginasAdapter adapter = new NavegacaoPorPaginasAdapter(
                this,
                Arrays.asList(new TorneiosGerenciadosFragment(santuarioOlimpia.getTorneios()),
                              new TorneiosSeguidosFragment(santuarioOlimpia.getTorneios()),
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
        int idNovoTorneio = santuarioOlimpia.getNovoTorneioId();
        if (idNovoTorneio != 0){
            Torneio novoTorneio = new Torneio(idNovoTorneio, nomeNovoTorneio);
            if (santuarioOlimpia.addTorneio(novoTorneio) != -1){
                CarrierSemiActivity.persistirSantuario(this, santuarioOlimpia);
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

    public void abrirTorneio(int torneioId){
        Intent intent = new Intent(this, TorneioActivity.class);
        Bundle dados = new Bundle();
        //Passa alguns dados para a próxima activity
        dados.putInt("torneio", torneioId);
        intent.putExtras(dados);
        startActivity(intent);
    }

    public boolean excluirTorneio(int posicaoTorneio) {
        if(santuarioOlimpia.delTorneio(posicaoTorneio) != null){
            CarrierSemiActivity.persistirSantuario(this, santuarioOlimpia);
            return true;
        }
        return false;
    }
}
