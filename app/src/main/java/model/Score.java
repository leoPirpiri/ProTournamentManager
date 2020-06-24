package model;

public class Score extends EntGeral {
    public static final int TIPO_PONTO = 0;
    public static final int TIPO_AMARELO = 1;
    public static final int TIPO_VERMELHO = 2;
    public static final int TIPO_FALTA_INDIVIDUAL = 3;
    public static final int TIPO_FALTA_TATICA_COLETIVA = 4;

    private int tipo;
    private int idJogador;

    public Score(int id, int tipo, int idJogador) {
        super(id);
        this.tipo = tipo;
        this.idJogador = idJogador;
    }

    public int getTipo() {
        return tipo;
    }

    public int getIdJogador() {
        return idJogador;
    }

    @Override
    public String toString() {
        return super.toString() +
                "Tipo: " + tipo +
                "Id do Jogador: " + idJogador;
    }
}
