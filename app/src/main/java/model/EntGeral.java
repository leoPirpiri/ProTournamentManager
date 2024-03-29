package model;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class EntGeral implements Serializable {
    private int id;

    public EntGeral(){}

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
    public int pegarIdNivel0() {
        return id/10000;
    }

    //Do id 102030 retorna 20
    public int pegarIdNivel1() {
        return (id%10000)/100;
    }

    //Do id 102030 retorna 30
    public int pegarIdNivel2() {
        return id%100;
    }

//    //retorna o id completo de uma entidade de nível 0
//    public int extrairIdEntidadeSuperiorLv0(){
//        //Do id 102030 retorna 100000
//        return ((id/10000)*10000);
//    }
//
//    //retorna o id completo de uma entidade de nível 1
//    public int extrairIdEntidadeSuperiorLv1(){
//        //Do id 102030 retorna 102000
//        return ((id/100)*100);
//    }

    @NonNull
    public String toString(){
        return "Id: "+ id;
    }
}
