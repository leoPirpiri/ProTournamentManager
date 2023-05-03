package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArvoreTabela implements Serializable {
    private Partida raiz;

    public ArvoreTabela(){
        raiz = null;
    }

    public Partida getRaiz() {
        return raiz;
    }

    public void setRaiz(Partida raiz) {
        this.raiz = raiz;
    }

    public Partida buscarPartida(int valor){
        return caminharNo(raiz, valor);
    }

    public boolean verificarExclusaoSegura(int idJogador){
        return buscarParticipacaoJogador(raiz, idJogador);
    }

    private boolean buscarParticipacaoJogador(Partida node, int chave){
        /*if(node.participacaoJogadorNaPartida(chave)){
            return true;
        } else if(node.getMandante() == null || node.getVisitante() == null){
            return false;
        } else {
            return buscarParticipacaoJogador(node.getMandante(), chave) || buscarParticipacaoJogador(node.getVisitante(), chave);
        }*/
        return true;
    }

    private Partida caminharNo(Partida no, int valor) {
        if(no.getId() == valor) {
            return no;
        }
        if(no.getId() > valor) {
            return caminharNo(no.getMandante(), valor);
        } else {
            return caminharNo(no.getVisitante(), valor);
        }
    }

    private Partida preencherTabela(int i, int f, List<Equipe> timesRestantes, ArrayList<String> nomesPartidas){
        int id = (i+f)/2;
        Partida node;
        if(id%2 == 1) {
            node = new Partida(id, nomesPartidas.get(0));
            if(!timesRestantes.isEmpty()){
                node.setCampeaoId(timesRestantes.get(0).getId());
            } else {
                node.setCampeaoId(-1);
            }
        } else {
            node = new Partida(id, nomesPartidas.remove(1));
            int meio = (int)Math.round(timesRestantes.size()/2.0);
            node.setMandante(preencherTabela(i, (i+f)/2-1, timesRestantes.subList(0, meio), nomesPartidas));
            node.setVisitante(preencherTabela((i+f)/2+1, f, timesRestantes.subList(meio, timesRestantes.size()), nomesPartidas));
            if(node.getMandante().isEncerrada() && node.getVisitante().isEncerrada()){
                if(node.getMandante().getCampeaoId()>0 && node.getVisitante().getCampeaoId()<=0){
                    node.setCampeaoId(node.getMandante().getCampeaoId());
                }else if(node.getMandante().getCampeaoId()<=0 && node.getVisitante().getCampeaoId()>=0){
                    node.setCampeaoId(node.getVisitante().getCampeaoId());
                }else if(node.getMandante().getCampeaoId()<0 && node.getVisitante().getCampeaoId()<0){
                    node.setCampeaoId(-1);
                }
            }
        }
        return node;
    }

    public void testarArvore(Partida node){
        if(node != null){
            System.out.println(node);
            testarArvore(node.getMandante());
            testarArvore(node.getVisitante());
        }
    }

    public void gerarTabela(ArrayList<Equipe> times, ArrayList<String> nomesPartidas) {
        int i = 1;
        int f = 31;
        Collections.shuffle(times);
        raiz=preencherTabela(i, f, times, nomesPartidas);
    }
}
