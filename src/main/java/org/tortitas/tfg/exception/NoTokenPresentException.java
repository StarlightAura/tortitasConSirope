package org.tortitas.tfg.exception;

public class NoTokenPresent extends IllegalArgumentException {
    public NoTokenPresent(String message) {
        super(message);
    }
}
