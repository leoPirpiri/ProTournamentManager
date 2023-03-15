package com.leoPirpiri.protournamentmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import model.Torneio;

public class TorneiosAdapter extends RecyclerView.Adapter<TorneiosAdapter.ViewHolder> implements View.OnClickListener, View.OnLongClickListener {
    private final Context context;
    private final LayoutInflater inflater;
    private final ArrayList<Torneio> torneios;
    private View.OnClickListener quicklistener;
    private View.OnLongClickListener longlistener;

    public TorneiosAdapter(Context context, ArrayList<Torneio> torneios){
        this.context = context;
        this.torneios = torneios;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_lista_torneio, parent, false);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return new ViewHolder(view);
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.quicklistener = listener;
    }
    public void setOnLongClickListener(View.OnLongClickListener listener){
        this.longlistener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String nome = torneios.get(position).getNome();
        String situacao = context.getResources().getStringArray(R.array.torneio_status)[torneios.get(position).pegarStatus()];
        holder.nomeTorneio.setText(nome);
        holder.situacaoTorneio.setText(situacao);
    }

    @Override
    public int getItemCount() {
        return torneios.size();
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
        TextView nomeTorneio, situacaoTorneio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeTorneio = itemView.findViewById(R.id.nome_torneio);
            situacaoTorneio = itemView.findViewById(R.id.situacao_torneio);
        }
    }
}