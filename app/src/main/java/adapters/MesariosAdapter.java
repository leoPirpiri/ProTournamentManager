package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.leoPirpiri.protournamentmanager.R;

import java.util.ArrayList;

import model.Torneio;
import model.Usuario;

public class MesariosAdapter extends RecyclerView.Adapter<MesariosAdapter.ViewHolder> implements View.OnClickListener, View.OnLongClickListener {
    private final Context context;
    private final LayoutInflater inflater;
    private final ArrayList<Usuario> mesarios;
    private View.OnClickListener quicklistener;
    private View.OnLongClickListener longlistener;

    public MesariosAdapter(Context context, ArrayList<Usuario> mesarios){
        this.context = context;
        this.mesarios = mesarios;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_lista_mesario, parent, false);
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
        String nome = mesarios.get(position).getApelido();
        String email = mesarios.get(position).getId();
        holder.nomeMesario.setText(nome);
        holder.emailMesario.setText(email);
    }

    @Override
    public int getItemCount() {
        return mesarios.size();
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
        TextView nomeMesario, emailMesario;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeMesario = itemView.findViewById(R.id.apelido_mesario);
            emailMesario = itemView.findViewById(R.id.email_mesario);
        }
    }
}