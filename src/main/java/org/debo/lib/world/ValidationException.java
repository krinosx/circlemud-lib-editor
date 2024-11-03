package org.debo.lib.world;


import java.util.List;

/**
 * Thrown when an object fails to validate
 */
public class ValidationException extends Exception {

    List<String> validationMessages;
    Class<?> klass;

    public ValidationException(Class<?> klass, String message, List<String> validationMessages) {
        this(klass, message, validationMessages, null);
    }

    public ValidationException(Class<?> klass, String message, List<String> validationMessages, Throwable t) {
        super(message, t);
        this.validationMessages = validationMessages;
        this.klass = klass;
    }


    @Override
    public String getMessage() {
        return String.format("Failed to validate: %s. Error: %s. Check validation messages for details.", klass == null ? "unknown" : klass.getName(), super.getMessage());
    }

    public List<String> getValidationMessages() {
        return validationMessages;
    }
}
