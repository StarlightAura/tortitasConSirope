package org.tortitas.tfg.exception;

import org.jose4j.lang.JoseException;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
public class AuthException {

    public void handle(Model model, Exception ex){
        switch (ex) {
            case UserNotFoundException userNotFoundException -> {
                model.addAttribute("error", "No autorizado. " + ex.getMessage());
            }
            case IncorrectPasswordException incorrectPasswordException -> {
                model.addAttribute("error", "Dato incorrecto. " + ex.getMessage());
            }
            case IllegalArgumentException illegalArgumentException -> {
                model.addAttribute("error", "Atributo incorrecto. " + ex.getMessage());
            }
            case IllegalStateException illegalStateException -> {
                model.addAttribute("error", "Conflicto. "+ex.getMessage());
            }
            case JoseException joseException -> {
                model.addAttribute("error", "Error de seguridad. " + ex.getMessage());
            }
            default -> {
                model.addAttribute("error", "Error interno. " +ex.getMessage());
            }
        }
    }



}
