package com.dmwm.sopproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATA_BASE_NAME="gemMobile";
    public DatabaseHelper(@Nullable Context context) {
        super(context,DATA_BASE_NAME,null,0);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {



        db.execSQL("create table IF NOT EXISTS Clients (ID INTEGER PRIMARY KEY ,nom TEXT,adresse TEXT,tel TEXT,fax TEXT,email TEXT,contact TEXT,telcontact TEXT,valsync INTEGER )");

        db.execSQL("create table  IF NOT EXISTS employes (ID INTEGER PRIMARY KEY ,login,TEXT,pwd TEXT,prenom TEXT,nom TEXT,email TEXT,actif TEXT,valsync INTEGRE)");


        db.execSQL("create table IF NOT EXISTS sites (ID INTEGER PRIMARY KEY ,longitude TEXT,latitude TEXT,adresse TEXT,rue TEXT," +
                "codepostal TEXT,ville TEXT,contact TEXT,telcontact TEXT,client_id INTEGER,valsync INTEGER ,FOREIGN KEY(client_id) REFERENCES Clients(ID))");

        db.execSQL("create table IF NOT EXISTS priorites (ID INTEGER PRIMARY KEY ,nom TEXT,valsync INTEGER)");

        db.execSQL("create table IF NOT EXISTS contacts (ID INTEGER PRIMARY KEY ,detedebut TEXT,detefin TEXT,redevence TEXT,client_id INTEGER, valsync INTEGER,FOREIGN KEY(client_id) REFERENCES Clients(ID))");

        db.execSQL("create table IF NOT EXISTS employes_interventions (ID INTEGER PRIMARY KEY ,employe_id INTEGER,inervention_id INTEGER," +
                "FOREIGN KEY(inervention_id) REFERENCES interventions(ID),FOREIGN KEY(employe_id) REFERENCES employes(ID))");

        db.execSQL("create table IF NOT EXISTS images (ID INTEGER PRIMARY KEY ,nom TEXT,img TEXT,dateCapture TEXT,intervention_id INTEGER,valsync INTEGER,FOREIGN KEY(inervention_id) REFERENCES interventions(ID))");

        db.execSQL("create table IF NOT EXISTS interventions (ID INTEGER PRIMARY KEY ,titre TEXT,datedebut TEXT,detefin TEXT,heuredebutplan TEXT,commentaire Text,heurebuteffect TEXT,heurefineffect TEXT,termine TEXT,datedeminaison TEXT," +
                "validee TEXT,datedevalidation TEXT,priorite_id INTEGER,site_id INTEGER,valsync INTEGER,FOREIGN KEY(priorite_id) REFERENCES priorites(ID),FOREIGN KEY(site_id) REFERENCES sites(ID))");

        db.execSQL("create table IF NOT EXISTS taches (ID INTEGER PRIMARY KEY ,reference TEXT,nom TEXT,dure TEXT,prix REAL,prixheure REAL," +
                "dateaction TEXT,intervention_id INTEGER,valsync INTEGE,FOREIGN KEY(inervention_id) REFERENCES interventions(ID))");




    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from employes",null);
        return res;
    }

    public boolean updateDataToEmploye(int ID ,String login,String pwd,String prenom ,String nom ,String email ,String actif ,int valsync) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID",ID);
        contentValues.put("login",login);
        contentValues.put("pwd",pwd);
        contentValues.put("prenom",prenom);
        contentValues.put("email",email);
        contentValues.put("actif",actif);
        contentValues.put("valsync",valsync);
        db.update("employes", contentValues, "ID = ?",new String[]{String.valueOf(ID)});
        return true;
    }
}
