package org.tortitas.tfg.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.generics.TelegramBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.tortitas.tfg.telegram.SiropeGameBot;

@Configuration
public class ConfigBot {
    /**

     Registra y arranca el bot de Telegram usando una sesion por defecto.
     @param bot La instancia del servicio TelegramBot.
     @return El API de TelegramBots listo para recibir mensajes.
     @throws Exception por si hay algun error en el registro.*/
    @Bean
    public TelegramBotsApi telegramBotsApi(SiropeGameBot bot) throws Exception {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(bot);

        return api;
    }
}
