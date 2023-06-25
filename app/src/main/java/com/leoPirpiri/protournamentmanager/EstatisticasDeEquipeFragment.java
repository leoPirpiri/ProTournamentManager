package com.leoPirpiri.protournamentmanager;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import model.Equipe;
import model.Jogador;
import model.Olimpia;
import model.Score;
import model.Torneio;


public class EstatisticasDeEquipeFragment extends Fragment {

    private EquipeActivity superActivity;
    private Equipe equipe;

    public EstatisticasDeEquipeFragment() {/*Required empty public constructor*/}

    public EstatisticasDeEquipeFragment(Equipe equipe) {
        this.equipe = equipe;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.superActivity = (EquipeActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_estatisticas_equipe, container, false);
        if (superActivity.getTorneioTrabalhado().estarFechado()){
            v.findViewById(R.id.layout_estatisticas_geral_equipe).setVisibility(View.VISIBLE);
            if (superActivity.getTorneioTrabalhado().getIdCampeao() == equipe.getId()){
                v.findViewById(R.id.layout_estatistica_campeao_equipe).setVisibility(View.VISIBLE);
            }
            HashMap<String, ArrayList<Score>> estatisticasGerais = superActivity.getTorneioTrabalhado().buscarEstatisticasTorneio(equipe.getId());
            int n_registro_padrao = 4;

            //Preenche estatísticas de artilharia
            ArrayList<Score> artilharia = estatisticasGerais.get("ponto");

            if (!artilharia.isEmpty()){
                String msg_artilheiros = getString(R.string.cabecalho_estatistica_artilharia_equipe);
                LinkedHashMap<Integer, Long> dados = artilharia.stream()
                        .collect(Collectors.groupingBy(Score::getIdJogador, Collectors.counting()))
                        .entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (oldValue, newValue) -> oldValue, LinkedHashMap::new));
                int posicoes = 0;
                for (Integer key : dados.keySet()) {
                    msg_artilheiros = String.format("%s\n%d %s - %s", msg_artilheiros,
                            dados.get(key),
                            dados.get(key)>1 ? "Gols" : "Gol",
                            equipe.buscarJogador(key).getNome()
                    );
                    if (++posicoes == n_registro_padrao) break;
                }
                ((TextView) v.findViewById(R.id.txv_estatistica_artilheiros)).setText(msg_artilheiros);
            }

            //Preenche estatísticas de falta
            ArrayList<Score> faltas = estatisticasGerais.get("falta");

            if (!faltas.isEmpty()){
                String msg_faltas = getString(R.string.cabecalho_estatistica_fairplay_equipe);
                Map<Integer, Long> dados = faltas.stream()
                        .collect(Collectors.groupingBy(Score::getIdJogador, Collectors.counting()));
                int posicoes=0;
                for (Integer key: dados.entrySet()
                        .stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (oldValue, newValue) -> oldValue, LinkedHashMap::new)).keySet()){
                    msg_faltas = String.format("%s\n%d %s - %s", msg_faltas,
                            dados.get(key),
                            dados.get(key)>1 ? "Faltas" : "Falta",
                            equipe.buscarJogador(key).getNome()
                    );
                    if (++posicoes == n_registro_padrao) break;
                }
                ((TextView) v.findViewById(R.id.txv_estatistica_faltas)).setText(msg_faltas);
            }

            //Preenche estatísticas de cartões
            ArrayList<Score> cartoes = estatisticasGerais.get("cartao");

            if (!cartoes.isEmpty()){
                String msg_cartoes = getString(R.string.cabecalho_estatistica_jogo_limpo_equipe);
                Map<Integer, Long> dados = cartoes.stream()
                        .collect(Collectors.groupingBy(Score::getIdJogador, Collectors.counting()));
                int posicoes=0;
                for (Integer key: dados.entrySet()
                        .stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (oldValue, newValue) -> oldValue, LinkedHashMap::new)).keySet()){
                    msg_cartoes = String.format("%s\n%d %s - %s", msg_cartoes,
                            dados.get(key),
                            dados.get(key)>1 ? "Cartões" : "Cartão",
                            equipe.buscarJogador(key).getNome()
                    );
                    if (++posicoes == n_registro_padrao) break;
                }
                ((TextView) v.findViewById(R.id.txv_estatistica_cartoes)).setText(msg_cartoes);
            }

        } else {
            v.findViewById(R.id.msg_estatistica_equipe).setVisibility(View.VISIBLE);

        }

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}