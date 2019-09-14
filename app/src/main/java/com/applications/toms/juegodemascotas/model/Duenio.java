package com.applications.toms.juegodemascotas.model;

import java.io.Serializable;
import java.util.List;

public class Duenio implements Serializable {

    //atributos
    private String userId;
    private String nombre;
    private String email;
    private String sexo;
    private String fechaNacimiento;
    private String direccion;
    private String fotoDuenio;
    private String infoDuenio;

    //Constructor

    public Duenio() {
    }

    public Duenio(String userId, String nombre, String email, String fotoDuenio) {
        this.userId = userId;
        this.nombre = nombre;
        this.email = email;
        this.fotoDuenio = fotoDuenio;
    }

    public Duenio(String userId, String nombre, String email,String direccion, String sexo, String fechaNacimiento, String fotoDuenio, String infoDuenio) {
        this.userId = userId;
        this.nombre = nombre;
        this.email = email;
        this.direccion = direccion;
        this.sexo = sexo;
        this.fechaNacimiento = fechaNacimiento;
        this.fotoDuenio = fotoDuenio;
        this.infoDuenio = infoDuenio;
    }

    //getter

    public String getUserId() {
        return userId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getSexo() {
        return sexo;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getFotoDuenio() {
        return fotoDuenio;
    }

    public String getInfoDuenio() {
        return infoDuenio;
    }

    //Setter
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public void setFotoDuenio(String fotoDuenio) {
        this.fotoDuenio = fotoDuenio;
    }

    public void setInfoDuenio(String infoDuenio) {
        this.infoDuenio = infoDuenio;
    }

    //toString

    @Override
    public String toString() {
        return "Duenio{" +
                "userId='" + userId + '\'' +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", sexo='" + sexo + '\'' +
                ", fechaNacimiento='" + fechaNacimiento + '\'' +
                ", direccion='" + direccion + '\'' +
                ", fotoDuenio='" + fotoDuenio + '\'' +
                ", infoDuenio='" + infoDuenio + '\'' +
                '}';
    }
}
