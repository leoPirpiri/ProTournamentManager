package model;

public class Score extends EntGeral {
    public static final int TIPO_PONTO = 0;
    public static final int TIPO_AMARELO = 1;
    public static final int TIPO_VERMELHO = 2;
    public static final int TIPO_FALTA_INDIVIDUAL = 3;
    public static final int TIPO_FALTA_TATICA_COLETIVA = 4;

    private int idJogador;
    private int tipo;

    public Score(int id, int idJogador, int tipo) {
        super(id);
        this.idJogador = idJogador;
        this.tipo = tipo;
    }

    public int getIdJogador() {
        return idJogador;
    }

    public int getTipo() {
        return tipo;
    }

    @Override
    public String toString() {
        return super.toString() +
                "Tipo: " + tipo +
                "Id do Jogador: " + idJogador;
    }
}
