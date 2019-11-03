package com.applications.toms.juegodemascotas.model;

public class Message {

    private String message;
    private String time;
    private String user;

    public Message() {
    }

    public Message(String message, String time, String user) {
        this.message = message;
        this.time = time;
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public String getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "Message{" +
                "message='" + message + '\'' +
                ", time='" + time + '\'' +
                ", user='" + user + '\'' +
                '}';
    }
}
