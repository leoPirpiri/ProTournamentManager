package com.leoPirpiri.protournamentmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText nome_novo_torneio;
    Button btn_novo_torneio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nome_novo_torneio = findViewById(R.id.nome_novo_torneio);
        btn_novo_torneio = findViewById(R.id.btn_novo_tourneio);

        //Listeners
        nome_novo_torneio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(nome_novo_torneio.getText().toString().equals("")){
                    btn_novo_torneio.setEnabled(false);
                    btn_novo_torneio.setBackground(getDrawable(R.drawable.button_shape_desabled));
                }else{
                    btn_novo_torneio.setEnabled(true);
                    btn_novo_torneio.setBackground(getDrawable(R.drawable.button_shape_enabled));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        nome_novo_torneio.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus==false) {
                    escondeTeclado(MainActivity.this, nome_novo_torneio);
                    Toast.makeText(MainActivity.this, "Perdeu o foco", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "Não perdeu o foco", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_novo_torneio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escondeTeclado(MainActivity.this, nome_novo_torneio);
                Toast.makeText(MainActivity.this, "Apertou o botão", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void escondeTeclado(Context context, View editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

}
