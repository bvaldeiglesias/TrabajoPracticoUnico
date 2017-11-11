/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfaces;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
/**
 *
 * @author TSB_Team
 */
public class FXMain extends Application  {
    
    private Stage mainStage;
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("InterfazFXML.fxml"));
        
        Scene scene = new Scene(root);
        
        primaryStage.setScene(scene);
//        primaryStage.setOnCloseRequest(confirmCloseEventHandler);
        primaryStage.setTitle("Procesador de Archivos");
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
//    private EventHandler<WindowEvent> confirmCloseEventHandler = event -> {
//        Alert closeConfirmation = new Alert(
//                Alert.AlertType.CONFIRMATION,
//                "¿Desea grabar antes de salir?"
//        );
//        Button exitButton = (Button) closeConfirmation.getDialogPane().lookupButton(
//                ButtonType.OK
//        );
//        
//        exitButton.setText("Grabar y salir");
//        
//        closeConfirmation.setHeaderText("Confirmar cierre");
//        closeConfirmation.initModality(Modality.APPLICATION_MODAL);
//        closeConfirmation.initOwner(mainStage);
//
//
//        Optional<ButtonType> closeResponse = closeConfirmation.showAndWait();
//        if (!ButtonType.OK.equals(closeResponse.get())) {
//            event.consume();
//        }
//    };
    
}
