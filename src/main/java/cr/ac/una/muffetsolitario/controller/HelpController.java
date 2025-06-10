/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.muffetsolitario.controller;

import java.net.URL;
import java.util.ResourceBundle;

import cr.ac.una.muffetsolitario.util.FlowController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author quesadx
 */
public class HelpController extends Controller implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private ImageView imgBackground;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        imgBackground.fitHeightProperty().bind(root.heightProperty());
        imgBackground.fitWidthProperty().bind(root.widthProperty());
    }    

    @Override
    public void initialize() {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @FXML
    private void onActionBtnExit(ActionEvent event) {
        FlowController.getInstance().goView("LogInView");
    }
    
}
