/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.muffetsolitario.controller;

import java.net.URL;
import java.util.ResourceBundle;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author quesadx
 */
public class PrincipalController extends Controller implements Initializable {

    @FXML private BorderPane root;
    @FXML private MFXButton btnMinimize;
    @FXML private MFXButton btnMaximize;
    @FXML private MFXButton btnClose;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @Override
    public void initialize() {
        // throw new UnsupportedOperationException("Unimplemented method 'initialize'");
    }

    @FXML
    private void onActionMinimize(ActionEvent event) {
        ((Stage) root.getScene().getWindow()).setIconified(true);
    }

    @FXML
    private void onActionMaximize(ActionEvent event) {
        ((Stage) root.getScene().getWindow()).setFullScreen(true);
    }

    @FXML
    private void onActionBtnClose(ActionEvent event) {
        ((Stage) root.getScene().getWindow()).close();
    }
    
}
