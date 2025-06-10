package cr.ac.una.muffetsolitario.controller;

import java.net.URL;
import java.util.ResourceBundle;

import cr.ac.una.muffetsolitario.model.UserAccountDto;
import cr.ac.una.muffetsolitario.service.UserAccountService;
import cr.ac.una.muffetsolitario.util.FlowController;
import cr.ac.una.muffetsolitario.util.Respuesta;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class PasswordResetController extends Controller implements Initializable {

    @FXML private AnchorPane root;
    @FXML private ImageView imgBackground;
    @FXML private Label lblSignUpTitle;
    @FXML private MFXTextField txfUser;
    @FXML private MFXTextField txfCurrentFavoriteWord;
    @FXML private MFXPasswordField psfNewPassword;
    @FXML private MFXPasswordField psfNewConfirmPassword;
    @FXML private MFXButton btnCancel;
    @FXML private VBox vboxMissingFieldsAlert;
    @FXML private MFXButton btnConfirmAlert;
    @FXML private VBox vboxWrongFavoriteWord;
    @FXML private VBox vboxPasswordUpdated;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        imgBackground.fitHeightProperty().bind(root.heightProperty());
        imgBackground.fitWidthProperty().bind(root.widthProperty());
        hideAllAlerts();
    }

    @Override
    public void initialize() {
        hideAllAlerts();
    }

    private void hideAllAlerts() {
        vboxMissingFieldsAlert.setVisible(false);
        vboxMissingFieldsAlert.setManaged(false);
        vboxWrongFavoriteWord.setVisible(false);
        vboxWrongFavoriteWord.setManaged(false);
        vboxPasswordUpdated.setVisible(false);
        vboxPasswordUpdated.setManaged(false);
    }

    private void clearFields() {
        txfUser.clear();
        txfCurrentFavoriteWord.clear();
        psfNewPassword.clear();
        psfNewConfirmPassword.clear();
    }

    @FXML
    private void onActionBtnCancel(ActionEvent event) {
        clearFields();
        hideAllAlerts();
        FlowController.getInstance().goView("LogInView");
    }

    @FXML
    private void onActionBtnConfirmPasswordReset(ActionEvent event) {
        hideAllAlerts();

        String username = txfUser.getText();
        String favWord = txfCurrentFavoriteWord.getText();
        String newPassword = psfNewPassword.getText();
        String confirmPassword = psfNewConfirmPassword.getText();

        // Check for missing fields
        if (username == null || username.isBlank() ||
            favWord == null || favWord.isBlank() ||
            newPassword == null || newPassword.isBlank() ||
            confirmPassword == null || confirmPassword.isBlank()) {
            vboxMissingFieldsAlert.setVisible(true);
            vboxMissingFieldsAlert.setManaged(true);
            return;
        }

        // Check if passwords match
        if (!newPassword.equals(confirmPassword)) {
            vboxMissingFieldsAlert.setVisible(true);
            vboxMissingFieldsAlert.setManaged(true);
            return;
        }

        UserAccountService userAccountService = new UserAccountService();
        Respuesta userResp = userAccountService.getUserAccountByNickname(username);

        System.out.println("PasswordReset: getUserAccountByNickname response: " + userResp);
        System.out.println("Estado: " + userResp.getEstado());
        System.out.println("Mensaje: " + userResp.getMensaje());
        System.out.println("Resultado: " + userResp.getResultado("UserAccount"));
        System.out.println("Username entered: '" + username + "'");

        if (!userResp.getEstado() || userResp.getResultado("UserAccount") == null) {
            vboxMissingFieldsAlert.setVisible(true);
            vboxMissingFieldsAlert.setManaged(true);
            return;
        }

        UserAccountDto userDto = (UserAccountDto) userResp.getResultado("UserAccount");

        // Check favorite word
        if (!userDto.getUserFavWord().toUpperCase().equals(favWord.trim().toUpperCase())) {
            vboxWrongFavoriteWord.setVisible(true);
            vboxWrongFavoriteWord.setManaged(true);
            return;
        }

        // Update password
        userDto.setUserPassword(newPassword);
        Respuesta updateResp = userAccountService.saveUserAccount(userDto);

        if (updateResp.getEstado()) {
            vboxPasswordUpdated.setVisible(true);
            vboxPasswordUpdated.setManaged(true);
            clearFields();
        } else {
            vboxMissingFieldsAlert.setVisible(true);
            vboxMissingFieldsAlert.setManaged(true);
        }
    }

    @FXML
    private void onActionBtnConfirmAlert(ActionEvent event) {
        hideAllAlerts();
    }

    @FXML
    private void onActionBtnConfirmPasswordResetAlert(ActionEvent event) {
        clearFields();
        hideAllAlerts();
        FlowController.getInstance().goView("LogInView");
    }
}