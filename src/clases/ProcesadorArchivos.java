/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clases;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Bruno
 */
public class ProcesadorArchivos implements Serializable {

    private File file;
    private TSB_OAHashtable tabla;
    private FileInputStream fileInput;
    private ObjectInputStream objectInput;
    private FileOutputStream fileOutput;
    private ObjectOutputStream objectOutput;
    private ArrayList<String> arreglo = new ArrayList<>();

    public ProcesadorArchivos() {
        this.tabla = new TSB_OAHashtable();
    }

    public void cargarArchivo(File file) throws FileNotFoundException, IOException {
        this.file = file;
        fileInput = new FileInputStream(file);
        objectInput = new ObjectInputStream(fileInput);

    }

    public StringBuilder procesar(File file) throws FileNotFoundException, IOException {
        this.file = file;

        FileReader fileReader = new FileReader(file);

        BufferedReader bufferReader = new BufferedReader(fileReader);

        String proximaLinea = bufferReader.readLine();
        StringBuilder texto = new StringBuilder();
        while (proximaLinea != null) {

            String vector[] = proximaLinea.split(" ");
            for (int j = 0; j < vector.length; j++) {

                if (j == vector.length - 1) {
                    texto.append(vector[j].replaceAll("\\P{L}+", "")).append("\n");
                } else {
                    texto.append(vector[j].replaceAll("\\P{L}+", "")).append(" ");
                }

            }

            proximaLinea = bufferReader.readLine();
        }

        System.out.println(texto);
        return texto;
    }

    public void procesarPorPalabra(File file) {
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNext()) //hasNextInt se fija is hay un proximo int
            {
                String palabra = sc.next();
                palabra = palabra.replaceAll("[-+.^:,?¿¡!]", "");
                palabra = palabra.toUpperCase();
                insertarPalabra(palabra);
                //arreglo.add(palabra);
                //System.out.println(palabra);
            }

            //System.out.println(arreglo.toString());

        } catch (FileNotFoundException e) {
            System.err.println("No existe el archivo");
        }
        
        tabla.toString();
    }

    public ArrayList getArreglo() {
        return arreglo;
    }

    public void insertarPalabra(String text) {
        int value = 0;
        if (tabla.get(text) != null) {
            value += 1;
            tabla.put(text, value);
        } else {
            tabla.put(text, value);
        }
    }

    public int buscarPalabra(String text) {
        int value = 0;
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

    public TSB_OAHashtable leerTabla() {
        try {
            fileInput = new FileInputStream(file);
            objectInput = new ObjectInputStream(fileInput);
            tabla = (TSB_OAHashtable) objectInput.readObject();

            objectInput.close();
            fileInput.close();
        } catch (IOException ex) {
            System.out.println("** ERROR DE I/O **" + "\n " + ex.getMessage());
        } catch (ClassNotFoundException ex) {
            System.out.println("** ERROR... CLASE NO ENCONTRADA **" + "\n " + ex.getMessage());
        }
        return tabla;
    }

    public void grabarTabla() {
        try {
            fileOutput = new FileOutputStream(file);
            objectOutput = new ObjectOutputStream(fileOutput);
            objectOutput.writeObject(tabla);

            objectOutput.close();
            fileOutput.flush();
        } catch (IOException ex) {
            System.out.println("** ERROR DE I/O **" + "\n " + ex.getMessage());
        }
    }
}
