package com.editorapendragon.boover;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Josue on 28/01/2017.
 */

@IgnoreExtraProperties
public class User {
    public String email;
    public String nome;
    public String sobrenome;
    public String sexo;
    public String data_nascimento;
    public String interesse;
    public String procurando;
    public String categoria;
    public String Day;
    public String Month;
    public String Year;


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, String nome, String sobrenome, String sexo, String data_nascimento , String interesse, String procurando, String mDay, String mMonth, String mYear, String categoria) {
        this.email = email;
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.sexo = sexo;
        this.data_nascimento = data_nascimento;
        this.interesse = interesse;
        this.Day = mDay;
        this.Month = Integer.toString(Integer.parseInt(mMonth)+1);
        this.Year = mYear;
        this.procurando = procurando;
        this.categoria = categoria;
    }
}
