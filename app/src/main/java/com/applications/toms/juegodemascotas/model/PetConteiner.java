package com.applications.toms.juegodemascotas.model;

import java.util.List;

public class PetConteiner {

    //Atributos
    private List<Pet> petList;

    //Constructor
    public PetConteiner() {
    }

    public PetConteiner(List<Pet> petList) {
        this.petList = petList;
    }

    //Getter

    public List<Pet> getPetList() {
        return petList;
    }


    //ToString

    @Override
    public String toString() {
        return "PetConteiner{" +
                "petList=" + petList +
                '}';
    }
}
