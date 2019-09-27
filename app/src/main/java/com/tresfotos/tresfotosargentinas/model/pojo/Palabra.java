package com.tresfotos.tresfotosargentinas.model.pojo;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;


@Entity(indices = {@Index(value = {"nombrePalabra"},
        unique = true)})
public class Palabra {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;
    private String nombrePalabra;
    private String drawableName1;
    private String drawableName2;
    private String drawableName3;
    private Boolean adivinada = false;
    private Boolean esNivelActual;
    private String pistasUtilizadas;

    public Palabra(String nombrePalabra) {
        this.nombrePalabra = nombrePalabra;
        drawableName1 = getNombrePalabraSinTildes(nombrePalabra) + "1";
        drawableName2 = getNombrePalabraSinTildes(nombrePalabra) + "2";
        drawableName3 = getNombrePalabraSinTildes(nombrePalabra) + "3";
    }

    public String getNombrePalabra() {
        return nombrePalabra;
    }

    public String getDrawableName1() {
        return drawableName1;
    }

    public String getDrawableName2() {
        return drawableName2;
    }

    public String getDrawableName3() {
        return drawableName3;
    }

    public void setNombrePalabra(String nombrePalabra) {
        this.nombrePalabra = nombrePalabra;
    }

    public void setDrawableName1(String drawableName1) {
        this.drawableName1 = drawableName1;
    }

    public void setDrawableName2(String drawableName2) {
        this.drawableName2 = drawableName2;
    }

    public void setDrawableName3(String drawableName3) {
        this.drawableName3 = drawableName3;
    }

    public Boolean getAdivinada() {
        return adivinada;
    }

    public void setAdivinada(Boolean adivinada) {
        this.adivinada = adivinada;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Boolean getEsNivelActual() {
        return esNivelActual;
    }

    public void setEsNivelActual(Boolean esNivelActual) {
        this.esNivelActual = esNivelActual;
    }

    public String getPistasUtilizadas() {
        return pistasUtilizadas;
    }

    public void setPistasUtilizadas(String pistasUtilizadas) {
        this.pistasUtilizadas = pistasUtilizadas;
    }

    private String getNombrePalabraSinTildes(String nombrePalabra){
        return nombrePalabra.replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ü", "u")
                .replace("ú", "u")
                .replace("ñ", "nn");
    }
}
