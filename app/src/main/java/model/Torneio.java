package model;

import java.util.ArrayList;
import java.util.Arrays;

public class Torneio extends EntConcreta {
    public final int MAX_EQUIPE = 16;
    public final int MIN_EQUIPE = 4;
    public static final int STATUS_ABERTO = 0;
    public static final int STATUS_FECHADO = 1;
    public static final int STATUS_FINALIZADO = 2;

    private ArrayList<Equipe> equipes;
    private ArvoreTabela tabela;
    private Equipe campeao;

    public Torneio(int id, String nome) {
        super(id, nome);
        this.equipes = new ArrayList<>();
        this.tabela = new ArvoreTabela();
        this.campeao = null;
    }

    public boolean isFechado() {
        return tabela.getRaiz() != null;
    }

    public int getStatus() {
        return (isFechado() ? (campeao == null ? STATUS_FECHADO : STATUS_FINALIZADO) : STATUS_ABERTO);
    }

    public Equipe getCampeao() {
        if(tabela.getRaiz() != null){
            int raiz = tabela.getCampeaoId();
            if (raiz!=0){
                campeao = getEquipe(raiz);
            }
        }
        return this.campeao;
    }

    public ArvoreTabela getTabela() {
        return tabela;
    }

    public void setTabela(NoPartida raiz) {
        this.tabela.setRaiz(raiz);
    }

    public ArrayList<Equipe> getEquipes() {
        return equipes;
    }

    public boolean fecharTorneio(String[] partida_nomes) {
        if(equipes.size()<MIN_EQUIPE) {
            return false;
        }
        tabela.gerarTabela(new ArrayList<>(equipes), new ArrayList<>(Arrays.asList(partida_nomes)));
        //tabela.testarArvore(tabela.getRaiz());
        return isFechado();
    }

    public boolean atoJogador(int idJogador){
        return  isFechado() && tabela.getSafeDeleteFlag(idJogador);
    }

    public void addEquipe(Equipe equipe) {
        if(!isFechado() && equipes.size()<MAX_EQUIPE && !siglaUsada(equipe.getSigla())){
            this.equipes.add(equipe);
        }
    }

    public boolean siglaUsada(String s){
        return equipes.stream().anyMatch(e -> e.getSigla().equals(s));
    }

    public Equipe delEquipe(int pos){
        if(!isFechado()) {
            return equipes.remove(pos);
        }else return null;
    }

    public Equipe getEquipe(int index){
        for (Equipe e : equipes) {
            if (e.getId() == index){
                return e;
            }
        }
        return null;
    }

    public int getNovaEquipeId(){
        ArrayList index = new ArrayList();
        for (Equipe e: equipes) {
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
               " Quantidade de times: " + equipes.size() +
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
