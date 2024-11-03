module org.debo.editor.libeditor {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires mapdb;

    opens org.debo.editor.libeditor to javafx.fxml;
    exports org.debo.editor.libeditor;
}