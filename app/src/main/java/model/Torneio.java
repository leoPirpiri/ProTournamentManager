package model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;

public class Torneio extends EntConcreta {
    public static final int MAX_EQUIPE = 16;
    public static final int MIN_EQUIPE = 4;
    public static final int MAX_GERENCIADORES = 4;
    public static final int STATUS_ABERTO = 0;
    public static final int STATUS_FECHADO = 1;
    public static final int STATUS_FINALIZADO = 2;

    private String uuid;
    private String sede = "Guarabira";
    private long dataAtualizacaoRemota;
    private long dataAtualizacaoLocal;
    private Equipe campeao;
    private ArvoreTabela tabela;
    private ArrayList<Equipe> equipes;
    private ArrayList<String> gerenciadores = new ArrayList<>();

    public Torneio(){
        this.equipes = new ArrayList<>();
        this.campeao = null;
        this.tabela = new ArvoreTabela();
        this.dataAtualizacaoRemota = System.currentTimeMillis();
        this.dataAtualizacaoLocal = dataAtualizacaoRemota;
    }

    public Torneio(int id, String nome, String organizadorId) {
        super(id, nome);
        this.gerenciadores.add(organizadorId);
        this.uuid = organizadorId+String.valueOf(id);
        this();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getDataAtualizacaoRemota() {
        return dataAtualizacaoRemota;
    }

    public long getDataAtualizacaoLocal() {
        return dataAtualizacaoLocal;
    }

    public void setDataAtualizacaoRemota(long dataAtualizacao) {
        this.dataAtualizacaoRemota = dataAtualizacao;
    }

    public void setDataAtualizacaoLocal(long dataAtualizacaoLocal) {
        this.dataAtualizacaoLocal = dataAtualizacaoLocal;
    }

    public String buscarDonoDoTorneio(){
        return gerenciadores.get(0);
    }

    public boolean ehMesario(String usuarioId){
        return !buscarDonoDoTorneio().equals(usuarioId) && gerenciadores.contains(usuarioId);
    }

    public void adicionarMesario(String usuarioId){
        gerenciadores.add(usuarioId);
    }

    public boolean removerMesario(int posicao){
        if(posicao > 0){
            gerenciadores.remove(posicao);
            return true;
        }
        return false;
    }

    public String getSede() {
        return sede;
    }

    public boolean estaFechado() {
        return tabela != null && tabela.getRaiz() != null;
    }

    public int pegarStatus() {
        return (estaFechado() ? (campeao == null ? STATUS_FECHADO : STATUS_FINALIZADO) : STATUS_ABERTO);
    }

    public Equipe getCampeao() {
        if(this.campeao == null && estaFechado()){
            int raiz = tabela.getRaiz().getCampeaoId();
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
        return estaFechado();
    }

    public boolean atoJogador(int idJogador){
        return  estaFechado() && tabela.verificarExclusaoSegura(idJogador);
    }

    public void addEquipe(Equipe equipe) {
        if(!estaFechado() && equipes.size()<MAX_EQUIPE && siglaUsada(equipe.getSigla())){
            this.equipes.add(equipe);
        }
    }

    public boolean siglaUsada(String s){
        return equipes.stream().noneMatch(e -> e.getSigla().equals(s));
    }

    public Equipe delEquipe(int pos){
        if(!estaFechado()) {
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

    public int pegarIdParaNovaEquipe(){
        ArrayList<Integer> index = new ArrayList<>();
        for (Equipe e: equipes) index.add(e.pegarIdNivel1());
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

    @NonNull
    public String toString(){
        return super.toString() +
               " Quantidade de times: " + equipes.size() +
               " Organizador: " + buscarDonoDoTorneio()  +
               " Estado: "+ (estaFechado() ? (campeao == null ? "Fechado e Não finalizado" :
                                    "Finalizado - Campeão: " + campeao.getNome()) : "Aberto");
    }

    public ArrayList<NoPartida> getPartidasOitavas(int c) {
        ArrayList<NoPartida> oitavas = new ArrayList<>();
        NoPartida p;
        for(int i = c; i<=c+12; i+=4){
            p = tabela.buscarPartida(i);
            if(p!=null){
                oitavas.add(p);
            }
        }
        return oitavas;
    }
}
