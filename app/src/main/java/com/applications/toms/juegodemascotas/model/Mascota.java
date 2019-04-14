package com.applications.toms.juegodemascotas.model;

public class Mascota {

    //atributos
    private String idPet;
    private String nombre;
    private String raza;
    private String tamanio;
    private String sexo;
    private String fechaNacimiento;
    private String fotoMascota;
    private String infoMascota;
    private Duenio miDuenio;

    //Constructor

    public Mascota() {
    }

    public Mascota(String idPet,String nombre, String raza, String tamanio, String sexo, String fechaNacimiento, String fotoMascota, String infoMascota, Duenio miDuenio) {
        this.idPet = idPet;
        this.nombre = nombre;
        this.raza = raza;
        this.tamanio = tamanio;
        this.sexo = sexo;
        this.fechaNacimiento = fechaNacimiento;
        this.fotoMascota = fotoMascota;
        this.infoMascota = infoMascota;
        this.miDuenio = miDuenio;
    }

    //Getter
    public String getIdPet() {
        return idPet;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRaza() {
        return raza;
    }

    public String getTamanio() {
        return tamanio;
    }

    public String getSexo() {
        return sexo;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getFotoMascota() {
        return fotoMascota;
    }

    public String getInfoMascota() {
        return infoMascota;
    }

    public Duenio getMiDuenio() {
        return miDuenio;
    }

    //Setter
    public void setFotoMascota(String fotoMascota) {
        this.fotoMascota = fotoMascota;
    }

    public void setInfoMascota(String infoMascota) {
        this.infoMascota = infoMascota;
    }

    //toString
    @Override
    public String toString() {
        return "Mascota{" +
                "nombre='" + nombre + '\'' +
                ", raza='" + raza + '\'' +
                '}';
    }
}
