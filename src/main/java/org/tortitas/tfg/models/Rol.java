package org.tortitas.tfg.models;

/**
 *Enum que define los roles de seguridad de la aplicacion.
 *<p>
 *Tipa de forma estricta los privilegios de los usuarios para evitar errores de escritura
 *en texto plano, sirviendo de base a Spring Security para restringir el acceso a las rutas y endpoints.
 *</p>
 */
public enum Rol {
    /**Rol de Administrador, con permisos totales para la gestion del catalogo de videojuegos.*/
    ADMIN,
    /**Rol de Usuario normal, con acceso a las consultas y recomendaciones de la IA.*/
    USER
}
