package com.tresfotos.tresfotosargentinas.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.tresfotos.tresfotosargentinas.model.pojo.Palabra;
import com.tresfotos.tresfotosargentinas.model.pojo.User;

@Database(entities = {Palabra.class, User.class}, version = 1, exportSchema = false)
    public abstract class AppDatabase extends RoomDatabase {

        private static AppDatabase INSTANCE;

        public abstract PalabraDao palabraDao();
        public abstract UserDao userDao();

        public static AppDatabase getInMemoryDatabase(Context context) {
            if (INSTANCE == null) {
                INSTANCE =
                        Room.databaseBuilder(context, AppDatabase.class, "AppDatabase")
                                .allowMainThreadQueries()
                                .build();
            }
            return INSTANCE;
        }

        public static void destroyInstance() {
            INSTANCE = null;
        }
    }
