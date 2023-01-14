package com.leoPirpiri.protournamentmanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText etx_email;
    private EditText etx_senha;
    private Button btn_login_padrao;
    //private Button btn_login_google;
    private Button btn_simulador_partida;
    private GoogleSignInClient googleSignIn;
    private FirebaseAuth db_auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etx_email = findViewById(R.id.etx_email);
        etx_senha = findViewById(R.id.etx_senha);
        btn_login_padrao = findViewById(R.id.btn_login_padrao);
        //btn_login_google = findViewById(R.id.btn_login_google);
        btn_simulador_partida = findViewById(R.id.btn_simulador_tela_login);
        db_auth = FirebaseAuth.getInstance();

        //Listeners

        etx_email.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && !etx_senha.isFocused()) {
                esconderTeclado(LoginActivity.this, etx_email);
            }
        });
        etx_senha.setOnFocusChangeListener((v, hasFocus) ->  {
            if (!hasFocus && !etx_email.isFocused()) {
                esconderTeclado(LoginActivity.this, etx_senha);
            }
        });

        btn_login_padrao.setOnClickListener(v -> {
            if (TextUtils.isEmpty(etx_email.getText())){
                etx_email.setError(getString(R.string.erro_campo_texto_vazio));
            } else if (TextUtils.isEmpty(etx_senha.getText())) {
                etx_senha.setError(getString(R.string.erro_campo_texto_vazio));
            } else {
                autenticarUsuario(etx_email.getText().toString(), etx_senha.getText().toString());
            }
        });

        btn_simulador_partida.setOnClickListener(v -> {
            limparCampos();
            abrirSimulador();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = db_auth.getCurrentUser();
        if (currentUser!=null){
            abrirPrincipal();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void autenticarUsuario(String email, String senha){
        db_auth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        //Log.d(TAG, "signInWithCustomToken:success");
                        FirebaseUser user = db_auth.getCurrentUser();
                        abrirPrincipal();
                    } else {
                        // If sign in fails, display a message to the user.
                        //Log.w(TAG, "signInWithCustomToken:failure", task.getException());
                        etx_email.setError(getString(R.string.erro_login_incorreto));
                        Toast.makeText(getApplicationContext(), getString(R.string.erro_login_auth),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void esconderTeclado(Context context, View editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    private void limparCampos(){
        etx_email.setText("");
        etx_senha.setText("");
    }

    private void abrirSimulador(){
        Intent intent = new Intent(getApplicationContext(), PartidaActivity.class);
        intent.putExtra("partida", -1);
        startActivity(intent);
    }
    private void abrirPrincipal(){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

//    private void fazerLogin(String login, String senha) {
//        autenticarUsuario(login, senha);
//    }
}
