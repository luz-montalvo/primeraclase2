module pe.edu.upeu.poorelaciones {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens pe.edu.upeu.poorelaciones to javafx.fxml;
    exports pe.edu.upeu.poorelaciones;
}