module cr.ac.una.muffetsolitario {
    requires javafx.controls;
    requires javafx.fxml;

    opens cr.ac.una.muffetsolitario to javafx.fxml;
    exports cr.ac.una.muffetsolitario;
}
