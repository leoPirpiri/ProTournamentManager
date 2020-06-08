package model;

public class Partida extends EntGeral {
    private int idHome; //Equipe mandante da partida
    private int idVisitor; //Equipe visitante
    private boolean estado; //True::partida encerrada

    public Partida(int id, int idHome, int idVisitor) {
        super(id);
        this.idHome = idHome;
        this.idVisitor = idVisitor;
    }

    public boolean isEncerrada() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public String toString() {
        return super.toString() +
               " Equipe Mandante: " + idHome +
               " Equipe Visitante: " + idVisitor+
               " Estado: " + (estado ? "Partida encerrada.": "Partida em andamento.");

    }
}
