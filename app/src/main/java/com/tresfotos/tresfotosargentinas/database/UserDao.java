package com.tresfotos.tresfotosargentinas.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.tresfotos.tresfotosargentinas.model.pojo.User;

@Dao
public interface UserDao {

    @Insert
    void insertUser(User user);

    @Query("SELECT * FROM User")
    User getUser();

    @Query("UPDATE User SET plata = :plata")
    void updatePlata(Integer plata);

    @Query("UPDATE User SET nivel = :masUno")
    void updateLevel(Integer masUno);

    @Query("UPDATE user SET nivel = :ponerUno")
    void volverANivelCeroCuandoGana(Integer ponerUno);
}
