package com.applications.toms.juegodemascotas.model;

import java.io.Serializable;

public class Owner implements Serializable {

    //atributos
    private String userId;
    private String name;
    private String email;
    private String sex;
    private String birthDate;
    private String address;
    private String avatar;
    private String aboutMe;
    private String status;
    private String search;
    private String token;

    //Constructor

    public Owner() {
    }

    public Owner(String userId, String name, String email, String avatar, String search, String status) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.avatar = avatar;
        this.search = search;
        this.status = status;
    }

    public Owner(String userId, String name, String email, String address, String sex, String birthDate, String avatar, String aboutMe) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.address = address;
        this.sex = sex;
        this.birthDate = birthDate;
        this.avatar = avatar;
        this.aboutMe = aboutMe;
    }

    //getter

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getSex() {
        return sex;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public String getStatus() {
        return status;
    }

    public String getSearch() {
        return search;
    }

    public String getToken() {
        return token;
    }

    //Setter
    public void setName(String name) {
        this.name = name;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }


    //toString

    @Override
    public String toString() {
        return "Owner{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", sex='" + sex + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", address='" + address + '\'' +
                ", avatar='" + avatar + '\'' +
                ", aboutMe='" + aboutMe + '\'' +
                '}';
    }
}
