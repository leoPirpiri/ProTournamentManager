package com.leoPirpiri.protournamentmanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import model.Usuario;

public class CadastroActivity extends AppCompatActivity {
    private EditText etx_usuario, etx_email, etx_senha, etx_sede;
    private AlertDialog alertaDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        etx_usuario = findViewById(R.id.etx_apelidio);
        etx_email = findViewById(R.id.etx_email_cadastro);
        etx_senha = findViewById(R.id.etx_senha_cadastro);
        etx_sede = findViewById(R.id.etx_sede);
        Button btn_cadastrar = findViewById(R.id.btn_cadastrar_padrao);

        btn_cadastrar.setOnClickListener(v -> {
            if (TextUtils.isEmpty(etx_usuario.getText())){
                etx_usuario.setError(getString(R.string.erro_campo_texto_vazio));
            } else if (TextUtils.isEmpty(etx_email.getText())) {
                etx_email.setError(getString(R.string.erro_campo_texto_vazio));
            } else if (TextUtils.isEmpty(etx_senha.getText())) {
                etx_senha.setError(getString(R.string.erro_campo_texto_vazio));
            } else {
                criarUsuarioEmailSenha(etx_usuario.getText().toString(),
                                          etx_email.getText().toString(),
                                          etx_senha.getText().toString(),
                                          etx_sede.getText().toString());
            }
        });


    }

    private void criarUsuarioEmailSenha(String usuario, String email, String senha, String sede) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        cadastrarUsuario(usuario);
                    }
                })
                .addOnFailureListener(this, exception -> montarAlertaFalhaCriarUsuario());
    }

    private void cadastrarUsuario(String usuario) {
        String uId = FirebaseAuth.getInstance().getUid();
        Usuario user = new Usuario(uId, usuario);
        FirebaseFirestore.getInstance().collection("usuarios")
                .document(uId)
                .set(user)
                .addOnSuccessListener(this, task -> {
                    Toast.makeText(getApplicationContext(), "cadastrou. eba",
                            Toast.LENGTH_LONG).show();
                    limparCampos();
                    abrirPrincipal();
                })
                .addOnFailureListener(this, exception -> montarAlertaFalhaCriarUsuario());
    }
    private void montarAlertaFalhaCriarUsuario(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.alerta_default, null);

        Button btn_confirmar = view.findViewById(R.id.btn_confirmar_default);
        TextView msg = view.findViewById(R.id.msg_alerta_default);
        btn_confirmar.setVisibility(View.VISIBLE);
        msg.setVisibility(View.VISIBLE);
        msg.setText(R.string.msg_alerta_confir_excluir_torneio);

        btn_confirmar.setOnClickListener(arg0 -> alertaDialog.dismiss());

        builder.setView(view);
        builder.setTitle(R.string.titulo_alerta_erro_cadastro);
        mostrarAlerta(builder);
    }

    private void mostrarAlerta(AlertDialog.Builder builder) {
        mostrarAlerta(builder, R.drawable.background_alerta);
    }

    private void mostrarAlerta(AlertDialog.Builder builder, int background) {
        alertaDialog = builder.create();
        alertaDialog.getWindow().setBackgroundDrawable(getDrawable(background));
        alertaDialog.show();
    }
    private void limparCampos(){
        etx_usuario.setText("");
        etx_email.setText("");
        etx_senha.setText("");
    }

    private void abrirPrincipal(){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}