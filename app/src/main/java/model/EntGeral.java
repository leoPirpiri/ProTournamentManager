package model;

import java.io.Serializable;

public class EntGeral implements Serializable {
    private int id;

    public EntGeral (int id) {
        this.id = id;
    }

    public void setId(int id) {
        this.id = id;
    }

    //Do id 102030 retorna 102030
    public int getId() {
        return id;
    }

    //Do id 102030 retorna 10
    public int getIdNivel0() {
        return id/10000;
    }

    //Do id 102030 retorna 20
    public int getIdNivel1() {
        return (id%10000)/100;
    }

    //Do id 102030 retorna 30
    public int getIdNivel2() {
        return id%100;
    }

    public String toString(){
        return "Id: "+ id;
    }
}
