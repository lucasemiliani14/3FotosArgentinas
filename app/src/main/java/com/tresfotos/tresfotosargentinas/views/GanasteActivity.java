package com.tresfotos.tresfotosargentinas.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.tresfotos.tresfotosargentinas.R;
import com.tresfotos.tresfotosargentinas.database.AppDatabase;

public class GanasteActivity extends AppCompatActivity {

    AppDatabase appDatabase;
    Button botonGanaste;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ganaste);

        appDatabase = AppDatabase.getInMemoryDatabase(this);
        botonGanaste = findViewById(R.id.boton_ganaste);

        botonGanaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appDatabase.palabraDao().updateAllPalabrasWhenUserWins(false, true);
                appDatabase.palabraDao().updateAllHintsWhenUserWins();
                appDatabase.userDao().volverANivelCeroCuandoGana(1);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                finish();
                startActivity(intent);
            }
        });

    }
}
