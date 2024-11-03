package org.debo.editor.libeditor;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.debo.lib.world.ParsingException;
import org.debo.lib.world.wld.Room;
import org.debo.lib.world.wld.WorldFile;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class WorldEditorController implements Initializable {

    @FXML
    private ListView<String> zoneListView;

    @FXML
    private ListView<String> roomListView;

    @FXML
    private TextField editorRoomTitle;

    @FXML
    private TextArea editorRoomText;


    private String libFolderPath;

    private String worldFileRootPath;


    private final List<Room> activeRooms = new ArrayList<>();

    private Room activeRoom;

    public void setActiveRoom(Room activeRoom) {
        this.activeRoom = activeRoom;

        if(this.activeRoom == null) {
            editorRoomTitle.clear();
            editorRoomText.clear();
            return;
        }

        editorRoomTitle.setText(this.activeRoom.getName());
        editorRoomText.setText(this.activeRoom.getDescription());
    }

    public void setActiveRooms(List<Room> activeRooms) {
        this.activeRooms.clear();
        this.activeRooms.addAll(activeRooms);
    }

    private void setActiveRoom(String roomVnum){
        this.activeRoom = null;
        for(Room room: activeRooms){
            if(room.getVNUM() == Integer.parseInt(roomVnum)){
                setActiveRoom(room);
                return;
            }
        }
    }


    public void setLibFolderPath(String libFolderPath) {
        System.out.println("Setting LibFolder Path " + libFolderPath);
        this.libFolderPath = libFolderPath;
        this.worldFileRootPath = libFolderPath + File.separator + "world" + File.separator + "wld";
        loadZoneListView();
    }

    public void loadZoneListView() {
        System.out.println("Loading world files.");
        if (libFolderPath != null && !libFolderPath.isBlank()) {
            File file = new File(worldFileRootPath);
            if (!file.isDirectory()) {
                System.out.println(file.getAbsolutePath() + " is not a folder.");
                return;
            }
            String[] list = file.list((file1, s) -> s.endsWith(".wld"));
            if (list != null) {
                zoneListView.getItems().addAll(list);
            } else {
                System.out.println("No files with world folder.");
            }
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }


    public void onRoomClicked(MouseEvent event) {
        if (event.getClickCount() == 2) {
            System.out.println("Room clicked: " + roomListView.getSelectionModel().getSelectedItem());
            setActiveRoom(roomListView.getSelectionModel().getSelectedItem());

        }
    }

    public void onWorldFileClicked(MouseEvent event){

        if(event.getClickCount() == 2){
            String fullPath =  worldFileRootPath + File.separator + zoneListView.getSelectionModel().getSelectedItem();


            LoadRoomsTask task = new LoadRoomsTask(fullPath, roomListView, this );

            System.out.println("Starting thread to load files.");
            Thread thread = new Thread(task);
            thread.setName(fullPath);
            thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable throwable) {
                    System.out.println("Uncaught exception on thread: " + thread.getName());
                    System.out.println(throwable.getMessage());
                    throwable.printStackTrace();
                }
            });

            thread.start();
        }
    }

    private static class LoadRoomsTask extends Task<Void>{

        private final WorldEditorController controller;
        private final String filePath;
        ListView<String> roomListView;
        LoadRoomsTask(String filePath, ListView<String> roomListView, WorldEditorController controller) {
            this.filePath = filePath;
            this.roomListView = roomListView;
            this.controller = controller;
        }

        @Override
        protected Void call() throws Exception {
            System.out.println("Starting Daemon thread: " + Thread.currentThread().getName());
            File worldFile = new File(filePath);
            if(!worldFile.isFile() || !worldFile.canRead()) {
                System.out.println(filePath + " is not a valid file or we cannot read.");
               return null;
            }

            try {
                WorldFile wf = new WorldFile(filePath);
                List<Room> rooms = wf.getRooms();

                System.out.println("Loaded " + rooms.size() + " rooms.");
                controller.setActiveRooms(rooms);

                Platform.runLater(() -> {
                    System.out.println("Updating the  visual component.");
                    roomListView.getItems().clear();
                    roomListView.getItems().addAll(rooms.stream().map(room -> String.valueOf(room.getVNUM())).toList());
                });
            } catch (ParsingException e){
                System.out.println("Failed to load rooms. "+ e.getMessage());
                e.printStackTrace(); // TODO: Replace the printstacktrace with some UI Element to show the error.
                throw e;
            } catch (Exception e) {
                System.out.println("Unexpected error. "+ e.getMessage());
                e.printStackTrace();
                throw e;
            }

            return null;
        }
    }


}
