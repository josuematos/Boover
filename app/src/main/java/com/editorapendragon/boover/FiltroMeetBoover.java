package com.editorapendragon.boover;

/**
 * Created by Josue on 02/03/2017.
 */

public class FiltroMeetBoover {
    private String nome;
    private Double valor;

    public FiltroMeetBoover(String nome, Double valor) {
        this.nome = nome;
        this.valor = valor;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

}
