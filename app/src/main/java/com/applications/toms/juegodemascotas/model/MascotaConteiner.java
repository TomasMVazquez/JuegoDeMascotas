package com.applications.toms.juegodemascotas.model;

import java.util.List;

public class MascotaConteiner {

    //Atributos
    private List<Mascota> mascotaList;

    //Constructor
    public MascotaConteiner() {
    }

    public MascotaConteiner(List<Mascota> mascotaList) {
        this.mascotaList = mascotaList;
    }

    //Getter

    public List<Mascota> getMascotaList() {
        return mascotaList;
    }


    //ToString

    @Override
    public String toString() {
        return "MascotaConteiner{" +
                "mascotaList=" + mascotaList +
                '}';
    }
}
