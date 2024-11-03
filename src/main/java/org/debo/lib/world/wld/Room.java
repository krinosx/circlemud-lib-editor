package org.debo.lib.world.wld;

import org.debo.lib.world.ParsingException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Room {
    protected int VNUM;
    protected String name;
    protected String description;
    protected int zoneNumber;
    protected List<RoomFlag> roomFlags;
    protected SectorType sectorType;
    protected Exit[] exits = new Exit[Exit.Direction.values().length];
    protected List<ExtraDescription> extraDescriptions;

    public Room() {
    }

    public Room(int VNUM, String name, String description, int zoneNumber, List<RoomFlag> roomFlags, SectorType sectorType) {
        this.VNUM = VNUM;
        this.name = name;
        this.description = description;
        this.zoneNumber = zoneNumber;
        this.roomFlags = roomFlags;
        this.sectorType = sectorType;
    }

    public int getVNUM() {
        return VNUM;
    }

    public void setVNUM(int VNUM) {
        this.VNUM = VNUM;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getZoneNumber() {
        return zoneNumber;
    }

    public void setZoneNumber(int zoneNumber) {
        this.zoneNumber = zoneNumber;
    }

    public List<RoomFlag> getRoomFlags() {
        return roomFlags;
    }

    public void setRoomFlags(List<RoomFlag> roomFlags) {
        this.roomFlags = roomFlags;
    }

    public SectorType getSectorType() {
        return sectorType;
    }

    public void setSectorType(SectorType sectorType) {
        this.sectorType = sectorType;
    }

    public Exit[] getExits() {
        return exits;
    }

    public void setExits(Exit[] exits) {
        this.exits = exits;
    }

    @Override
    public String toString() {
        return String.format("{'class':'Room.clas', 'vnum': %d, 'name':'%s'}", VNUM, name);
    }

    public List<ExtraDescription> getExtraDescriptions() {
        return extraDescriptions;
    }

    public void setExtraDescriptions(List<ExtraDescription> extraDescriptions) {
        this.extraDescriptions = extraDescriptions;
    }

    /**
     * Check the minimum requirements for a room to be considered ready
     */
    public boolean isValid() {
        validate();
        return validationErrors.isEmpty();
    }

    private final List<String> validationErrors = new ArrayList<>();

    public List<String> getValidationErrors() {
        return List.copyOf(validationErrors);
    }

    public void validate() {
        validationErrors.clear();

        if (this.VNUM <= 0) {
            validationErrors.add("VNUM is required");
        }
        // Vnum and zone number must be in the same range
        if (this.zoneNumber < 0) {
            validationErrors.add("Zone number must be greater or equal to zero.");
        }

        int roomMinValue = zoneNumber * 100;
        int roomMaxValue = roomMinValue + 99;
        if (VNUM < roomMinValue || VNUM > roomMaxValue) {
            validationErrors.add(String.format("Incorrect zone number or room VNUM. Zone: %d  Minimum Room Number: %d Maximum Room Number %d.", zoneNumber, roomMinValue, roomMaxValue));
        }

        if (name == null || name.isBlank()) {
            validationErrors.add("Room name must not be empty.");
        }

        if (description == null || description.isEmpty()) {
            validationErrors.add("Room description must not be empty");
        }

        if (roomFlags == null || roomFlags.isEmpty()) {
            validationErrors.add("It's necessary to have at least one room flag.");
        }
        if (sectorType == null) {
            validationErrors.add("It's necessary to specify the Sector Type");
        }

        // Exits are optional, but if they are here we must validate
        if (exits != null) {
            validateExit(exits);
        }

        if (extraDescriptions != null && !extraDescriptions.isEmpty()) {
            for (ExtraDescription exDesc : extraDescriptions) {
                validateExtraDescription(exDesc);
            }
        }
    }

    private void validateExtraDescription(ExtraDescription exDesc) {
        if (exDesc.keywords == null || exDesc.keywords.isBlank()) {
            validationErrors.add("Extra description must have keywords");
        }
        if (exDesc.description == null || exDesc.description.isBlank()) {
            validationErrors.add("Extra description need a description.");
        }
    }

    private void validateExit(Exit[] exits) {
        // Check if there is duplicated directions
        if (exits.length > Exit.Direction.values().length) {
            validationErrors.add("There are too many exits");
        }
        boolean[] isDirectionUsed = new boolean[Exit.Direction.values().length];

        for (Exit exit : exits) {
            if (isDirectionUsed[exit.direction.number]) {
                validationErrors.add("Direction " + exit.direction.description + " is duplicated");
            }
            isDirectionUsed[exit.direction.number] = true;
        }
    }


    // Room Bitvector
    public enum RoomFlag {
        DARK('a', "Room � dark"), DEATH('b', "Room � Death Trap (DT)"), NOMOB('c', "Mobs cannot enter this room"), INDOORS('d', "Room is indoors"), PEACEFUL('e', "No violence allowed"), SOUNDPROOF('f', "Shout, gossip and other communication will not be heard from this room"), NOTRACK('g', "Track skill will not use this room as part of a path"), NOMAGIC('h', "Every magic attempt will fail in this room"), TUNNEL('i', "Only one person is allowed at time"), PRIVATE('j', "Cannot teleport IN or GOTO if two people here"), GODROOM('k', "Only LVL_GOD and above can enter"), HOUSE('l', "Internal Use - do not use"), HOUSE_CRASH('m', "Internal Use - do not use"), ATRIUM('n', "Internal Use - do not use"), OLC('o', "Internal Use - do not use"), BFS_MARK('p', "Internal Use - do not use"),

        // DEBO CUSTOM
        HEAL('q', ""), FLY('r', ""), ARENA('s', ""), NOPORTAL('t', ""), VORTEX('u', ""), NOSPIRIT('v', ""), NOAIR('w', "Can't breath!"), ECHOZONE('x', "Echo events to whole zone"), DISMOUNT('y', ""), NOMOUNT('z', "Sem Montaria"), IGNORABREVE('A', "Ignora Breve"), PVPONLY('B', "PVP ONLY"), NOSPY('C', "NO-SPY"), NOBITS('0', "NOBITS") /* Actually this one is used when no flags are defiend*/;


        RoomFlag(char flag, String description) {
            this.flag = flag;
            this.description = description;
        }

        public static RoomFlag valueOfFlag(char flag) {
            // First we try to find by letters
            RoomFlag[] values = values();
            for (RoomFlag value : values()) {
                if (value.flag == flag) {
                    return value;
                }
            }

            // if we don't find, we try to convert the letter to a
            // valid flag goes from a-p (lowercase). Char numbers from 97-112
//            int index = flag - (int) 'a'; // (int)'a' should be 97
//            if (index < 0 || index > values().length) {
            throw new IllegalArgumentException("Invalid room flag. No element for the flag " + flag);
            //}

        }

        final char flag;
        final String description;
    }

    public enum SectorType {
        INSIDE(0, "Indoors, small usage of move points"), CITY(1, "The streets of a city"), FIELD(2, "An open field"), FOREST(3, "A dense forest"), HILLS(4, "Low foothills"), MOUNTAIN(5, "Mountains, high usage of move points"), WATER_SWIM(6, "Water, swimmable"), WATER_NOSWIM(7, "Unswimmable water - Requires a boat"), FLYING(8, "Well, you are over the skies"), UNDERWATER(9, "Underwater"), CAVE(10, "Cave");


        SectorType(int id, String description) {
            this.id = id;
            this.description = description;
        }

        final int id;
        final String description;

        public static SectorType fromId(int sectorTypeId) {
            if (sectorTypeId < 0 || sectorTypeId > values().length) {
                throw new IllegalArgumentException("Invalid ID for sector type. Valid ids: 0 to " + values().length);
            }
            return values()[sectorTypeId];
        }

        public String toMudText() {
            return String.valueOf(id);
        }

    }

    // Direction field
    public static class Exit {
        Direction direction;
        String description;
        String keywords;
        DoorFlag doorFlag;
        Integer keyVnum;
        Integer linkedRoom;

        public Exit() {
            // Init default values?
            description = "";
            keywords = "";
            keyVnum = -1;
            doorFlag = DoorFlag.NO_DOOR;
            linkedRoom = -1; // nowhere
        }

        public enum Direction {
            NORTH(0, "North"), EAST(1, "East"), SOUTH(2, "South"), WEST(3, "West"), UP(4, "Up"), DOWN(5, "Down");

            Direction(int number, String description) {
                this.number = number;
                this.description = description;
            }

            final int number;
            final String description;

            public static Direction fromId(int i) {
                if (i < 0 || i > values().length) {
                    throw new IllegalArgumentException("Invalid id for Direction. Valid IDs: 0-" + values().length + " Provided: " + i);

                }
                return values()[i];
            }

        }

        public enum DoorFlag {
            NO_DOOR(0, "An exist that has no door OR a door that need special procedure to be opened"), NORMAL_DOOR(1, "Normal door that can be used with Open/Close/Lock/ commands"), PICKPROOF_DOOR(3, "A door that cannot be by pick lock, if locked you must have the key");

            DoorFlag(int id, String description) {
                this.ID = id;
                this.description = description;
            }

            final int ID;
            final String description;


            public static DoorFlag fromId(int i) {
                if (i < 0 || i > values().length) {
                    throw new IllegalArgumentException("Invalid id for DoorFlag. Valid IDs: 0-" + values().length + " Provided: " + i);

                }
                return values()[i];
            }
        }

        public String toMudString() {
            return "D" + direction.number + NEWLINE + description + NEWLINE + "~" + NEWLINE + keywords + "~" + NEWLINE + doorFlag.ID + WHITESPACE + keyVnum + WHITESPACE + linkedRoom + NEWLINE;
        }
    }

    public static class ExtraDescription {
        String keywords;
        String description;

        public ExtraDescription(String keywords, String description) {
            this.keywords = keywords;
            this.description = description;
        }

        public String getKeywords() {
            return keywords;
        }

        public String getDescription() {
            return description;
        }

        public String toMudString() {
            return "E" + NEWLINE + keywords + "~" + NEWLINE + description + NEWLINE + "~" + NEWLINE;
        }

    }

    private static final String NEWLINE = "\n";
    private static final String WHITESPACE = " ";

    public static String roomToText(Room room) {
        StringBuilder builder = new StringBuilder();
        builder.append("#").append(room.VNUM).append(NEWLINE);
        builder.append(room.name).append("~").append(NEWLINE);
        builder.append(room.description).append(NEWLINE);
        builder.append("~").append(NEWLINE);
        builder.append(room.zoneNumber).append(WHITESPACE).append(roomFlagsToText(room)).append(WHITESPACE).append(room.sectorType.toMudText()).append(NEWLINE);
        for (Exit exit : room.exits) {
            if (exit != null) {
                builder.append(exit.toMudString());
            }
        }
        for (ExtraDescription extraDescription : room.extraDescriptions) {
            builder.append(extraDescription.toMudString());
        }
        builder.append("S").append(NEWLINE);


        return builder.toString();
    }

    /**
     * Parse a string representation of a Room from CircleMud format to java object types.
     *
     * @param text String representation of a CircleMud room.
     * @return a Room object parsed from the given text
     * @throws ParsingException if the provided text does not follow the specs
     */
    static Room roomFromText(String text) throws ParsingException {
        final int MIN_LINES = 4;
        Room room = new Room();

        String[] lines = text.split(NEWLINE);
        // First line must be the ID
        if (lines.length < MIN_LINES) {
            // It must have at least 5 lines
            throw new ParsingException(Room.class, "The provided text has less than " + MIN_LINES + " lines.");

        }
        if (lines[0].charAt(0) != '#') {
            throw new ParsingException(Room.class, 1, "The first line does not start with '#'");
        }

        String vnumStr = lines[0].substring(1);
        try {
            room.setVNUM(Integer.parseInt(vnumStr));
        } catch (NumberFormatException e) {
            throw new ParsingException(Room.class, 1, "Provided vnum (" + vnumStr + ") is not a number.");
        }

        int titleTerminatorPos = lines[1].lastIndexOf("~");
        if (titleTerminatorPos == -1) {
            throw new ParsingException(Room.class, 2, "Missing title terminator '~'. Provided text: " + lines[1]);
        }
        room.setName(lines[1].substring(0, titleTerminatorPos));


        int currentLine = 2;

        try {
            ParsedDescription roomDesc = parseDescriptionString(currentLine, lines);
            room.setDescription(roomDesc.description());
            currentLine = roomDesc.currentLine();
        } catch (ParsingException e) {
            throw new ParsingException(Room.class, currentLine, "Description with bad format. Probably missing a empty line with '~' to delimit the description text.");
        }

        // Check if the description consumed all the text
        if (currentLine >= lines.length) {
            throw new ParsingException(Room.class, currentLine, "Premature end of file. Missing zone information and terminator 'S'.");
        }
        // Now the zone details
        String[] zoneData = lines[currentLine].split(" ");
        if (zoneData.length < 3) {
            String errorMessage = String.format("Wrong format. Expecting: [<zone_number> <room_flags> <sector_type>], but found: [%s]", lines[currentLine]);
            throw new ParsingException(Room.class, currentLine, errorMessage);
        }

        try {
            room.setZoneNumber(Integer.parseInt(zoneData[0]));
        } catch (NumberFormatException e) {
            throw new ParsingException(Room.class, currentLine, "Provided zone number (" + zoneData[0] + ") is not a number. ");
        }
        try {
            room.setRoomFlags(parseRoomFlags(zoneData[1]));
        } catch (ParsingException e) {
            e.setLine(currentLine);
            throw new ParsingException(Room.class, currentLine, "Failed to parse room " + room.getVNUM() + ". Error: " + e.getMessage());
        }
        // Sector type
        try {
            int sectorTypeId = Integer.parseInt(zoneData[2]);
            room.setSectorType(SectorType.fromId(sectorTypeId));
        } catch (NumberFormatException e) {
            throw new ParsingException(Room.class, currentLine, "Failed to parse Sector type. Provided: " + zoneData[2]);

        }
        //advance a line
        currentLine++;
        // Now it can be a 'S' to indicate EOF or any number of Exits followed by any number of extra descriptions
        if (lines[currentLine].charAt(0) == 'S') {
            return room;
        }

        if (lines[currentLine].charAt(0) == 'D') {
            // will parse exit
            ParsedExits exits = parseExits(currentLine, lines);
            room.setExits(exits.exits());
            currentLine = exits.currentLine();
        }
        if (lines[currentLine].charAt(0) == 'E') {
            // Will parse extra description
            ParsedExtraDescriptions descriptions = parseExtraDescription(currentLine, lines);
            room.setExtraDescriptions(descriptions.descriptions());
            currentLine = descriptions.currentLine();
        }

        if (lines[currentLine].charAt(0) == '~') {
            currentLine++;
        }
        if (lines[currentLine].charAt(0) != 'S') {
            throw new ParsingException(Room.class, currentLine, "Invalid terminator char for the given room. Excepting a 'S', found '" + lines[currentLine].charAt(0) + "'");
        }

        return room;
    }

    private record ParsedExtraDescriptions(int currentLine, List<ExtraDescription> descriptions) {
    }

    private static ParsedExtraDescriptions parseExtraDescription(int currentLine, String[] lines) throws ParsingException {
        List<ExtraDescription> exDescriptions = new ArrayList<>();
        while (currentLine < lines.length) {
            if (lines[currentLine].charAt(0) == 'S') {
                // eof
                break;
            }
            if (lines[currentLine].charAt(0) != 'E') {
                throw new ParsingException(Room.class, currentLine, "Invalid start for a Extra Description. Expecting an E but found: " + lines[currentLine]);
            }
            currentLine++;


            int keywordDelimiterIdx = lines[currentLine].lastIndexOf("~");
            if (keywordDelimiterIdx == -1) {
                throw new ParsingException(Room.class, currentLine, "Invalid extra description. Could not find keyword delimiter. Keyword Line: " + lines[currentLine]);
            }
            String keywords = lines[currentLine].substring(0, keywordDelimiterIdx);
            currentLine++;
            ParsedDescription extraDescription = parseDescriptionString(currentLine, lines);
            ExtraDescription desc = new ExtraDescription(keywords, extraDescription.description());
            exDescriptions.add(desc);
            currentLine = extraDescription.currentLine();
        }

        return new ParsedExtraDescriptions(currentLine, exDescriptions);
    }


    private record ParsedExits(int currentLine, Exit[] exits) {
    }

    private static ParsedExits parseExits(int currentLine, String[] lines) throws ParsingException {
        Room.Exit[] exits = new Room.Exit[Exit.Direction.values().length];

        for (; currentLine < lines.length; currentLine++) {
            if (lines[currentLine].charAt(0) == 'E' || lines[currentLine].charAt(0) == 'S') {
                // Found extra description or EOF
                break;
            }
            if (lines[currentLine].charAt(0) != 'D') {
                throw new ParsingException(Room.class, currentLine, "Invalid direction provided. Provided: " + lines[currentLine]);
            }

            int directionIndex = -1;
            try {
                directionIndex = Integer.parseInt(String.valueOf(lines[currentLine].charAt(1)));
                exits[directionIndex] = new Room.Exit();
                exits[directionIndex].direction = Exit.Direction.fromId(directionIndex);

            } catch (NumberFormatException e) {
                throw new ParsingException(Room.class, currentLine, "Provided direction ID is invalid. Expecting a number between 0 and " + exits.length + ". Received: " + lines[currentLine].charAt(1));
            } catch (IllegalArgumentException e) {
                throw new ParsingException(Room.class, currentLine, "Provided direction ID is invalid. Allowed values: 0-" + exits.length + ". Parsed: " + directionIndex);
            }


            currentLine++;
            try {
                ParsedDescription parsedExitDescription = parseDescriptionString(currentLine, lines);
                exits[directionIndex].description = parsedExitDescription.description();

                currentLine = parsedExitDescription.currentLine;
            } catch (ParsingException e) {
                throw new ParsingException(Room.class, currentLine, "Error parsing exit with id: D" + directionIndex + ". Error: " + e.getMessage());
            }
            // Keyword list
            int keywordTerminatorPos = lines[currentLine].lastIndexOf("~");
            if (keywordTerminatorPos == -1) {
                throw new ParsingException(Room.class, currentLine, "Missing keyword terminator '~'. Provided text: " + lines[currentLine]);
            }
            exits[directionIndex].keywords = lines[currentLine].substring(0, keywordTerminatorPos);


            // Exit Info - <door_flag> <key_number> <room_linked>
            currentLine++;
            String[] doorData = lines[currentLine].split(" ");
            // door flag 0,1,2
            if (doorData.length != 3) {
                throw new ParsingException(Room.class, currentLine, "Invalid format for door flags. Expecting" + " [<door_flag> <key_number> <linked room>] found: [" + lines[currentLine] + "]");
            }
            try {
                exits[directionIndex].doorFlag = Exit.DoorFlag.fromId(Integer.parseInt(doorData[0]));
            } catch (IllegalArgumentException e) {
                throw new ParsingException(Room.class, currentLine, "Invalid room flag number. " + e.getMessage());
            }
            try {
                exits[directionIndex].keyVnum = Integer.parseInt(doorData[1]);
            } catch (NumberFormatException e) {
                throw new ParsingException(Room.class, currentLine, "Invalid key number. It must be -1 or a valid key VNUM. Received: " + doorData[1]);
            }

            try {
                exits[directionIndex].linkedRoom = Integer.parseInt(doorData[2]);
            } catch (NumberFormatException e) {
                throw new ParsingException(Room.class, currentLine, "Invalid room number. It must be -1 or a valid key room. Received: " + doorData[2]);
            }

        }

        return new ParsedExits(currentLine, exits);
    }


    private record ParsedDescription(int currentLine, String description) {
    }

    private static ParsedDescription parseDescriptionString(int currentLine, String[] lines) throws ParsingException {

        if (lines == null || lines.length < 1) {
            throw new ParsingException(Room.class, currentLine, "Provided lines are too short for a room.");
        }

        StringBuilder builder = new StringBuilder();
        boolean wellFormattedDescription = false;
        for (; currentLine < lines.length; currentLine++) {
            if (lines[currentLine] == null) {
                // parsing exception
                throw new ParsingException(Room.class, currentLine, "Null line provided. We MUST have no null lines.");
            }
            if (!lines[currentLine].isBlank()) {
                if (lines[currentLine].charAt(0) == '~') {
                    wellFormattedDescription = true;
                    currentLine++;
                    break;
                }

                if(lines[currentLine].lastIndexOf('~') >= lines[currentLine].length() - 1) {

                    builder.append(lines[currentLine], 0, lines[currentLine].lastIndexOf('~'));
                    builder.append('\n');
                    wellFormattedDescription = true;
                    currentLine++;
                    break;
                }
            }

            builder.append(lines[currentLine]).append(NEWLINE);
        }
        if (!wellFormattedDescription) {
            throw new ParsingException(Room.class, currentLine, "Could not find the description delimiter");
        }


        String returnString;
        if (builder.isEmpty()) {
            returnString = builder.toString();
        } else if (builder.charAt(builder.length() - 1) == '\n') {
            returnString = builder.substring(0, builder.length() - 1);
        } else {
            // This should give an error
            throw new ParsingException(Room.class, currentLine, "Expecting a empty line terminator but found no '\\n'");
        }


        return new ParsedDescription(currentLine, returnString);
    }


    /**
     * Receives the flag letters 'abcde' and convert to a list of objects
     *
     * @param flags the text representation for flags.
     * @return a list of RoomFlags based on the string
     * @throws ParsingException if the provided string does not follow the spec
     */
    private static List<RoomFlag> parseRoomFlags(String flags) throws ParsingException {
        List<RoomFlag> roomFlag = new ArrayList<>();
        for (int i = 0; i < flags.length(); i++) {
            try {
                roomFlag.add(RoomFlag.valueOfFlag(flags.charAt(i)));
            } catch (IllegalArgumentException e) {
                throw new ParsingException(Room.class, "Invalid room flag: " + flags.charAt(i) + ". Valid flags are: a-z (lowercase) or A (CAPITAL)");
            }
        }
        return roomFlag;
    }

    private static String roomFlagsToText(Room room) {
        StringBuilder returnText = new StringBuilder();

        List<RoomFlag> sortedFlags = room.getRoomFlags().stream().sorted(Comparator.comparing(flag -> flag.flag)).toList();
        for (RoomFlag flag : sortedFlags) {
            returnText.append(flag.flag);
        }
        return returnText.toString();
    }


}
