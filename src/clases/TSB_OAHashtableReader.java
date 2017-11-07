/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clases;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 *
 * @author default
 */
public class TSB_OAHashtableReader implements Serializable {
    
    private String archivo = "archivoTabla.dat";

    public TSB_OAHashtableReader() {
    }

    public TSB_OAHashtableReader(String nombre) {
        archivo = nombre;
    }

    public TSB_OAHashtable leerTabla() {

        TSB_OAHashtable tabla = null;
        try {
            FileInputStream fileInput = new FileInputStream(archivo);
            ObjectInputStream objectInput = new ObjectInputStream(fileInput);
            tabla = (TSB_OAHashtable) objectInput.readObject();

            objectInput.close();
            fileInput.close();
            
            System.out.println("**TABLA CARGADA CON EXITO**");
        }
        catch(IOException ex ){
            System.out.println("** ERROR DE I/O **" +"\n " + ex.getMessage());
        }
        catch(ClassNotFoundException ex){
            System.out.println("** ERROR... CLASE NO ENCONTRADA **" +"\n " + ex.getMessage());
        }
        
        return tabla;
    }
}
