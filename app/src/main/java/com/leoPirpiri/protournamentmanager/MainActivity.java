package com.leoPirpiri.protournamentmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        setTabLayout();

        Button btn_logout_padrao = findViewById(R.id.btn_logout_padrao);

//Listeners
        btn_logout_padrao.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });
    }

    private void setTabLayout() {
        NavegacaoPorPaginasAdapter adapter = new NavegacaoPorPaginasAdapter(
                this,
                Arrays.asList(new TorneiosGerenciadosFragment(this),
                              new TorneiosSeguidosFragment(this),
                              new InformacoesFragment(R.string.informacoes_tela_principal)),
                Arrays.asList(getResources().getStringArray(R.array.tab_bar_tela_principal_nomes))
        );

        ViewPager2 viewPager = findViewById(R.id.tab_conteudo_inicial);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getItemCount());

        TabLayoutMediator mediator = new TabLayoutMediator(findViewById(R.id.tab_bar_inicial), viewPager, (tab, posicao) -> tab.setText(adapter.getTitulo(posicao)));
        mediator.attach();
    }

}
