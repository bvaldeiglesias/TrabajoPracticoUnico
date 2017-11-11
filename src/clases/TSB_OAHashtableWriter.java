/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clases;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 *
 * @author TSB_Team
 */
public class TSB_OAHashtableWriter implements Serializable{
 
    private String archivo = "archivoTabla.dat";

    public TSB_OAHashtableWriter() {
    }

    public TSB_OAHashtableWriter(String nombre) {
        archivo = nombre;
    }

    public boolean grabarTabla(TSB_OAHashtable tabla) {   
        try {
            FileOutputStream fileOutput = new FileOutputStream(archivo);
            ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput);
            objectOutput.writeObject(tabla);

            objectOutput.close();
            fileOutput.flush();
            return true;

        }
        catch(IOException ex ){
            File f = new File("archivoTabla.dat");
            f.delete();
            return false;
        }
    }
}
