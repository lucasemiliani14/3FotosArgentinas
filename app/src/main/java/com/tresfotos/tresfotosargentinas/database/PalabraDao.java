package com.tresfotos.tresfotosargentinas.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.tresfotos.tresfotosargentinas.model.pojo.Palabra;

import java.util.HashMap;
import java.util.List;

@Dao
public interface PalabraDao {

    @Insert
    void insertPalabra(Palabra palabra);

    @Query("SELECT * FROM Palabra WHERE nombrePalabra = :palabra")
    Palabra getPalabraByName(String palabra);

    @Query("SELECT * FROM Palabra WHERE adivinada = :ponerFalso")
    List<Palabra> getAllPalabras(Boolean ponerFalso);

    @Query("UPDATE Palabra SET adivinada = :trueOrFalse WHERE nombrePalabra = :palabra")
    void updateIfIsAdivinada(Boolean trueOrFalse, String palabra);

    @Query("UPDATE Palabra SET adivinada = :ponerFalse WHERE adivinada = :ponerTrue")
    void updateAllPalabrasWhenUserWins(Boolean ponerFalse, Boolean ponerTrue);

    @Query("UPDATE Palabra SET pistasUtilizadas = null")
    void updateAllHintsWhenUserWins();

    @Query("UPDATE Palabra SET esNivelActual = :ponerTrueOFalse WHERE nombrePalabra = :palabra")
    void updateIfIsNivelActual(Boolean ponerTrueOFalse, String palabra);

    @Query("UPDATE Palabra SET pistasUtilizadas = :celdaPista WHERE nombrePalabra = :palabra")
    void addPistaDePalabra(String celdaPista, String palabra);

}
