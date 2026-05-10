# Sirope Games
Motor de recomendación de videojuegos basado en IA.

## Características

### Bases de datos separadas

Por motivos de seguridad, la gestión de usuarios y el almacenamiento de juegos se hace de forma separadas.
- MongoDB Atlas: Almacenamiento de juegos en base de datos documental para su fácil recuperación y gestión. Al existir en la nube se puede separar de los contenedores Docker facilitando así el despliegue de instancias locales.
- PostgreSQL: Base de datos relacional para el almacenamiento de usuarios en local, encriptando las contraseñas en el proceso.

### Docker

El uso de docker hace el despliegue de la aplicación sencillo y replicable.

### Ollama

Uso de Ollama para vectorización de documentos de MongoDB, permitiendo así la creación de algoritmos de similitud entre la búsqueda del usuario y los juegos de la base de datos.

### Thymeleaf

Uso de thymeleaf para generación automática de páginas web basadas en plantillas.

### JWT

Uso de tokens seguros JWT para la autenticación de usuarios. Biblioteca Jose4J.

### Postman

Scripts de Postman para la verificación de la funcionalidad correcta del programa.

### Spring Boot

Compartimentalización de procesos dentro del programa.

### Algoritmo de similitud coseno.

Algoritmo de búsqueda entre documentos vectorializados.