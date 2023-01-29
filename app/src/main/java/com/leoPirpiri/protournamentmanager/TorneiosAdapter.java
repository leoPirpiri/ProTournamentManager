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

public class TorneiosAdapter extends RecyclerView.Adapter<TorneiosAdapter.ViewHolder> implements View.OnClickListener {
    private final Context context;
    private final LayoutInflater inflater;
    private ArrayList<Torneio> torneios;
    private View.OnClickListener listener;

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
        return new ViewHolder(view);
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String nome = torneios.get(position).getNome();
        String situacao = context.getResources().getStringArray(R.array.torneio_status)[torneios.get(position).getStatus()];
        holder.nomeTorneio.setText(nome);
        holder.situacaoTorneio.setText(situacao);
    }

    @Override
    public int getItemCount() {
        return torneios.size();
    }

    @Override
    public void onClick(View v) {
        if(listener!=null){
            listener.onClick(v);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nomeTorneio, situacaoTorneio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeTorneio = itemView.findViewById(R.id.nome_torneio);
            situacaoTorneio = itemView.findViewById(R.id.situacao_torneio);
        }
    }
}

    /*@Override
    public int getCount() {
        return torneios.size();
    }

    @Override
    public Torneio getItem(int position) {
        return torneios.get(position);
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
            v = inflater.inflate(R.layout.item_lista_torneio, null);
        } else {
            v = convertView;
        }
        Torneio torneio = getItem(position);
        TextView itemNome = (TextView) v.findViewById(R.id.nome_torneio);
        TextView itemEstado = (TextView) v.findViewById(R.id.situacao_torneio);

        itemNome.setText(torneio.getNome());
        itemEstado.setText(torneio.isFechado() ? R.string.estado_fechado : (
                torneio.getCampeao() == null ? R.string.estado_aberto : R.string.estado_encerrado));
        return v;
    }*/
