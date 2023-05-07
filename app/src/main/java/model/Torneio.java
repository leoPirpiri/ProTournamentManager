package model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Torneio extends EntConcreta {
    public static final int MAX_EQUIPE = 16;
    public static final int MIN_EQUIPE = 4;
    public static final int MAX_GERENCIADORES = 4;
    private static final int STATUS_ABERTO = -1;
    private static final int STATUS_FECHADO = 0;
    private static final int STATUS_FINALIZADO = 1;

    private String sede = "Guarabira";
    private long dataAtualizacaoRemota;
    private long dataAtualizacaoLocal;
    /*
    * O id do campeão será responsável por indicar o o status do torneio
    * idCampeao sempre inicia com -1. Valor negativo.
    * Será atribuído o valor 0 quado fechar o torneio e a tabela ser gerada.
    * Um campeão só existe se o torneio estiver finalizado (id de uma equipe é sempre > 0. ex: 112200)
    */
    private int idCampeao;
    private Tabela tabela;
    private ArrayList<Equipe> equipes;
    private ArrayList<String> gerenciadores;

    public Torneio() {
    }

    public Torneio(int id, String nome, String organizadorId) {
        super(id, nome);
        this.idCampeao = STATUS_ABERTO;
        this.tabela = new Tabela();
        this.equipes = new ArrayList<>();
        this.gerenciadores = new ArrayList<>();
        this.dataAtualizacaoRemota = System.currentTimeMillis();
        this.dataAtualizacaoLocal = dataAtualizacaoRemota;
        this.gerenciadores.add(organizadorId);
    }

    public String buscarUuid() {
        return buscarDonoDoTorneio() + getId();
    }

    public String getSede() {
        return sede;
    }

    public long getDataAtualizacaoRemota() {
        return dataAtualizacaoRemota;
    }

    public long getDataAtualizacaoLocal() {
        return dataAtualizacaoLocal;
    }

    public int getIdCampeao() {
        return idCampeao;
    }

    public Equipe buscarCampeao() {
        return buscarEquipe(idCampeao);
    }

    public Tabela buscarTabela() {
        return tabela;
    }

    public ArrayList<Equipe> getEquipes() {
        return equipes;
    }

    public ArrayList<String> getGerenciadores() {
        return gerenciadores;
    }

    public ArrayList<String> getNomeFragmentado(){
        return (ArrayList<String>) Arrays.stream(getNome().toLowerCase().split(" "))
                                         .filter(s -> s.length()>2)
                                         .collect(Collectors.toList());
    }

    public void setSede(String sede) {
        this.sede = sede;
    }

    public void setDataAtualizacaoRemota(long dataAtualizacao) {
        this.dataAtualizacaoRemota = dataAtualizacao;
    }

    public void setDataAtualizacaoLocal(long dataAtualizacaoLocal) {
        this.dataAtualizacaoLocal = dataAtualizacaoLocal;
    }

    public void setIdCampeao(int idCampeao) {
        this.idCampeao = idCampeao;
    }

    public void setTabela(Tabela tabela) {
        this.tabela = tabela;
    }

    public void setEquipes(ArrayList<Equipe> equipes) {
        this.equipes = equipes;
    }

    public void setGerenciadores(ArrayList<String> gerenciadores) {
        this.gerenciadores = gerenciadores;
    }

    public String buscarDonoDoTorneio() {
        return gerenciadores.get(0);
    }

    public boolean ehMesario(String usuarioId) {
        return !buscarDonoDoTorneio().equals(usuarioId) && gerenciadores.contains(usuarioId);
    }

    public boolean adicionarMesario(String usuarioId) {
        if (gerenciadores.size() < MAX_GERENCIADORES) {
            gerenciadores.add(usuarioId);
            return true;
        }
        return false;
    }

    public boolean removerMesario(int posicao) {
        if (posicao > 0) {
            gerenciadores.remove(posicao);
            return true;
        }
        return false;
    }

    public boolean estarFechado() {
        return idCampeao >= STATUS_FECHADO;
    }

    public boolean estarFinalizado() {
        return idCampeao >= STATUS_FINALIZADO;
    }

    public int pegarStatus() {
        // Torneio ao ser finalizado, recebe o id do Campeão, antes disso recebe os estado do torneio.
        return (idCampeao > 0 ? STATUS_FINALIZADO : idCampeao);
    }

    public boolean fecharTorneio(String[] partida_nomes) {
        if (equipes.size() < MIN_EQUIPE) {
            return false;
        }
        tabela.sortearTimesGerarTabela(equipes, partida_nomes);
        idCampeao = STATUS_FECHADO;
        //tabela.testarArvore(tabela.getRaiz());
        return true;
    }

    public boolean ParticipacaoAcoesTorneio(int idJogador) {
        return estarFechado() && tabela.verificarExclusaoSegura(idJogador);
    }

    public boolean addEquipe(Equipe equipe) {
        if (!estarFechado() && !estarCheio() && siglaDisponivel(equipe.getSigla())) {
            return this.equipes.add(equipe);
        }
        return false;
    }

    public boolean estarCheio() {
        return equipes.size() == MAX_EQUIPE;
    }

    public boolean siglaDisponivel(String s) {
        return equipes.stream().noneMatch(e -> e.getSigla().equals(s));
    }

    public Equipe excluirEquipe(int pos) {
        if (!estarFechado()) {
            return equipes.remove(pos);
        } else return null;
    }

    public Equipe buscarEquipe(int index) {
        for (Equipe e : equipes) {
            if (e.getId() == index) {
                return e;
            }
        }
        return null;
    }

    public int pegarIdParaNovaEquipe() {
        ArrayList<Integer> index = new ArrayList<>();
        for (Equipe e : equipes) index.add(e.pegarIdNivel1());
        int i = index.size() + 1;
        do {
            if (!index.contains(i)) {
                return getId() + i * 100;
            } else {
                i++;
            }
        } while (i < 100);
        return 0;
    }


    @NonNull
    public String toString() {
        return super.toString() +
                " Quantidade de times: " + equipes.size() +
                " Organizador: " + buscarDonoDoTorneio() +
                " Estado: " + (
                    estarFechado()
                    ? (estarFinalizado() ? "Finalizado - Campeão: " + buscarCampeao().getNome() : "Fechado, mas não finalizado")
                    : "Aberto"
                );
    }
}
