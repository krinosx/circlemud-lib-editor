package org.debo.lib.world.wld;

import org.debo.lib.world.ParsingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import static org.assertj.core.api.Assertions.*;


class RoomTest {

    private static String theRedRoom;

    @BeforeAll
    static void loadResources() throws IOException {
        ClassLoader classLoader = RoomTest.class.getClassLoader();
        try (InputStream resourceAsStream = classLoader.getResourceAsStream("Room_The_Red_room.wld")) {
            assert resourceAsStream != null;
            theRedRoom = new String(resourceAsStream.readAllBytes());
        }
    }

    @Test
    void roomToText() {
        Room room = new Room();
        room.setVNUM(18629);
        room.setName("The Red Room");
        room.setDescription("""
                  It takes you a moment to realize that the red glow here is
                  coming from a round portal on the floor. It looks almost as
                  if someone had painted a picture of a dirt running through a
                  field on the floor of this room. Oddly enough, it is so
                  realistic you can feel the wind in the field coming out of the
                  picture.""");

        room.setZoneNumber(186);
        room.setRoomFlags(List.of(Room.RoomFlag.INDOORS, Room.RoomFlag.DARK));
        room.setSectorType(Room.SectorType.INSIDE);

        Room.Exit exitNorth = new Room.Exit();
        exitNorth.direction = Room.Exit.Direction.NORTH;
        exitNorth.description = "You see a big room up there.";
        exitNorth.doorFlag = Room.Exit.DoorFlag.NO_DOOR;
        exitNorth.linkedRoom = 18620;

        Room.Exit exitEast = new Room.Exit();
        exitEast.direction = Room.Exit.Direction.EAST;
        exitEast.description = "You see a small room.";
        exitEast.keywords = "oak door";
        exitEast.doorFlag = Room.Exit.DoorFlag.NORMAL_DOOR;
        exitEast.keyVnum = 18000;
        exitEast.linkedRoom = 18630;

        room.exits[Room.Exit.Direction.NORTH.number] = exitNorth;
        room.exits[Room.Exit.Direction.EAST.number] = exitEast;


        Room.ExtraDescription portalFloor = new Room.ExtraDescription("portal floor",
                """
                        It looks as if you could go down into it... but you can?t be
                        sure of where you will end up, or if you can get back.""");
        room.extraDescriptions = List.of(portalFloor);

        String s = Room.roomToText(room);
        assertThat(s).isNotNull().isEqualTo(theRedRoom);
    }
    @Test
    void textToRoom() throws ParsingException {

        Room room = Room.roomFromText(theRedRoom);

        assertThat(room).isNotNull();

    }

}