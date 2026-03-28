package org.tortitas.tfg.telegram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.tortitas.tfg.models.JWTToken;
import org.tortitas.tfg.models.User;
import org.tortitas.tfg.repositories.UserRepo;
import org.tortitas.tfg.services.GameService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@Component
public class SiropeGameBot extends TelegramLongPollingBot {

    @Autowired private GameService gameService;

    // Token JWT de cada usuario guardado por chatId
    private final Map<Long, String> tokensUsuarios = new HashMap<>();
    // Estado del flujo de registro/login
    private final Map<Long, String> estadoUsuario = new HashMap<>();
    private final Map<Long, String> datosTemporales = new HashMap<>();

    private final String botName;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String API_BASE = "http://localhost:8087";

    public SiropeGameBot(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.username}") String botName) {
        super(botToken);
        this.botName = botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        String texto = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        String respuesta;

        if (estadoUsuario.containsKey(chatId)) {
            respuesta = manejarFlujo(chatId, texto);

        } else if (texto.equals("/start")) {
            respuesta = "¡Bienvenido a SiropeGamers! 🎮\n\n" +
                    "/registro - Crear cuenta\n" +
                    "/login - Iniciar sesión\n" +
                    "/recomendar <juego> - Buscar recomendaciones";

        } else if (texto.equals("/registro")) {
            estadoUsuario.put(chatId, "registro_usuario");
            respuesta = "¿Qué nombre de usuario quieres?";

        } else if (texto.equals("/login")) {
            estadoUsuario.put(chatId, "login_usuario");
            respuesta = "Escribe tu nombre de usuario:";

        } else if (texto.startsWith("/recomendar ")) {
            if (!tokensUsuarios.containsKey(chatId)) {
                respuesta = "⚠️ Necesitas hacer /login primero.";
            } else {
                respuesta = pedirRecomendaciones(chatId, texto.substring(12));
            }

        } else {
            respuesta = "Comando no reconocido. Usa /start para ver los comandos.";
        }

        enviarMensaje(chatId, respuesta);
    }

    private String manejarFlujo(long chatId, String texto) {
        String estado = estadoUsuario.get(chatId);

        switch (estado) {
            case "registro_usuario":
                datosTemporales.put(chatId, texto);
                estadoUsuario.put(chatId, "registro_password");
                return "Ahora escribe tu contraseña:";

            case "registro_password":
                return registrarUsuario(chatId, texto);

            case "login_usuario":
                datosTemporales.put(chatId, texto);
                estadoUsuario.put(chatId, "login_password");
                return "Escribe tu contraseña:";

            case "login_password":
                return loginUsuario(chatId, texto);

            default:
                estadoUsuario.remove(chatId);
                return "Algo fue mal. Usa /start para empezar.";
        }
    }

    private String registrarUsuario(long chatId, String password) {
        String nombreUser = datosTemporales.get(chatId);
        estadoUsuario.remove(chatId);
        datosTemporales.remove(chatId);

        try {
            Map<String, String> body = Map.of(
                    "nombreUser", nombreUser,
                    "password", password
            );
            restTemplate.postForObject(API_BASE + "/api/auth/signup", body, String.class);
            return "✅ ¡Cuenta creada! Ahora usa /login para entrar.";
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 400) {
                return "❌ Ese nombre de usuario ya existe. Intenta con otro.";
            }
            return "❌ Error al registrar. Inténtalo de nuevo.";
        } catch (Exception e) {
            return "❌ Error al conectar con el servidor.";
        }
    }

    private String loginUsuario(long chatId, String password) {
        String nombreUser = datosTemporales.get(chatId);
        estadoUsuario.remove(chatId);
        datosTemporales.remove(chatId);

        try {
            Map<String, String> body = Map.of(
                    "nombreUser", nombreUser,
                    "password", password
            );
            // Llama al endpoint de login y obtiene el JWT
            Map respuesta = restTemplate.postForObject(
                    API_BASE + "/api/auth/signin", body, Map.class);

            String token = (String) respuesta.get("token");
            tokensUsuarios.put(chatId, token); // guarda el JWT por chatId
            return "✅ ¡Login correcto! Ya puedes usar /recomendar <juego>.";

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 401) {
                return "❌ Usuario o contraseña incorrectos.";
            }
            return "❌ Error al hacer login.";
        } catch (Exception e) {
            return "❌ Error al conectar con el servidor.";
        }
    }

    private String pedirRecomendaciones(long chatId, String query) {
        try {
            String token = tokensUsuarios.get(chatId);

            // Añade el JWT en la cabecera Authorization
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<List> response = restTemplate.exchange(
                    API_BASE + "/api/recommendations?product=" + query,
                    HttpMethod.GET,
                    request,
                    List.class
            );

            List<String> recomendaciones = response.getBody();
            if (recomendaciones == null || recomendaciones.isEmpty()) {
                return "No encontré juegos similares 😕";
            }
            return "🕹️ Recomendaciones para \"" + query + "\":\n\n"
                    + String.join("\n", recomendaciones);

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 401) {
                tokensUsuarios.remove(chatId); // token expirado, limpiamos
                return "❌ Tu sesión ha expirado. Usa /login para volver a entrar.";
            }
            return "❌ Error al obtener recomendaciones.";
        } catch (Exception e) {
            return "❌ Error al conectar con el servidor.";
        }
    }

    private void enviarMensaje(long chatId, String texto) {
        SendMessage msg = new SendMessage();
        msg.setChatId(String.valueOf(chatId));
        msg.setText(texto);
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() { return botName; }
}