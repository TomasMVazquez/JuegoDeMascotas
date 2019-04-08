package com.applications.toms.juegodemascotas.model;

import java.util.List;

public class JuegoConteiner {

    //Atributos
    private List<Juego> juegoList;

    //Constructor
    public JuegoConteiner() {
    }

    public JuegoConteiner(List<Juego> juegoList) {
        this.juegoList = juegoList;
    }

    //Getter
    public List<Juego> getJuegoList() {
        return juegoList;
    }

    //ToString
    @Override
    public String toString() {
        return "JuegoConteiner{" +
                "juegoList=" + juegoList +
                '}';
    }
}
