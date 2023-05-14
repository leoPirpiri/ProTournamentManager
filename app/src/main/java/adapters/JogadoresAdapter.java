package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.leoPirpiri.protournamentmanager.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import model.Jogador;
import model.Score;

public class JogadoresAdapter extends RecyclerView.Adapter<JogadoresAdapter.ViewHolder> implements View.OnClickListener, View.OnLongClickListener {

    private final Context context;
    private final LayoutInflater inflater;
    private final ArrayList<Jogador> jogadores;
    private ArrayList<Score> acoesTime;
    private View.OnClickListener quicklistener;
    private View.OnLongClickListener longlistener;

    public JogadoresAdapter(Context ctx, ArrayList<Jogador> jogadores) {
        this(ctx, jogadores, null);
    }

    public JogadoresAdapter(Context ctx, ArrayList<Jogador> jogadores, ArrayList<Score> acoesTime) {
        Collections.sort(jogadores);
        this.context = ctx;
        this.jogadores = jogadores;
        this.acoesTime = acoesTime;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public JogadoresAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_lista_jogador_mini, parent, false);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return new JogadoresAdapter.ViewHolder(view);
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.quicklistener = listener;
    }

    public void setOnLongClickListener(View.OnLongClickListener listener){
        this.longlistener = listener;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onBindViewHolder(@NonNull JogadoresAdapter.ViewHolder holder, int position) {
        Jogador jogador = jogadores.get(position);

        int num = jogador.getNumero();
        holder.numeroJogador.setText(num<10 ? "0"+num : ""+num);

        if (acoesTime == null) {
            holder.posicaoJogador.setText(context.getResources().getStringArray(R.array.posicoes_jogador)[jogador.getPosicao()].substring(0, 3));
            holder.nomeJogador.setText(jogador.getNome());
        } else {
            holder.posicaoJogador.setVisibility(View.GONE);
            holder.nomeJogador.setText(getNomeCompacto(jogador.getNome()));
            holder.nomeJogador.setTextSize(18);
            holder.numeroJogador.setTextSize(18);

            HashMap<Integer, Integer> acoesIndividuais = getAcoesIndividuais(position);
            if (acoesIndividuais.containsKey(Score.TIPO_PONTO)){
                int gols = acoesIndividuais.get(Score.TIPO_PONTO);
                if(gols!=1){
                    holder.pontoProJogador.setText(String.valueOf(gols));
                } else {
                    holder.pontoProJogador.setText("");
                }
                holder.pontoProJogador.setVisibility(View.VISIBLE);
            } else {
                holder.pontoProJogador.setVisibility(View.GONE);
            }
            if (acoesIndividuais.containsKey(Score.TIPO_AUTO_PONTO)){
                int autoGol = acoesIndividuais.get(Score.TIPO_AUTO_PONTO);
                if(autoGol!=1){
                    holder.pontoContraJogador.setText(String.valueOf(autoGol));
                } else {
                    holder.pontoContraJogador.setText("");
                }
                holder.pontoContraJogador.setVisibility(View.VISIBLE);
            } else {
                holder.pontoContraJogador.setVisibility(View.GONE);
            }
            int quantCartoes = acoesIndividuais.getOrDefault(Score.TIPO_AMARELO, 0)
                    + acoesIndividuais.getOrDefault(Score.TIPO_VERMELHO, 0)*2;
            switch (quantCartoes){
                case 0:
                    holder.cartaoJogador.setVisibility(View.GONE);
                    break;
                case 1:
                    holder.cartaoJogador.setBackground(ContextCompat.getDrawable(context, R.drawable.acao_add_card_amarelo));
                    holder.cartaoJogador.setVisibility(View.VISIBLE);
                    break;
                default:
                    holder.cartaoJogador.setBackground(ContextCompat.getDrawable(context, R.drawable.acao_add_card_vermelho));
                    holder.cartaoJogador.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return jogadores.size();
    }

    @Override
    public void onClick(View v) {
        if(quicklistener != null){
            quicklistener.onClick(v);
        }
    }

    public Jogador getItem(int position){
        return jogadores.get(position);
    }

    @Override
    public boolean onLongClick(View v) {
        if(longlistener != null){
            longlistener.onLongClick(v);
        }
        return true;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nomeJogador, numeroJogador, posicaoJogador, cartaoJogador, pontoProJogador, pontoContraJogador;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeJogador = itemView.findViewById(R.id.adp_jogador_nome);
            numeroJogador = itemView.findViewById(R.id.adp_jogador_number);
            posicaoJogador = itemView.findViewById(R.id.adp_jogador_posicao);
            cartaoJogador = itemView.findViewById(R.id.adp_jogador_punicao);
            pontoProJogador = itemView.findViewById(R.id.adp_jogador_gol_pro);
            pontoContraJogador = itemView.findViewById(R.id.adp_jogador_gol_contra);
        }
    }

    public void setAcoesTime(ArrayList<Score> acoesTime) {
        this.acoesTime = acoesTime;
    }

    public HashMap<Integer, Integer> getAcoesIndividuais(int position) {
        HashMap<Integer, Integer> acoes = new HashMap<>();
        for (Score s : acoesTime) {
            if (s.getIdJogador() == jogadores.get(position).getId()){
                acoes.compute(s.getTipo(), (k, v) -> (v == null) ? 1 : ++v);
            }
        }
        return acoes;
    }

    @org.jetbrains.annotations.NotNull
    private String getNomeCompacto(String nomeBruto){
        ArrayList<String> pulos = new ArrayList<>(Arrays.asList("da", "do", "de", "das", "dos"));
        int limite = 10;
        int margemErro = limite+5;
        String nome = "";
        for(String s : nomeBruto.split(" ")){
            if(!pulos.contains(s.toLowerCase())){
                if(nome.length() + s.length() <= limite){
                    nome = nome.concat(s.concat(" "));
                } else if (nome.length() + s.length() <= margemErro){
                    nome = nome.concat(s);
                    break;
                } else {
                    if(nome.isEmpty()) nome=s;
                    break;
                }
            }
        }
        return nome.trim();
    }
}
