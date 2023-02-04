package com.leoPirpiri.protournamentmanager;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import control.Olimpia;
import model.Torneio;

public class TorneiosSeguidosFragment extends Fragment {

    private Context context;
    private TorneiosAdapter adapterTorneio;
    private RecyclerView recyclerViewTorneiosGerenciados;
    private TextView txv_torneios_recentes;
    private ArrayList<Torneio> listaTorneios;
    private Olimpia santuarioOlimpia;
    private int aux = 1;
    private TextView txv_teste;
    private FirebaseUser nowUser;

    public TorneiosSeguidosFragment() {/* Required empty public constructor*/}

    public TorneiosSeguidosFragment(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_torneios_seguidos, container, false);
        Button btn_teste = v.findViewById(R.id.btn_teste);
        txv_teste = v.findViewById(R.id.txv_teste);
        nowUser = FirebaseAuth.getInstance().getCurrentUser();
        if (nowUser.getEmail().equals("teste@gmail.com")){
            btn_teste.setEnabled(false);
            btn_teste.setVisibility(View.INVISIBLE);
            acionarSnapshot();
        }
        //Listeners
        btn_teste.setOnClickListener(v1 -> {
            enviarValor();
        });

        return v;
    }

    private void acionarSnapshot(){
        FirebaseFirestore.getInstance().collection("/teste")
                .document("tester")
                .addSnapshotListener((value, e) -> {
                    if (value.exists()){
                        txv_teste.setText(value.get("valor").toString());
                    }
                });
    }

    private void enviarValor() {
        Map<String, Object> mapa = new HashMap<>();
        mapa.put("valor", ++aux);
        FirebaseFirestore.getInstance().collection("/teste")
                .document("tester")
                .set(mapa)
                .addOnSuccessListener(onSuccessListener -> {
                    txv_teste.setText(String.valueOf(aux));
                })
                .addOnFailureListener(e -> {
                    txv_teste.setText("Deu Errado");
                });
    }
}

