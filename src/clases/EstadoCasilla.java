package clases;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Fer
 */
public enum EstadoCasilla {

    OPEN("Open"), CLOSE("Close"), TUMBSTONE("Tumbstone"),; 
    private String estadoCasilla;

    private EstadoCasilla(String estadoCasilla) {
        this.estadoCasilla = estadoCasilla;
    }

    public String getEstadoCasilla() {
        return estadoCasilla;
    }

    public void setEstadoCasilla(String estadoCasilla) {
        this.estadoCasilla = estadoCasilla;
    }
}