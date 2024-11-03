package org.debo.editor.libeditor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.paint.Paint;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.debo.persistence.Database;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class OpenScreenController implements Initializable {
    private boolean libChosen = false;
    public static final String SETTINGS_LIB_LOCATION = "lib_location";

    @FXML
    TextField libFolderTxt;

    public void openWorldEditor(ActionEvent event) {
        if (!validateChosenLib()) {
            return;
        }
        try {
            SceneManager.SceneData<WorldEditorController> sceneData = sceneManager.getSceneData(SceneManager.SCENE_EDIT_WORLD);
            WorldEditorController controller = sceneData.controller();
            controller.setLibFolderPath(libFolderTxt.getText());

            sceneData.stage().setScene(sceneData.scene());
            sceneData.stage().show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void openMobEditor(ActionEvent event) {
        openScene(SceneManager.SCENE_EDIT_MOB);
    }

    public void openObjEditor(ActionEvent event) {
        openScene(SceneManager.SCENE_EDIT_OBJ);
    }

    public void openShopEditor(ActionEvent event) {
        openScene(SceneManager.SCENE_EDIT_SHOP);
    }

    public void openQuestEditor(ActionEvent event) {
        openScene(SceneManager.SCENE_EDIT_QUEST);
    }

    public void openZonaEditor(ActionEvent event) {
        openScene(SceneManager.SCENE_EDIT_ZONA);
    }


    private void openScene(String sceneFile) {

    }

    public void chooseLibFolder(ActionEvent event) {
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setTitle("Selecione o diretorio raiz da LIB");
        File libFolder = fileChooser.showDialog(getStageFromEvent(event));

        libChosen = validateLibFolder(libFolder);

        if (libChosen) {
            System.out.println("Found a valid lib. Saving");
            Database.getInstance().getSettingsDatabase().put(SETTINGS_LIB_LOCATION, libFolder.getAbsolutePath());
            Database.getInstance().commit();
        }

        // paint text as RED
        updateLibFolderUi(libChosen, libFolder.getAbsolutePath());
    }


    private void updateLibFolderUi(boolean valid, String path) {
        // paint text as green
        libFolderTxt.setText(path);
        Background green = Background.fill(Paint.valueOf("GREEN"));
        Background red = Background.fill(Paint.valueOf("RED"));
        libFolderTxt.setBackground(valid ? green : red);
    }


    private boolean validateChosenLib() {
        if (!libChosen) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Selecione a pasta da LIB que quer editar.");
            alert.show();
            return false;
        }
        return true;
    }


    private boolean validateLibFolder(File folder) {
        libChosen = false;
        
        if (folder == null) {
            return false;
        }
        if (!folder.isDirectory()) {
            return false;
        }


        File[] folders = folder.listFiles((file, s) -> s.equalsIgnoreCase("world"));
        if (folders == null || folders.length != 1) {
            return false;
        }

        String[] list = folders[0].list();
        if (list == null) {
            return false;
        }
        // check basic folders
        long count = Arrays.stream(list).filter(s ->
                s.equalsIgnoreCase("mob") ||
                        s.equalsIgnoreCase("obj") ||
                        s.equalsIgnoreCase("quests") ||
                        s.equalsIgnoreCase("shp") ||
                        s.equalsIgnoreCase("wld") ||
                        s.equalsIgnoreCase("zon")
        ).count();

        this.libChosen = count == 6;
        return libChosen;
    }

    private Stage getStageFromEvent(ActionEvent event) {
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }

    SceneManager sceneManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sceneManager = SceneManager.getInstance();
        libFolderTxt.setEditable(false);

        String lib_location = Database.getInstance().getSettingsDatabase().get(SETTINGS_LIB_LOCATION);
        if(lib_location != null && !lib_location.isBlank()){
            System.out.println("Found lib from previous session");
            File libFolder = new File(lib_location);
            boolean isValid = validateLibFolder(libFolder);
            updateLibFolderUi(isValid, libFolder.getAbsolutePath());
        }
    }
}
