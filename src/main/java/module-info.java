module org.example.homework_4 {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.sql;
    requires com.healthmarketscience.jackcess;
    requires org.controlsfx.controls;

    requires org.kordamp.bootstrapfx.core;
    requires com.google.gson;

    opens org.example.homework_4 to javafx.fxml, com.google.gson;
    exports org.example.homework_4;
}