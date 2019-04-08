package com.applications.toms.juegodemascotas.model;

import java.util.Date;
import java.util.List;

public class Juego {

    //atributos
    private String privacidad;
    private Date fechaJuego;
    private Date horaJuego;
    private String lugar;
    private String tamanioPerros;
    private String organizadorMascota;
    private String organizadorDuenio;
    private List<Mascota> invitados;

    //Constructor

    public Juego() {
    }

    public Juego(String privacidad, Date fechaJuego, Date horaJuego, String lugar, String tamanioPerros, String organizadorMascota, String organizadorDuenio, List<Mascota> invitados) {
        this.privacidad = privacidad;
        this.fechaJuego = fechaJuego;
        this.horaJuego = horaJuego;
        this.lugar = lugar;
        this.tamanioPerros = tamanioPerros;
        this.organizadorMascota = organizadorMascota;
        this.organizadorDuenio = organizadorDuenio;
        this.invitados = invitados;
    }

    //Getter
    public String getPrivacidad() {
        return privacidad;
    }

    public Date getFechaJuego() {
        return fechaJuego;
    }

    public Date getHoraJuego() {
        return horaJuego;
    }

    public String getLugar() {
        return lugar;
    }

    public String getTamanioPerros() {
        return tamanioPerros;
    }

    public String getOrganizadorMascota() {
        return organizadorMascota;
    }

    public String getOrganizadorDuenio() {
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
                ", lugar='" + lugar + '\'' +
                '}';
    }
}
