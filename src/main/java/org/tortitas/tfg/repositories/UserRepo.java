package org.tortitas.tfg.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tortitas.tfg.models.User;

import java.util.Optional;
/**
 *Repositorio encargado de la gestion de usuarios en PostgreSQL.
 *<p>
 *Al heredar de {@link JpaRepository}, obtenemos de forma automatica los metdos
 *para realizar las operaciones CRUD basicas sobre la entidad {@link User}, utilizando
 *un String como clave primaria.
 *</p>
 */
@Repository
public interface UserRepo extends JpaRepository<User, String> {
    /**
     *Busca un usuario por su nombre de usuario en la base de datos.
     *<p>
     *Spring Data genera la consulta SQL automaticamente interpretando el nombre del metdo.
     *Devuelve un {@link Optional} para gestionar de forma segura la ausencia del registro.
     *</p>
     *@param nombreUser Nombre del usuario a localizar.
     *@return Un contenedor Optional con el usuario si existe, o vacio en caso contrario.
     */
    Optional<User> findByNombreUser(String nombreUser);
}
