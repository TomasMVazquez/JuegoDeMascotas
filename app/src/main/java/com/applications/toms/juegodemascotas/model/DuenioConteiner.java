package com.applications.toms.juegodemascotas.model;

import java.util.List;

public class DuenioConteiner {

    //atributos
    private List<Duenio> duenioList;

    //constructor
    public DuenioConteiner() {
    }

    public DuenioConteiner(List<Duenio> deviceList) {
        this.duenioList = deviceList;
    }

    //getter
    public List<Duenio> getDuenioList() {
        return duenioList;
    }

    //tostring
    @Override
    public String toString() {
        return "DuenioConteiner{" +
                "duenioList=" + duenioList +
                '}';
    }
}
