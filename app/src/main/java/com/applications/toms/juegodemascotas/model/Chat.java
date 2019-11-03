package com.applications.toms.juegodemascotas.model;

public class Chat {

    //Atributos
    private String idChat;
    private String userOne;
    private String userTwo;

    public Chat() {
    }

    public Chat(String idChat, String userOne, String userTwo) {
        this.idChat = idChat;
        this.userOne = userOne;
        this.userTwo = userTwo;
    }

    public String getIdChat() {
        return idChat;
    }

    public String getUserOne() {
        return userOne;
    }

    public String getUserTwo() {
        return userTwo;
    }

    public void setIdChat(String idChat) {
        this.idChat = idChat;
    }

    public void setUserOne(String userOne) {
        this.userOne = userOne;
    }

    public void setUserTwo(String userTwo) {
        this.userTwo = userTwo;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "idChat='" + idChat + '\'' +
                ", userOne='" + userOne + '\'' +
                ", userTwo='" + userTwo + '\'' +
                '}';
    }
}
