package com.leoPirpiri.protournamentmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CadastroActivity extends AppCompatActivity {
    private EditText etx_usuario, etx_email, etx_senha, etx_sede;
    private Button btn_cadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        etx_usuario = findViewById(R.id.etx_apelidio);
        etx_email = findViewById(R.id.etx_email_cadastro);
        etx_senha = findViewById(R.id.etx_senha_cadastro);
        etx_sede = findViewById(R.id.etx_sede);
        btn_cadastrar = findViewById(R.id.btn_cadastrar_padrao);

        btn_cadastrar.setOnClickListener(v -> {
            if (TextUtils.isEmpty(etx_usuario.getText())){
                etx_usuario.setError(getString(R.string.erro_campo_texto_vazio));
            } else if (TextUtils.isEmpty(etx_email.getText())) {
                etx_email.setError(getString(R.string.erro_campo_texto_vazio));
            } else if (TextUtils.isEmpty(etx_senha.getText())) {
                etx_senha.setError(getString(R.string.erro_campo_texto_vazio));
            } else {
                cadastrarUsuario(etx_usuario.getText().toString(),
                                          etx_email.getText().toString(),
                                          etx_senha.getText().toString(),
                                          etx_sede.getText().toString());
            }
        });


    }

    private void cadastrarUsuario(String usuario, String email, String senha, String sede) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, task -> {

                })
                .addOnFailureListener(this, exception -> {

        });
    }
}