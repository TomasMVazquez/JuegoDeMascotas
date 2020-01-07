package com.applications.toms.juegodemascotas.model;

import java.util.Comparator;
import java.util.PrimitiveIterator;

public class Message {

    private Integer id;
    private String message;
    private String time;
    private String user;

    public Message() {
    }

    public Message(Integer id,String message, String time, String user) {
        this.id = id;
        this.message = message;
        this.time = time;
        this.user = user;
    }

    public Integer getId() {
        return id;
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
