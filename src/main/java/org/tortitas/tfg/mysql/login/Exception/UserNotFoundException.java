package org.tfg.api.mysql.login.Exception;

public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException(String message){
       super(message);
    }

}
