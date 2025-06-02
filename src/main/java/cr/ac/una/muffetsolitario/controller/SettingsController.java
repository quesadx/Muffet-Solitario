/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.muffetsolitario.controller;

import java.net.URL;
import java.util.ResourceBundle;

import cr.ac.una.muffetsolitario.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author quesadx
 */
public class SettingsController extends Controller implements Initializable  {

    @FXML private AnchorPane root;
    @FXML private ImageView imgBackground;
    
    @FXML private MFXButton btnBGM;
    @FXML private Label lblSignUpTitle;
    @FXML private MFXTextField txfNewUsername;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Bind the background ImageView to the root pane
        imgBackground.fitHeightProperty().bind(root.heightProperty());
        imgBackground.fitWidthProperty().bind(root.widthProperty());
    }

    @Override
    public void initialize() {
        
    }

    @FXML void onActionConfirmNewUserName(ActionEvent event) {
        // TODO: Revisar si es que no lo cambió y revisar si está disponible
    }

    @FXML
    void onActionExitSettings(ActionEvent event) {
        FlowController.getInstance().goView("LogInView");
    }

    @FXML
    void onActionToggleBtnBGM(ActionEvent event) {
        // TODO: Obtener el estado actual del ajuste y setear el toggler
    }
    
}
