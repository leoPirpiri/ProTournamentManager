package com.leoPirpiri.protournamentmanager;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import control.Olimpia;

public final class CarrierSemiActivity {

    public static void persistirSantuario(Context ctx, Olimpia santuarioOlimpia){
        try {
            santuarioOlimpia.salvarSantuario(santuarioOlimpia,
                    ctx.openFileOutput(santuarioOlimpia.NOME_ARQUIVO_SERIALIZADO, ctx.MODE_PRIVATE));
            Toast.makeText(ctx, R.string.dados_salvando, Toast.LENGTH_LONG).show();
            Toast.makeText(ctx, R.string.dados_salvo, Toast.LENGTH_SHORT).show();
        } catch (IOException ex) {
            //System.out.println(ex.getMessage());//apagar quando der certo.
            Toast.makeText(ctx, R.string.dados_erro_gravar_santuario, Toast.LENGTH_LONG).show();
        }
    }

    public static Olimpia carregarSantuario(Context ctx) {
        Olimpia santuarioOlimpia = new Olimpia();
        if (new File(ctx.getFileStreamPath(Olimpia.NOME_ARQUIVO_SERIALIZADO).toString()).exists()) {
            try {
                santuarioOlimpia = Olimpia.carregarSantuario(ctx.openFileInput(Olimpia.NOME_ARQUIVO_SERIALIZADO));
            } catch (IOException ex) {
                Toast.makeText(ctx, R.string.dados_erro_leitura_santuario, Toast.LENGTH_LONG).show();
            }
        }
        //Remover ao término do projeto.
        if(santuarioOlimpia.getTorneios().isEmpty()) {
            santuarioOlimpia = santuarioOlimpia.testeExemplos(santuarioOlimpia);
            persistirSantuario(ctx, santuarioOlimpia);
        }
        return santuarioOlimpia;
    }

    public static void exemplo(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
    }
}
