/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfaces;

import clases.ProcesadorArchivos;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

/**
 * FXML Controller class
 *
 * @author TSB_Team
 */
public class InterfazFXMLController implements Initializable {

    @FXML
    private TextField txtBusqueda;
    private Label lblArchivo;
    @FXML
    private Button btnBuscar;
    @FXML
    private Button btnLimpiar;
    @FXML
    private Label lblContador;
    @FXML
    private TextArea txtArchivo;
    @FXML
    private Label lblDistintos;
    @FXML
    private Label lblResultado;
    
    private ProcesadorArchivos procesador;
    private boolean cargado;
    @FXML
    private Button btnGrabar;
    @FXML
    private Label lblGrabar;
    @FXML
    private Button btnAgregar;
    @FXML
    private TextArea txtTabla;
    @FXML
    private Button btnReiniciar;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        File f = new File("archivoTabla.dat");
        System.out.println(f.getAbsolutePath());
        if (f.isFile() && (f.exists() && !f.isDirectory())) {
            this.procesador = new ProcesadorArchivos(f);
            cargado=true;
            lblDistintos.setText(String.valueOf(procesador.getCount()));
            txtTabla.setText(procesador.toString());
        }else{
            this.procesador = new ProcesadorArchivos();
            this.cargado = false;
        }
        
    }  

    @FXML
    private void handleButtonAgregar(ActionEvent event) throws IOException {
        lblGrabar.setText("");
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Abrir archivo de texto");
        
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos de texto (*.txt)", "*.txt"));
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            limpiarBusqueda();
            String r = txtArchivo.getText() + "\n\t" + file.getAbsolutePath() ;
            txtArchivo.setText(r);
            procesador.procesarPorPalabra(file);
            cargado=true;
            lblDistintos.setText(String.valueOf(procesador.getCount()));
            txtTabla.setText(procesador.toString());
        }
    }

    @FXML
    private void handleButtonBuscar(ActionEvent event) {
        lblGrabar.setText("");
        if (cargado) {
            if (txtBusqueda.getText().isEmpty()) {
                lblResultado.setTextFill(Color.RED);
                lblResultado.setText("No se ingreso ninguna palabra");
            } else {
                if (procesador.buscarPalabra(txtBusqueda.getText())> 0) {
                    lblResultado.setTextFill(Color.GREEN);
                    lblResultado.setText("Palabra encontrada");
                    lblContador.setText(String.valueOf(procesador.buscarPalabra(txtBusqueda.getText())));
                } else {
                    lblResultado.setTextFill(Color.RED);
                    lblResultado.setText("Palabra NO encontrada");
                }
            }
        }
        else{
            lblResultado.setTextFill(Color.RED);
            lblResultado.setText("No se cargó archivo aún");
        }
    }
    
    @FXML
    private void handleButtonGrabar(ActionEvent event) {
        if (procesador.grabar()) {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Grabar");
            a.setHeaderText("Se grabó la tabla con exito.");
            a.show();
        }else {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Grabar");
            a.setHeaderText("Error al grabar la tabla.");
            a.show();
        }
    }
    
    @FXML
    private void handleButtonLimpiar(ActionEvent event) {
        limpiarBusqueda();
    }

    private void limpiarBusqueda() {
        txtBusqueda.setText("");
        lblContador.setText("0");
        lblResultado.setText("");
        lblGrabar.setText("");
    }
}
