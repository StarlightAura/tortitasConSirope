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

Script de Postman para la verificación de la funcionalidad correcta del programa.
El script se encuentra en la carpeta "postman-tests" y esta listo para poder importarse en postman y ejecutarse.

| Endpoint                                                       | Descripción                                 |
|----------------------------------------------------------------|---------------------------------------------|
|http://localhost:8787/api/auth/signin                          | Login exitoso y obtención del token         |
|http://localhost:8787/api/recommendations?product=dark%20souls | Fallo sin token                             
|http://localhost:8787/api/products    | Inserción de producto con vectorización, usando token válido |
|http://localhost:8787/api/recommendations?product=dark%20souls  | Recomendaciones con token válido            |

### Spring Boot

Compartimentalización de procesos dentro del programa.

### Algoritmo de similitud coseno.

Algoritmo de búsqueda entre documentos vectorializados.

### En memoria del Bot de Telegram
Teníamos nuestro bot llamado @SiropeGamersBot en Telegram y usando los siguientes comandos el usuario se podía registrar, iniciar sesión y buscar la recomendación de juegos:

            
| Comando   | Descripción  |
|---|---|
| /start  |Muestra los comandos disponibles   |
| /registro  | Crear una cuenta nueva  |
| /login   | Iniciar sesión y obtener token  |
|/recomendar <juego>   | Pedir recomendaciones (requiere login)  |


![captura bot 1.png](src/main/resources/static/img/captura%20bot%201.png)
![captura bot.png](src/main/resources/static/img/captura%20bot.png)

#### Momentos humildes

![captura bot errores.png](src/main/resources/static/img/captura%20bot%20errores.png)
---

# TODO: Instrucciones
