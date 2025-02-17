/**
 * Пакет содержит конфигурацию Telegram-бота для сервиса рекомендаций.
 * <p>
 * Этот пакет предназначен для настройки параметров работы Telegram-бота,
 * таких как токен бота и другие необходимые свойства.
 */
package bank.recommendationservice.fintech.telegrambot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Конфигурация Telegram-бота.
 * <p>
 * Класс отвечает за загрузку токена бота из файла конфигурации
 * {@code tg_token.properties} и предоставление его для использования в приложении.
 * Если файл конфигурации не найден или произошла ошибка чтения, логируется соответствующее сообщение.
 */
@Configuration
@PropertySource("classpath:tg_token.properties")
public class TelegramBotConfiguration {

    /**
     * Токен Telegram-бота.
     */
    @Value("${bot.token}")
    private String botToken;

    private static final Logger logger = LoggerFactory.getLogger(TelegramBotConfiguration.class);

    /**
     * Конструктор для инициализации конфигурации Telegram-бота.
     * <p>
     * Загрузка токена бота из файла конфигурации {@code tg_token.properties}.
     * В случае ошибки чтения файла или его отсутствия, логируется ошибка.
     * Возвращает токен Telegram-бота.
     * @return Строка, содержащая токен бота.
     */
    @Bean
    public TelegramBot telegramBot() {
        if (botToken == null || botToken.isEmpty()) {
            logger.error("Токен бота не установлен. Проверьте файл конфигурации tg_token.properties");
            throw new IllegalArgumentException("Токен бота не установлен.");
        }
        return new TelegramBot(botToken);
    }
}