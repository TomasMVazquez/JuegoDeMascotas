package com.applications.toms.juegodemascotas.model;

import java.util.List;

public class Juego {

    //atributos
    private String idPlay;
    private Integer privacidad; //La privacidad es 0 para publica y 1 para privada -- por ahora no esta la funcionalidad de privada
    private String fechaJuego;
    private String horaJuego;
    private String idPlace;
    private String tamanioPerros;
    private List<Mascota> organizadorMascota;
    private Duenio organizadorDuenio;
    private List<Mascota> invitados;

    //Constructor

    public Juego() {
    }

    public Juego(String idPlay, Integer privacidad, String fechaJuego, String horaJuego, String idPlace, String tamanioPerros, List<Mascota> organizadorMascota, Duenio organizadorDuenio, List<Mascota> invitados) {
        this.idPlay = idPlay;
        this.privacidad = privacidad;
        this.fechaJuego = fechaJuego;
        this.horaJuego = horaJuego;
        this.idPlace = idPlace;
        this.tamanioPerros = tamanioPerros;
        this.organizadorMascota = organizadorMascota;
        this.organizadorDuenio = organizadorDuenio;
        this.invitados = invitados;
    }

    //Getter

    public String getIdPlay() {
        return idPlay;
    }

    public Integer getPrivacidad() {
        return privacidad;
    }

    public String getFechaJuego() {
        return fechaJuego;
    }

    public String getHoraJuego() {
        return horaJuego;
    }

    public String getIdPlace() {
        return idPlace;
    }

    public String getTamanioPerros() {
        return tamanioPerros;
    }

    public List<Mascota> getOrganizadorMascota() {
        return organizadorMascota;
    }

    public Duenio getOrganizadorDuenio() {
        return organizadorDuenio;
    }

    public List<Mascota> getInvitados() {
        return invitados;
    }

    //ToString
    @Override
    public String toString() {
        return "Juego{" +
                "fechaJuego=" + fechaJuego +
                ", horaJuego=" + horaJuego +
                ", idPlace='" + idPlace + '\'' +
                '}';
    }
}
