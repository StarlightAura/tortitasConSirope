package org.tortitas.tfg.mysql.login.Exception;

public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException(String message){
       super(message);
    }

}
