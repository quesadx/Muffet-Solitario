module cr.ac.una.muffetsolitario {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires MaterialFX;
    requires jakarta.persistence;

    opens cr.ac.una.muffetsolitario to javafx.fxml;
    opens cr.ac.una.muffetsolitario.controller to javafx.fxml; 
    //opens cr.ac.una.muffetsolitario.util to javafx.fxml; este toca ponerlo después supongo, ahí lo dejo
    exports cr.ac.una.muffetsolitario;
}