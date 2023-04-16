package model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Torneio extends EntConcreta {
    public static final int MAX_EQUIPE = 16;
    public static final int MIN_EQUIPE = 4;
    public static final int MAX_GERENCIADORES = 4;
    public static final int STATUS_ABERTO = 0;
    public static final int STATUS_FECHADO = 1;
    public static final int STATUS_FINALIZADO = 2;

    private String sede = "Guarabira";
    private long dataAtualizacaoRemota;
    private long dataAtualizacaoLocal;
    private Equipe campeao;
    private ArvoreTabela tabela;
    private ArrayList<Equipe> equipes;
    private ArrayList<String> gerenciadores;

    public Torneio() {
    }

    public Torneio(int id, String nome, String organizadorId) {
        super(id, nome);
        this.campeao = null;
        this.tabela = new ArvoreTabela();
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

    public Equipe getCampeao() {
        if (this.campeao == null && estarFechado()) {
            int raiz = tabela.getRaiz().getCampeaoId();
            if (raiz != 0) {
                campeao = getEquipe(raiz);
            }
        }
        return this.campeao;
    }

    public ArvoreTabela getTabela() {
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

    public void setCampeao(Equipe campeao) {
        this.campeao = campeao;
    }

    public void setTabela(ArvoreTabela tabela) {
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
        return tabela != null && tabela.getRaiz() != null;
    }

    public int pegarStatus() {
        return (estarFechado() ? (campeao == null ? STATUS_FECHADO : STATUS_FINALIZADO) : STATUS_ABERTO);
    }

    public void setRaizTabela(NoPartida raiz) {
        this.tabela.setRaiz(raiz);
    }

    public boolean fecharTorneio(String[] partida_nomes) {
        if (equipes.size() < MIN_EQUIPE) {
            return false;
        }
        tabela.gerarTabela(new ArrayList<>(equipes), new ArrayList<>(Arrays.asList(partida_nomes)));
        //tabela.testarArvore(tabela.getRaiz());
        return estarFechado();
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

    public Equipe delEquipe(int pos) {
        if (!estarFechado()) {
            return equipes.remove(pos);
        } else return null;
    }

    public Equipe getEquipe(int index) {
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

    public ArrayList<NoPartida> getPartidasOitavas(int c) {
        ArrayList<NoPartida> oitavas = new ArrayList<>();
        NoPartida p;
        for (int i = c; i <= c + 12; i += 4) {
            p = tabela.buscarPartida(i);
            if (p != null) {
                oitavas.add(p);
            }
        }
        return oitavas;
    }

    @NonNull
    public String toString() {
        return super.toString() +
                " Quantidade de times: " + equipes.size() +
                " Organizador: " + buscarDonoDoTorneio() +
                " Estado: " + (estarFechado() ? (campeao == null ? "Fechado e Não finalizado" :
                "Finalizado - Campeão: " + campeao.getNome()) : "Aberto");
    }
}
