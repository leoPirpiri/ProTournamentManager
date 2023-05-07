package adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.leoPirpiri.protournamentmanager.R;

import java.util.ArrayList;
import java.util.HashMap;

import model.Partida;
import model.Score;
import model.Torneio;

public class PartidasAdapter extends BaseAdapter {
    private final ArrayList<Partida> partidas;
    private final Torneio torneio;
    private final boolean flagLadoEsquerdo;
    private final Context ctx;

    public PartidasAdapter(Context ctx, Torneio torneio, boolean flagLadoEsquerdo) {
        this.ctx = ctx;
        this.torneio = torneio;
        this.partidas = torneio.buscarTabela().buscarPartidasOitavas(flagLadoEsquerdo);
        this.flagLadoEsquerdo = flagLadoEsquerdo;
    }

    @Override
    public int getCount() {
        return partidas.size();
    }

    @Override
    public Partida getItem(int position) {
        return partidas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        if(convertView == null) {
            LayoutInflater inflater = ((Activity) ctx).getLayoutInflater();
            v = inflater.inflate(flagLadoEsquerdo ? R.layout.item_lista_partida8e : R.layout.item_lista_partida8d, null);
        } else {
            v = convertView;
        }
        LinearLayout chave = (LinearLayout) v.findViewById(R.id.layout_chave_oitava);
        TextView mandanteSigla = (TextView) v.findViewById(R.id.adp_homer_nome);
        TextView mandanteScore = (TextView) v.findViewById(R.id.adp_homer_ponto);
        TextView visitanteSigla = (TextView) v.findViewById(R.id.adp_visitor_nome);
        TextView visitanteScore = (TextView) v.findViewById(R.id.adp_visitor_ponto);

        Partida partida = getItem(position);
        if(partida!=null) {
            System.out.println(partida);
            chave.setVisibility(View.VISIBLE);
            mandanteSigla.setText(torneio.buscarEquipe(partida.getMandante()).getSigla());
            visitanteSigla.setText(torneio.buscarEquipe(partida.getVisitante()).getSigla());
            if (partida.estaEncerrada()) {
                HashMap<String, Integer> placar = partida.buscarDetalhesDaPartida();
                mandanteScore.setText(String.valueOf(placar.getOrDefault("Mand_" + Score.TIPO_PONTO, 0)));
                visitanteScore.setText(String.valueOf(placar.getOrDefault("Vist_" + Score.TIPO_PONTO, 0)));
                v.setBackground(ContextCompat.getDrawable(ctx, R.drawable.chave_background_desabled));
            } else {
                v.setBackground(ContextCompat.getDrawable(ctx, R.drawable.chave_background_enabled));
                mandanteScore.setText(R.string.equipe_ponto_partida_aberta);
                visitanteScore.setText(R.string.equipe_ponto_partida_aberta);
            }
            v.setClickable(false);
        } else {
            v.setClickable(true);
        }
        return v;
    }
}
