package com.applications.toms.juegodemascotas.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PlayDate implements Comparable<PlayDate> {

    //atributos
    private String idPlay;
    private String title;
    private Integer privacy; //La privacy es 0 para publica y 1 para privada -- por ahora no esta la funcionalidad de privada
    private String datePlay;
    private String timePlay;
    private String idPlace;
    private String size;
    private List<Pet> creatorPets;
    private Owner creator;
    private List<String> participants;
    private String referenceLocation;

    //Constructor

    public PlayDate() {
    }

    public PlayDate(String title,String idPlay, Integer privacy, String datePlay, String timePlay, String idPlace, String size, List<Pet> creatorPets, Owner creator, List<String> participants, String referenceLocation) {
        this.title = title;
        this.idPlay = idPlay;
        this.privacy = privacy;
        this.datePlay = datePlay;
        this.timePlay = timePlay;
        this.idPlace = idPlace;
        this.size = size;
        this.creatorPets = creatorPets;
        this.creator = creator;
        this.participants = participants;
        this.referenceLocation = referenceLocation;
    }

    //Getter

    public String getTitle() {
        return title;
    }

    public String getIdPlay() {
        return idPlay;
    }

    public Integer getPrivacy() {
        return privacy;
    }

    public String getDatePlay() {
        return datePlay;
    }

    public String getTimePlay() {
        return timePlay;
    }

    public String getIdPlace() {
        return idPlace;
    }

    public String getSize() {
        return size;
    }

    public List<Pet> getCreatorPets() {
        return creatorPets;
    }

    public Owner getCreator() {
        return creator;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public String getReferenceLocation() {
        return referenceLocation;
    }

    //ToString
    @Override
    public String toString() {
        return "PlayDate{" +
                "datePlay=" + datePlay +
                ", timePlay=" + timePlay +
                ", idPlace='" + idPlace + '\'' +
                '}';
    }

    public Date getDateTime(){
        try {
            Date date=new SimpleDateFormat("dd/MM/yyyy hh:mm")
                    .parse(getDatePlay() + " " + getTimePlay());
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int compareTo(PlayDate o) {
        return getDateTime().compareTo(o.getDateTime());
    }
}
