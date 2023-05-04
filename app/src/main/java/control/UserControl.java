package control;

import java.util.HashMap;
import java.util.Map;

import model.Partida;
import model.Tabela;

public class UserControl {
    private String exemplo;
    private Tabela tabela;

    public UserControl() {
        this.exemplo = "teste";
        this.tabela = new Tabela();
    }

    public String getExemplo() {
        return exemplo;
    }

    public void setExemplo(String exemplo) {
        this.exemplo = exemplo;
    }

    public Tabela getTabela() {
        return tabela;
    }

    public void setTabela(Tabela tabela) {
        this.tabela = tabela;
    }
}
