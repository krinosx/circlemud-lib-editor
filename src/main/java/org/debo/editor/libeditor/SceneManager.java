package org.debo.editor.libeditor;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {
    public static final String SCENE_HOME = "open-screen.fxml";
    public static final String SCENE_EDIT_WORLD = "world-editor.fxml";
    public static final String SCENE_EDIT_MOB = "mob-editor.fxml";
    public static final String SCENE_EDIT_OBJ = "mob-editor.fxml";
    public static final String SCENE_EDIT_SHOP = "mob-editor.fxml";
    public static final String SCENE_EDIT_QUEST = "mob-editor.fxml";
    public static final String SCENE_EDIT_ZONA = "mob-editor.fxml";


    private static SceneManager instance = null;

    public static SceneManager getInstance(){
        if(instance == null){
            throw new IllegalStateException("Instance not initialized. It must be initialized on the main application start method.");
        }
        return instance;
    }

    SceneManager(Stage stage){
        if(stage == null){
            throw new IllegalArgumentException("Stage must not be null!");
        }
        this.stage = stage;
        instance = this;
    }

    public final Stage stage;

    public void changeScene(String sceneFile) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SceneManager.class.getResource(sceneFile));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    public <T> SceneData<T> getSceneData(String sceneFile) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SceneManager.class.getResource(sceneFile));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);

        return new SceneData<>(fxmlLoader, scene, stage, fxmlLoader.getController());

    }

    void foo() throws IOException {
        SceneData<OpenScreenController> sceneData = this.getSceneData("");


    }

    public record SceneData<T>(FXMLLoader loader, Scene scene, Stage stage, T controller){}

}
