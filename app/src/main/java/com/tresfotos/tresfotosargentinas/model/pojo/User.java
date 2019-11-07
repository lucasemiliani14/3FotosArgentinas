package com.tresfotos.tresfotosargentinas.model.pojo;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;
    private String name;
    private Integer nivel;
    private Integer plata;
    private String email;

    public User(String name) {
        this.name = name;
        this.nivel = 1;
        this.plata = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    public Integer getPlata() {
        return plata;
    }

    public void setPlata(Integer plata) {
        this.plata = plata;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
