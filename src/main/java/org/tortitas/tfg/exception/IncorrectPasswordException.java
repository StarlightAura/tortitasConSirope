package org.tortitas.tfg.exception;

public class IncorrectPasswordException extends IllegalArgumentException {
    public IncorrectPasswordException(String message) {
        super(message);
    }
}
