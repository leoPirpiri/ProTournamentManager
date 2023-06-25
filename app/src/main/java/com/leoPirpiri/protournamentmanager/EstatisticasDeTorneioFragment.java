package com.leoPirpiri.protournamentmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import model.Olimpia;
import model.Score;
import model.Torneio;


public class EstatisticasDeTorneioFragment extends Fragment {

    private Torneio torneio;

    public EstatisticasDeTorneioFragment() {/*Required empty public constructor*/}

    public EstatisticasDeTorneioFragment(Torneio torneio) {
        this.torneio = torneio;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_estatisticas_torneio, container, false);
        if (torneio.estarFechado() && torneio.buscarTabela() != null){
            v.findViewById(R.id.layout_estatisticas_geral).setVisibility(View.VISIBLE);
            if (torneio.estarFinalizado()){
                v.findViewById(R.id.layout_estatica_campeao).setVisibility(View.VISIBLE);
                TextView txv_campeao = v.findViewById(R.id.txv_estatistica_campeao);
                txv_campeao.setText(String.format("%s - %s", torneio.buscarCampeao().getNome(), torneio.buscarCampeao().getSigla()));
            }
            HashMap<String, ArrayList<Score>> estatisticasGerais = torneio.buscarEstatisticasTorneio();
            int n_registro_padrao = 5;

            //Preenche estatísticas de artilharia
            ArrayList<Score> artilharia = estatisticasGerais.get("ponto");

            if (!artilharia.isEmpty()){
                String msg_artilheiros = getString(R.string.cabecalho_estatistica_artilharia);
                LinkedHashMap<Integer, Long> dados = artilharia.stream()
                        .collect(Collectors.groupingBy(Score::getIdJogador, Collectors.counting()))
                        .entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (oldValue, newValue) -> oldValue, LinkedHashMap::new));
                int posicoes = 0;
                for (Integer key : dados.keySet()) {
                    msg_artilheiros = String.format("%s\n%d %s - %s (%s)", msg_artilheiros,
                            dados.get(key),
                            dados.get(key)>1 ? "Gols" : "Gol",
                            torneio.buscarEquipe(Olimpia.extrairIdEntidadeSuperiorLv1(key)).buscarJogador(key).getNome(),
                            torneio.buscarEquipe(Olimpia.extrairIdEntidadeSuperiorLv1(key)).getNome()
                            );
                    if (++posicoes == n_registro_padrao) break;
                }
                ((TextView) v.findViewById(R.id.txv_estatistica_artilheiros)).setText(msg_artilheiros);
            }

            //Preenche estatísticas de falta
            ArrayList<Score> faltas = estatisticasGerais.get("falta");

            if (!faltas.isEmpty()){
                String msg_faltas = getString(R.string.cabecalho_estatistica_fairplay);
                Map<Integer, Long> dados = faltas.stream()
                        .peek(s -> s.setIdJogador(Olimpia.extrairIdEntidadeSuperiorLv1(s.getIdJogador())))
                        .collect(Collectors.groupingBy(Score::getIdJogador, Collectors.counting()));
                for (Equipe e: torneio.getEquipes()) {
                    if(!dados.containsKey(e.getId())){
                        dados.put(e.getId(), 0L);
                    }
                }
                int posicoes=0;
                for (Integer key: dados.entrySet()
                                    .stream().sorted(Map.Entry.comparingByValue())
                                    .collect(Collectors.toMap(
                                            Map.Entry::getKey,
                                            Map.Entry::getValue,
                                            (oldValue, newValue) -> oldValue, LinkedHashMap::new)).keySet()){
                    msg_faltas = String.format("%s\n%d %s - %s", msg_faltas,
                        dados.get(key),
                        dados.get(key)>1 ? "Faltas" : "Falta",
                        torneio.buscarEquipe(key).getNome()
                );
                    if (++posicoes == n_registro_padrao) break;
                }
                ((TextView) v.findViewById(R.id.txv_estatistica_faltas)).setText(msg_faltas);
            }

            //Preenche estatísticas de cartões
            ArrayList<Score> cartoes = estatisticasGerais.get("cartao");

            if (!cartoes.isEmpty()){
                String msg_cartoes = getString(R.string.cabecalho_estatistica_jogo_limpo);
                Map<Integer, Long> dados = cartoes.stream()
                        .peek(s -> s.setIdJogador(Olimpia.extrairIdEntidadeSuperiorLv1(s.getIdJogador())))
                        .collect(Collectors.groupingBy(Score::getIdJogador, Collectors.counting()));
                for (Equipe e: torneio.getEquipes()) {
                    if(!dados.containsKey(e.getId())){
                        dados.put(e.getId(), 0L);
                    }
                }
                int posicoes=0;
                for (Integer key: dados.entrySet()
                        .stream().sorted(Map.Entry.comparingByValue())
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (oldValue, newValue) -> oldValue, LinkedHashMap::new)).keySet()){
                    msg_cartoes = String.format("%s\n%d %s - %s", msg_cartoes,
                            dados.get(key),
                            dados.get(key)>1 ? "Cartões" : "Cartão",
                            torneio.buscarEquipe(key).getNome()
                    );
                    if (++posicoes == n_registro_padrao) break;
                }
                ((TextView) v.findViewById(R.id.txv_estatistica_cartoes)).setText(msg_cartoes);
            }

        } else {
            v.findViewById(R.id.msg_estatistica).setVisibility(View.VISIBLE);

        }
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}