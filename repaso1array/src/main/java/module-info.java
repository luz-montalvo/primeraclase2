module pe.edu.upeu.repaso1array {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens pe.edu.upeu.repaso1array to javafx.fxml;
    exports pe.edu.upeu.repaso1array;
}