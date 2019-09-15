package com.applications.toms.juegodemascotas.model;

import java.util.List;

public class PlayDateConteiner {

    //Atributos
    private List<PlayDate> playDateList;

    //Constructor
    public PlayDateConteiner() {
    }

    public PlayDateConteiner(List<PlayDate> playDateList) {
        this.playDateList = playDateList;
    }

    //Getter
    public List<PlayDate> getPlayDateList() {
        return playDateList;
    }

    //ToString
    @Override
    public String toString() {
        return "PlayDateConteiner{" +
                "playDateList=" + playDateList +
                '}';
    }
}
