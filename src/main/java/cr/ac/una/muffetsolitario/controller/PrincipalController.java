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
import javafx.scene.layout.HBox;
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
    @FXML private HBox hboxWindowBar;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Enable window dragging via hboxWindowBar
        final Delta dragDelta = new Delta();
        hboxWindowBar.setOnMousePressed(event -> {
            Stage stage = (Stage) root.getScene().getWindow();
            dragDelta.x = stage.getX() - event.getScreenX();
            dragDelta.y = stage.getY() - event.getScreenY();
            if (event.getClickCount() == 2) {
                // Double click to maximize/restore
                if (stage.isFullScreen()) {
                    stage.setFullScreen(false);
                } else {
                    stage.setFullScreen(true);
                }
            }
        });
        hboxWindowBar.setOnMouseDragged(event -> {
            Stage stage = (Stage) root.getScene().getWindow();
            if (!stage.isFullScreen()) {
                stage.setX(event.getScreenX() + dragDelta.x);
                stage.setY(event.getScreenY() + dragDelta.y);
            }
        });
        // Remove focus from window buttons at startup
        btnMinimize.setFocusTraversable(false);
        btnMaximize.setFocusTraversable(false);
        btnClose.setFocusTraversable(false);
        btnMinimize.setStyle("-fx-focus-color: transparent; -fx-faint-focus-color: transparent;");
        btnMaximize.setStyle("-fx-focus-color: transparent; -fx-faint-focus-color: transparent;");
        btnClose.setStyle("-fx-focus-color: transparent; -fx-faint-focus-color: transparent;");
        // Enable window resizing via root BorderPane
        WindowResizer.addResizeListeners(root);
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
    
    // Helper class for window dragging
    private static class Delta { double x, y; }
}
