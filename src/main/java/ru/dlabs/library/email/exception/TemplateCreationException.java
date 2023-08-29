package ru.dlabs.library.email.exception;

/**
 * It's the exception to creating a Velocity Template object
 */
public class TemplateCreationException extends Exception{

    public TemplateCreationException(String message) {
        super(message);
    }
}
