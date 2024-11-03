package org.debo.lib.world;

/**
 * Generic error when we fail to parse a Lib Object
 */
public class ParsingException  extends Exception{

    private final Class<?> objectType;
    private final String message;
    int line;

    public ParsingException(Class<?> objectType, String message) {
        this(objectType, -1, message);
    }
    public ParsingException(Class<?> objectType, int line, String message) {
        super(message);
        this.objectType = objectType;
        this.message = message;
        this.line = line;
    }

    @Override
    public String getMessage() {
        if(line >= 0) {
            return String.format("Failed to parse object %s. Line: %d - Error: [ %s ]", objectType.getName(), line, message);
        }
        return String.format("Failed to parse object %s. Error: [ %s ]", objectType.getName(), message);
    }

    public void setLine(int currentLine) {
        this.line = currentLine;
    }
}
