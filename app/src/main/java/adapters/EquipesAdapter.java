package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.leoPirpiri.protournamentmanager.R;

import java.util.ArrayList;

import model.Equipe;

public class EquipesAdapter extends RecyclerView.Adapter<EquipesAdapter.ViewHolder> implements View.OnClickListener, View.OnLongClickListener {
    private final Context context;
    private final LayoutInflater inflater;
    private final ArrayList<Equipe> equipes;
    private View.OnClickListener quicklistener;
    private View.OnLongClickListener longlistener;

    public EquipesAdapter(Context context, ArrayList<Equipe> equipes) {
        this.context = context;
        this.equipes = equipes;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public EquipesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_lista_equipe, parent, false);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return new EquipesAdapter.ViewHolder(view);
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.quicklistener = listener;
    }
    public void setOnLongClickListener(View.OnLongClickListener listener){
        this.longlistener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull EquipesAdapter.ViewHolder holder, int position) {
        String nome = equipes.get(position).getNome() + " - " +
                      equipes.get(position).getSigla();
        String quantidade = context.getResources().getString(R.string.lbl_quant_jogadores) + ": " +
                            equipes.get(position).buscarTamanhoEquipe();
        holder.nomeEquipe.setText(nome);
        holder.quantidadeDeJogadores.setText(quantidade);
    }

    @Override
    public int getItemCount() {
        return equipes.size();
    }

    @Override
    public void onClick(View v) {
        if(quicklistener != null){
            quicklistener.onClick(v);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if(longlistener != null){
            longlistener.onLongClick(v);
        }
        return true;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nomeEquipe, quantidadeDeJogadores;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeEquipe = itemView.findViewById(R.id.adp_equipe_nome);
            quantidadeDeJogadores = itemView.findViewById(R.id.adp_equipe_quant);
        }
    }

    /*@Override
    public int getCount() {
        return equipes.size();
    }

    @Override
    public Equipe getItem(int position) {
        return equipes.get(position);
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
            v = inflater.inflate(R.layout.item_lista_equipe, null);
        } else {
            v = convertView;
        }
        Equipe equipe = getItem(position);
        TextView itemNome = (TextView) v.findViewById(R.id.adp_equipe_nome);
        TextView itemQjogadores = (TextView) v.findViewById(R.id.adp_equipe_quant);

        itemNome.setText(equipe.getNome()+" - "+equipe.getSigla());
        String label = ctx.getResources().getString(R.string.dica_quant_jogadores);
        itemQjogadores.setText( label +": "+ Integer.toString(equipe.getTamanhoEquipe()));
        return v;
    }*/
}
