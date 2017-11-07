/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clases;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 *
 * @author default
 */
public class TSB_OAHashtableWriter{
 
    private String archivo = "archivoTabla.dat";

    public TSB_OAHashtableWriter() {
    }

    public TSB_OAHashtableWriter(String nombre) {
        archivo = nombre;
    }

    public void grabarTabla(TSB_OAHashtable tabla) {
                
        try {
            FileOutputStream fileOutput = new FileOutputStream(archivo);
            ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput);
            objectOutput.writeObject(tabla);

            objectOutput.close();
            fileOutput.flush();
            System.out.println("**TABLA GRABADA EXITOSAMENTE**");

        }
        catch(IOException ex ){
            System.out.println("** ERROR DE I/O **" +"\n " + ex.getMessage());
        }
    }
}
