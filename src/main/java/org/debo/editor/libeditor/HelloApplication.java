package org.debo.editor.libeditor;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        SceneManager sceneManager = new SceneManager(stage);

        stage.setTitle("DeBo - Editor de Lib!");

        sceneManager.changeScene(SceneManager.SCENE_HOME);
    }

    public static void main(String[] args) {
        launch();
    }
}