package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArvoreTabela implements Serializable {
    private NoPartida raiz;

    public ArvoreTabela(){
        raiz = null;
    }

    public NoPartida getRaiz() {
        return raiz;
    }

    public int getCampeaoId() {
        return raiz.getCampeaoId();
    }

    public NoPartida getNo(int valor){
        return getNo(raiz, valor);
    }

    private NoPartida getNo(NoPartida no, int valor) {
        if(no.getId() == valor) {
            return no;
        }
        if(no.getId() > valor) {
            return getNo(no.getMandante(), valor);
        } else {
            return getNo(no.getVisitante(), valor);
        }
    }

    private NoPartida preencherTabela(int i, int f, List<Equipe> timesRestantes, ArrayList<String> nomesPartidas){
        int id = (i+f)/2;
        System.out.println("Esse é o ID da folha "+id);
        NoPartida node;
        if(id%2 == 1) {
            node = new NoPartida(id, nomesPartidas.get(0));
            if(!timesRestantes.isEmpty()){
                node.setCampeaoId(timesRestantes.get(0).getId());
            }
        } else {
            node = new NoPartida(id, nomesPartidas.remove(1));
            int meio = (int)Math.round(timesRestantes.size()/2.0);
            node.setMandante(preencherTabela(i, (i+f)/2-1, timesRestantes.subList(0, meio), nomesPartidas));
            node.setVisitante(preencherTabela((i+f)/2+1, f, timesRestantes.subList(meio, timesRestantes.size()), nomesPartidas));
        }
        return node;
    }
    public void testarArvore(NoPartida node){
        if(node != null){
            System.out.println(node.toString());
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
