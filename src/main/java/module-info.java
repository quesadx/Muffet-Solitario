module cr.ac.una.muffetsolitario {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.logging;
    requires MaterialFX;
    requires jakarta.persistence;
    requires javafx.graphics;
    requires java.base;
    requires java.instrument;

    opens cr.ac.una.muffetsolitario to javafx.fxml;
    opens cr.ac.una.muffetsolitario.controller to javafx.fxml; 
    //opens cr.ac.una.muffetsolitario.util to javafx.fxml; este toca ponerlo después supongo, ahí lo dejo
    exports cr.ac.una.muffetsolitario;
    exports cr.ac.una.muffetsolitario.model;
    opens cr.ac.una.muffetsolitario.model;
}