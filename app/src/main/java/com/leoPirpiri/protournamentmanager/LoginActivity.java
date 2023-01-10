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

    private EditText email;
    private EditText senha;
    private Button btn_login_padrao;
    //private Button btn_login_google;
    private Button btn_simulador_partida;
    private GoogleSignInClient googleSignIn;
    private FirebaseAuth db_auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.etx_email);
        senha = findViewById(R.id.etx_senha);
        btn_login_padrao = findViewById(R.id.btn_login_padrao);
        //btn_login_google = findViewById(R.id.btn_login_google);
        btn_simulador_partida = findViewById(R.id.btn_simulador_tela_login);
        db_auth = FirebaseAuth.getInstance();

        //Listeners

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus==false && !senha.isFocused()) {
                    esconderTeclado(LoginActivity.this, email);
                }
            }
        });
        senha.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus==false && !email.isFocused()) {
                    esconderTeclado(LoginActivity.this, senha);
                }
            }
        });

        btn_login_padrao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(email.getText())){
                    email.setError(getString(R.string.erro_campo_texto_vazio));
                } else if (TextUtils.isEmpty(senha.getText())) {
                    senha.setError(getString(R.string.erro_campo_texto_vazio));
                } else {
                    fazerLogin(email.getText().toString(), email.getText().toString());
                }
            }
        });

        btn_simulador_partida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limparCampos();
                abrirSimulador();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = db_auth.getCurrentUser();
        //updateUI(currentUser);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void login(String email, String senha){
        db_auth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCustomToken:success");
                            FirebaseUser user = db_auth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithCustomToken:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void esconderTeclado(Context context, View editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    private void limparCampos(){
        email.setText("");
        senha.setText("");
    }

    private void abrirSimulador(){
        Intent intent = new Intent(getApplicationContext(), PartidaActivity.class);
        intent.putExtra("partida", -1);
        startActivity(intent);
    }

    private void fazerLogin(String login, String senha) {
    }
}
