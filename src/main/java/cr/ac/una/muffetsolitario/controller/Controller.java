/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cr.ac.una.muffetsolitario.controller;

import javafx.scene.control.Control;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public abstract class Controller {

  private Stage stage;
  private String accion;
  private String nombreVista;

  public String getAccion() {
    return accion;
  }

  public void setAccion(String accion) {
    this.accion = accion;
  }

  public void setStage(Stage stage) {
    this.stage = stage;
  }

  public Stage getStage() {
    return stage;
  }

  public String getNombreVista() {
    return nombreVista;
  }

  public void setNombreVista(String nombreVista) {
    this.nombreVista = nombreVista;
  }

  public void sendTabEvent(KeyEvent event) {
    event.consume();
    KeyEvent keyEvent =
        new KeyEvent(KeyEvent.KEY_PRESSED, null, null, KeyCode.TAB, false, false, false, false);
    ((Control) event.getSource()).fireEvent(keyEvent);
  }

  public abstract void initialize();

    void updateItem(Image image, boolean empty) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
