package com.applications.toms.juegodemascotas.model;

import java.util.Objects;

public class Pet {

    //atributos
    private String idPet;
    private String name;
    private String breed;
    private String size;
    private String sex;
    private String dateBirth;
    private String photo;
    private String info;
    private String ownerId;
    private String search;

    //Constructor

    public Pet() {
    }

    public Pet(String idPet, String name,String search, String breed, String size, String sex, String dateBirth, String photo, String info, String ownerId) {
        this.idPet = idPet;
        this.name = name;
        this.search = search;
        this.breed = breed;
        this.size = size;
        this.sex = sex;
        this.dateBirth = dateBirth;
        this.photo = photo;
        this.info = info;
        this.ownerId = ownerId;
    }

    //Getter

    public String getIdPet() {
        return idPet;
    }

    public String getName() {
        return name;
    }

    public String getBreed() {
        return breed;
    }

    public String getSize() {
        return size;
    }

    public String getSex() {
        return sex;
    }

    public String getDateBirth() {
        return dateBirth;
    }

    public String getPhoto() {
        return photo;
    }

    public String getInfo() {
        return info;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getSearch() {
        return search;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Pet pet = (Pet) o;
        return idPet.equals(pet.idPet) && ownerId.equals(pet.ownerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPet, ownerId);
    }
}
