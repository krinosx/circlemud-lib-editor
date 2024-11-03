package org.debo.lib.world.wld;

import org.debo.lib.world.ParsingException;
import org.debo.lib.world.ValidationException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a .wld file.
 * These files contain a list of {@link Room} definitions.
 */
public class WorldFile {
    //max allowed rooms on a single file (CircleMUD limitation)
    private static final int MAX_ROOMS = 100;
    List<Room> rooms = new ArrayList<>();

    String filename;
    /**
     * Full content of the file - We may need to optimize it at some point, for now, just let it go
     */
    String content;

    public WorldFile(String filePath) {
        this.filename = filePath;
    }

    public List<Room> getRooms() throws ParsingException {
        if (rooms.isEmpty()) {
            try {
                loadRooms();
            } catch (IOException | ParsingException e) {
                throw new ParsingException(Room.class, 0, e.getMessage());
            }
        }
        return rooms;
    }

    public void addRooms(Room room) throws ValidationException {
        if (rooms.size() >= MAX_ROOMS) {
            throw new ValidationException(WorldFile.class, "Too many rooms",
                    List.of("The maximum amount of rooms (100) for this file was already reached."));
        }

        if (!room.isValid()) {
            throw new ValidationException(Room.class, "Trying to add an invalid room.", room.getValidationErrors());
        }
        rooms.add(room);
    }

    public void loadRooms() throws IOException, ParsingException {
        loadContent();

        List<String> rooms = List.of(this.content.split("\nS\n"));

        for (String roomDescription : rooms) {
            if (roomDescription.charAt(0) == '$') { // EOF char
                continue;
            }
            roomDescription += "\nS\n";
            Room room = Room.roomFromText(roomDescription);
            this.rooms.add(room);
        }
    }


    private void loadContent() throws IOException {

        File file = new File(filename);


        if (!file.isFile() || !file.exists()) {
            throw new IOException("File not found");
        }
        if (!file.canRead()) {
            throw new IOException("Cant read the file.");
        }

        try (InputStream fileStream = new FileInputStream(file)) {
            content = new String(fileStream.readAllBytes(), StandardCharsets.ISO_8859_1);
        }
    }


}
