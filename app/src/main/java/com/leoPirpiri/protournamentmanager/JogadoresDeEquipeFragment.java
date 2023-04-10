package com.leoPirpiri.protournamentmanager;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import model.Jogador;

public class JogadoresDeEquipeFragment extends Fragment {

    private EquipeActivity superActivity;
    private ArrayList<Jogador> listaJogadores;
    private RecyclerView recyclerViewJogadoresDeEquipe;
    private JogadoresAdapter adapterJogadores;
    private TextView txv_info_lista_jogadores;

    public JogadoresDeEquipeFragment() {
        // Required empty public constructor
    }

    public JogadoresDeEquipeFragment(ArrayList<Jogador> jogadores) {
        this.listaJogadores = jogadores;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.superActivity = (EquipeActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_jogadores_equipe, container, false);
//
        recyclerViewJogadoresDeEquipe = v.findViewById(R.id.recyclerview_lista_jogadores);
        txv_info_lista_jogadores = v.findViewById(R.id.txv_msg_jogadores_salvos);
        FloatingActionButton btn_novo_jogador = v.findViewById(R.id.btn_novo_jogador);

        listarJogadores();
        povoarRecycleView();
        //Listeners
        btn_novo_jogador.setOnClickListener(view -> montarAlertaNovoEditarJogador(null, listaJogadores.size()));
        return v;
    }
    private void listarJogadores() {
        if (listaJogadores.isEmpty()) {
            txv_info_lista_jogadores.setVisibility(View.VISIBLE);
        } else {
            txv_info_lista_jogadores.setVisibility(View.GONE);
        }
    }

    private void povoarRecycleView(){
        recyclerViewJogadoresDeEquipe.setLayoutManager(new LinearLayoutManager(superActivity));
        adapterJogadores = new JogadoresAdapter(superActivity, listaJogadores);
        recyclerViewJogadoresDeEquipe.setAdapter(adapterJogadores);
        construirListenersAdapterJogadores();
    }

    private void construirListenersAdapterJogadores() {
        /*adapterJogadores.setOnClickListener(v -> abrirEquipe(listaEquipes.get(recyclerViewEquipesDoTorneio.getChildAdapterPosition(v)).getId()));

        adapterJogadores.setOnLongClickListener(v -> {
            Olimpia.printteste(context, "Clicou no Longo");
            //montarAlertaExcluirEquipe(recyclerViewEquipesDoTorneio.getChildAdapterPosition(v));
            return true;
        });*/
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void montarAlertaNovoEditarJogador(Jogador velhoJogador, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(superActivity);
        View view = getLayoutInflater().inflate(R.layout.alerta_novo_jogador, null);
        EditText etx_nome_jogador = view.findViewById(R.id.etx_nome_novo_jogador);
        Button  btn_confirma_jogador = view.findViewById(R.id.btn_confirmar_jogador);
        Spinner spnr_posicao = view.findViewById(R.id.spr_pos_novo_jogador);
        Spinner spnr_numero = view.findViewById(R.id.spr_num_novo_jogador);
        spnr_posicao.setAdapter(new ArrayAdapter<>(superActivity, R.layout.spinner_item_style, getResources().getStringArray(R.array.posicoes_jogador)));
        ArrayList<Integer> numerosDisponiveis;

        if (velhoJogador == null) {
            numerosDisponiveis = superActivity.numerosDisponiveisNaEquipe(-1);
            spnr_posicao.setSelection(0);
        } else {
            numerosDisponiveis = superActivity.numerosDisponiveisNaEquipe(velhoJogador.getNumero());
            btn_confirma_jogador.setText(R.string.btn_editar);
            etx_nome_jogador.setText(velhoJogador.getNome());
            spnr_posicao.setSelection(velhoJogador.getPosicao());
            if(!superActivity.validarParticipacaoAcoesTorneio(velhoJogador.getId())){
                view.findViewById(R.id.btn_del_jogador).setVisibility(View.VISIBLE);
            }
        }
        etx_nome_jogador.requestFocus();
        spnr_numero.setAdapter(new ArrayAdapter<>(superActivity, R.layout.spinner_item_style, numerosDisponiveis));
        spnr_numero.setSelection(0);

        //Listeners possíveis do alerta
        etx_nome_jogador.setOnFocusChangeListener((v1, hasFocus) -> {
            if (!hasFocus) {
                superActivity.esconderTeclado(etx_nome_jogador);
            }
        });

        btn_confirma_jogador.setOnClickListener(arg0 -> {
            String nome = etx_nome_jogador.getText().toString().trim();
            int numero = Integer.parseInt(spnr_numero.getSelectedItem().toString());
            int posicao = spnr_posicao.getSelectedItemPosition();

            if(nome.isEmpty()){
                etx_nome_jogador.setError(getString(R.string.erro_campo_texto_vazio));
            } else {
                if(velhoJogador==null) {
                    if(superActivity.adicionarJogador(nome, numero, posicao)){
                        Toast.makeText(superActivity, R.string.jogador_adicionado, Toast.LENGTH_SHORT).show();
                        superActivity.persistirDados();
                        adapterJogadores.notifyItemInserted(position);
                        listarJogadores();
                    } else {
                        Toast.makeText(superActivity, R.string.jogador_erro_adicionar, Toast.LENGTH_SHORT).show();
                    }
                } else if (!velhoJogador.getNome().equals(nome) ||
                            velhoJogador.getNumero() != numero ||
                            velhoJogador.getPosicao() != posicao) {
                    velhoJogador.setNome(nome);
                    velhoJogador.setNumero(numero);
                    velhoJogador.setPosicao(posicao);
                    superActivity.persistirDados();
                    adapterJogadores.notifyItemChanged(position);
                } else {
                    Toast.makeText(superActivity, R.string.jogador_erro_adicionar, Toast.LENGTH_SHORT).show();
                }
                superActivity.esconderAlerta();
            }
        });

        view.findViewById(R.id.btn_cancelar_jogador).setOnClickListener(arg0 -> superActivity.esconderAlerta());

        builder.setView(view);
        builder.setTitle(R.string.titulo_alerta_novo_jogador);
        superActivity.mostrarAlerta(builder);
    }

    /*
    private void montarAlertaDeletarJogador(Jogador j) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.alerta_default, null);

        Button btn_confirmar = view.findViewById(R.id.btn_confirmar_default);
        Button btn_cancelar = view.findViewById(R.id.btn_cancelar_default);
        TextView msg = view.findViewById(R.id.msg_alerta_default);
        btn_confirmar.setVisibility(View.VISIBLE);
        btn_cancelar.setVisibility(View.VISIBLE);
        msg.setVisibility(View.VISIBLE);
        msg.setText(R.string.msg_alerta_confir_excluir_jogador);

        btn_confirmar.setOnClickListener(arg0 -> {
            alertaDialog.dismiss();
            excluirJogador(j);
        });

        btn_cancelar.setOnClickListener(arg0 -> {
            alertaDialog.dismiss();
        });

        builder.setView(view);
        builder.setTitle(R.string.titulo_alerta_confir_excluir_jogador);
        mostrarAlerta(builder);
    }
*/
}