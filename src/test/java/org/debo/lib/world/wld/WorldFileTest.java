package org.debo.lib.world.wld;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class WorldFileTest {


    @Test
    void testLoadContent() {

        String absPath = new File("src/test/resources").getAbsolutePath();

        WorldFile worldFile = new WorldFile(absPath, "308.wld");

        List<Room> rooms = worldFile.getRooms();

        assertThat(rooms).isNotNull().hasSize(99);

    }



}