package org.tortitas.tfg.mysql.login.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Pattern(
        regexp ="^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$",
        message = "La contraseña debe tener mayúsculas, minúsculas, número y símbolo"
)

public @interface ValidPassword {

    String message() default "contraseña inválida";

    Class<?>[] groups() default {};

    Class<? extends Payload> [] payload () default {};



}
