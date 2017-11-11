/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clases;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Scanner;

/**
 *
 * @author TSB_Team
 */
public class ProcesadorArchivos implements Serializable {

    private File file;
    private TSB_OAHashtable tabla;
    private FileInputStream fileInput;
    private ObjectInputStream objectInput;

    public ProcesadorArchivos() {
        this.tabla = new TSB_OAHashtable();
    }

    public ProcesadorArchivos(File f) {
        this.tabla = this.leer(f);
    }


    public void procesarPorPalabra(File file) {
        try (Scanner sc = new Scanner(file,"ISO-8859-1")) {
            while (sc.hasNext()) //hasNextInt se fija is hay un proximo int
            {
                    String palabra = sc.next();
                    palabra = palabra.replaceAll("\\P{L}+", "");
                    palabra = palabra.toUpperCase();
                    if (!"".equals(palabra)){
                        insertarPalabra(palabra);
                    }
            }
        } catch (FileNotFoundException e) {
            System.err.println("No existe el archivo");
        }
        
        tabla.toString();
    }

    public void insertarPalabra(String text) {
        if (tabla.containsKey(text)) {
            int value = (int) tabla.get(text) + 1;
            tabla.put(text, value);
        } else {
            tabla.put(text, 1);
        }
    }

    public int buscarPalabra(String text) {
        int value = 0;
        text = text.replaceAll("[-+.^:,?¿¡!_*;<>()%«$#@»°'0123456789]", "");
        text = text.toUpperCase();
        if (tabla.get(text) != null) {
            value = (int) tabla.get(text);
        }
        return value;
    }

    /**
     * Retorna la cantidad de palabras que contiene la tabla en un momento dado
     *
     * @return el tamaño de la tabla (cantidad de palabras distintas)
     */
    public int getCount() {
        return tabla.size();
    }

    @Override
    public String toString() {
        return tabla.toString();
    }
    
    public TSB_OAHashtable getTabla(){
        return tabla;
    }

    public boolean grabar() {
        TSB_OAHashtableWriter writer = new TSB_OAHashtableWriter();
        return writer.grabarTabla(tabla);
        
    }
    
    public TSB_OAHashtable leer(File f) {
        TSB_OAHashtableReader reader = new TSB_OAHashtableReader(f);
        return reader.leerTabla();
    }
}
