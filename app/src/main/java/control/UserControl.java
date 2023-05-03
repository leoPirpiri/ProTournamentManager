package control;

import java.util.HashMap;
import java.util.Map;

import model.Partida;

public class UserControl {
    private String exemplo;
    Map<String, Partida> user;

    public UserControl() {
        this.exemplo = "teste";
        this.user = new HashMap<>();
    }

    public String getExemplo() {
        return exemplo;
    }

    public Map<String, Partida> getUser() {
        return user;
    }

    public void setUser(Map<String, Partida> user) {
        this.user = user;
    }
}
