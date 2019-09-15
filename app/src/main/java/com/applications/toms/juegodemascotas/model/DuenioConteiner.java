package com.applications.toms.juegodemascotas.model;

import java.util.List;

public class DuenioConteiner {

    //atributos
    private List<Owner> ownerList;

    //constructor
    public DuenioConteiner() {
    }

    public DuenioConteiner(List<Owner> deviceList) {
        this.ownerList = deviceList;
    }

    //getter
    public List<Owner> getOwnerList() {
        return ownerList;
    }

    //tostring
    @Override
    public String toString() {
        return "DuenioConteiner{" +
                "ownerList=" + ownerList +
                '}';
    }
}
