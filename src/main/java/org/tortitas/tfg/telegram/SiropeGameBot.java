package org.tortitas.tfg.telegram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.tortitas.tfg.repositories.UserRepo;
import org.tortitas.tfg.services.GameService;
import java.util.List;


@Component
public class SiropeGameBot extends TelegramLongPollingBot {

    @Autowired
    private GameService gameService;

    private final String botName;

    // El token se pasa al constructor del padre
    public SiropeGameBot(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.username}") String botName
    ) {
        super(botToken);
        this.botName = botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String texto = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            String respuesta;

            if (texto.equals("/start")) {
                respuesta = "¡Bienvenido a SiropeGamers! 🎮\nUsa /recomendar <juego> para buscar recomendaciones.";
            } else if (texto.startsWith("/recomendar ")) {
                String query = texto.substring(12);
                List<String> recomendaciones = gameService.recomendar(query);
                if (recomendaciones.isEmpty()) {
                    respuesta = "No encontré juegos similares 😕";
                } else {
                    respuesta = "🕹️ Recomendaciones para \"" + query + "\":\n\n"
                            + String.join("\n", recomendaciones);
                }
            } else {
                respuesta = "No entiendo ese comando. Prueba con /recomendar <nombre del juego>";
            }

            SendMessage mensj = new SendMessage();
            mensj.setChatId(String.valueOf(chatId));
            mensj.setText(respuesta);

            try {
                execute(mensj);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }
}