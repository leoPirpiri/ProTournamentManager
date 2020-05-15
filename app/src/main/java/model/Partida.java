package model;

import java.io.Serializable;

public class Partida implements Serializable {
    private int idHome; //Equipe mandante da partida
    private int idVisitor; //Equipe visitante
    private boolean estado; //True::partida encerrada

    public Partida(int idHome, int idVisitor) {
        this.idHome = idHome;
        this.idVisitor = idVisitor;
    }
    public Partida(){
    }
}
