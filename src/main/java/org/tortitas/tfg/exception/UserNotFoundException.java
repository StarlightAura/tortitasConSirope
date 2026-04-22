package org.tortitas.tfg.exception;

public class UserNotFoundException extends IllegalArgumentException{

    public UserNotFoundException(String message){
        super(message);
    }

}
