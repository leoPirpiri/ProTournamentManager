package model;

import java.util.ArrayList;
import java.util.Arrays;

public class Torneio extends EntConcreta {
    public final int MAX_EQUIPE = 16;
    public final int MIN_EQUIPE = 4;

    private ArrayList<Equipe> times;
    private ArvoreTabela tabela;
    private Equipe campeao;

    public Torneio(int id, String nome) {
        super(id, nome);
        this.times = new ArrayList<>();
        this.tabela = new ArvoreTabela();
        this.campeao = null;
    }

    public boolean isFechado() {
        return tabela.getRaiz() != null;
    }

    public Equipe getCampeao() {
        if(tabela.getRaiz() != null){
            int raiz = tabela.getCampeaoId();
            if (raiz!=0){
                campeao = getTime(raiz);
            }
        }
        return this.campeao;
    }

    public ArvoreTabela getTabela() {
        return tabela;
    }

    public ArrayList<Equipe> getTimes() {
        return times;
    }

    public boolean fecharTorneio(String[] partida_nomes) {
        if(times.size()<MIN_EQUIPE) {
            return false;
        }
        tabela.gerarTabela(new ArrayList<>(times), new ArrayList<>(Arrays.asList(partida_nomes)));
        tabela.testarArvore(tabela.getRaiz());
        return isFechado();
    }

    public void addTime(Equipe time) {
        if(!isFechado() && times.size()<MAX_EQUIPE){
            this.times.add(time);
        }
    }

    public Equipe delTime(int pos){
        if(!isFechado()) {
            return times.remove(pos);
        }else return null;
    }

    public Equipe getTime(int index){
        for (Equipe e : times) {
            if (e.getId() == index){
                return e;
            }
        }
        return null;
    }

    public int getNovaEquipeId(){
        ArrayList index = new ArrayList();
        for (Equipe e: times) {
            index.add(e.getIdNivel1());
        }
        int i = index.size()+1;
        do {
            if(!index.contains(i)){
                return getId() + i*100;
            } else {
                i++;
            }
        }while (i<100);
        return 0;
    }

    public String toString(){
        return super.toString() +
               " Quantidade de times: " + times.size() +
               " Estado: "+ (isFechado() ? (campeao == null ? "Fechado e Não finalizado" :
                                    "Finalizado - Campeão: " + campeao.getNome()) : "Aberto");
    }

    public ArrayList<NoPartida> getPartidasOitavas(int c) {
        ArrayList<NoPartida> oitavas = new ArrayList<>();
        NoPartida p;
        for(int i = c; i<=c+12; i+=4){
            p = tabela.getPartida(i);
            if(p!=null){
                oitavas.add(p);
            }
        }
        return oitavas;
    }
}
