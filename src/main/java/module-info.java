module cr.ac.una.muffetsolitario {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
//    opens cr.ac.una.muffetsolitario to javafx.fxml;
//    exports cr.ac.una.muffetsolitario;


    opens cr.ac.una.muffetsolitario to javafx.fxml;
    opens cr.ac.una.muffetsolitario.view to javafx.fxml;
    opens cr.ac.una.muffetsolitario.controller to javafx.fxml;

    exports cr.ac.una.muffetsolitario;
    exports cr.ac.una.muffetsolitario.controller to javafx.fxml;
    
}
