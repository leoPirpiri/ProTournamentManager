package com.leoPirpiri.protournamentmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import control.CarrierSemiActivity;
import model.Olimpia;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "TELA_LOGIN";

    private EditText etx_email;
    private EditText etx_senha;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth db_auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etx_email = findViewById(R.id.etx_email_login);
        etx_senha = findViewById(R.id.etx_senha_login);
        Button btn_login_padrao = findViewById(R.id.btn_login_padrao);
        SignInButton btn_login_google = findViewById(R.id.btn_login_google);
        Button btn_cadastrar_novo = findViewById(R.id.btn_cadastrar_novo_usuario);
        Button btn_simulador_partida = findViewById(R.id.btn_simulador_tela_login);

        db_auth = FirebaseAuth.getInstance();
        GoogleSignInOptions googleSignInOpt = new GoogleSignInOptions.
                Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestIdToken("103955613869-ptc0ask0u5qbbcqh0cmaf92l45f26cag.apps.googleusercontent.com").
                requestEmail().
                build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOpt);

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
                autenticarUsuarioPadrao(etx_email.getText().toString(), etx_senha.getText().toString());
            }
        });

        btn_login_google.setOnClickListener(v -> signInGoogle());

        btn_cadastrar_novo.setOnClickListener(v -> abrirCadastroNovoUsuario());

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

    ActivityResultLauncher<Intent> abrirJanelaContasGoogle = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Olimpia.printteste(this, String.valueOf(result.getResultCode()));
                if (result.getResultCode() == Activity.RESULT_OK){
                    Olimpia.printteste(this, "Result ok login google");
                    Intent intent = result.getData();
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                    try {
                        GoogleSignInAccount conta = task.getResult(ApiException.class);
                        autenticarUsuarioGoogle(conta.getIdToken());
                    } catch (ApiException ex){
                        Toast.makeText(getApplicationContext(), getString(R.string.erro_login_google_auth),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
    );

    private void autenticarUsuarioPadrao(String email, String senha){
        db_auth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG+"_PADRAO", task.getResult().toString());
                        CarrierSemiActivity.limparSantuarioLocal(this);
                        abrirPrincipal();
                    } else {
                        Log.d(TAG+"_PADRAO", task.getException().getMessage());
                        etx_email.setError(getString(R.string.erro_login_incorreto));
                    }
                });
    }

    private void autenticarUsuarioGoogle(String token) {
        AuthCredential credencial = GoogleAuthProvider.getCredential(token, null);
        db_auth.signInWithCredential(credencial).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d(TAG+"_GOOGLE", task.getResult().toString());
                CarrierSemiActivity.limparSantuarioLocal(this);
                abrirPrincipal();
            } else {
                Log.d(TAG+"_GOOGLE", task.getException().getMessage());
                Toast.makeText(getApplicationContext(), getString(R.string.erro_login_google_auth),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void signInGoogle() {
        Intent intent = googleSignInClient.getSignInIntent();
        abrirJanelaContasGoogle.launch(intent);
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
        startActivity(intent);
    }

    private void abrirPrincipal(){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    private void abrirCadastroNovoUsuario(){
        startActivity(new Intent(getApplicationContext(), CadastroActivity.class));
        finish();
    }

}
