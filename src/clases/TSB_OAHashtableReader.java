/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clases;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 *
 * @author TSB_Team 
 */
public class TSB_OAHashtableReader implements Serializable {
    
    private File archivo;

    public TSB_OAHashtableReader() {
    }

    public TSB_OAHashtableReader(File f) {
        archivo = f;
    }

    public TSB_OAHashtable leerTabla() {

        TSB_OAHashtable tabla = null;
        try {
            FileInputStream fileInput = new FileInputStream(archivo);
            ObjectInputStream objectInput = new ObjectInputStream(fileInput);
            tabla = (TSB_OAHashtable) objectInput.readObject();

            objectInput.close();
            fileInput.close();
        }
        catch(IOException ex ){
            return null;
        }
        catch(ClassNotFoundException ex){
            return null;
        }
        
        return tabla;
    }
}
