package bank.recommendationservice.fintech.telegrambot.listener;

import bank.recommendationservice.fintech.dto.RecommendationDTO;
import bank.recommendationservice.fintech.repository.RecommendationsRepository;
import bank.recommendationservice.fintech.service.RecommendationService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TelegramBotUpdatesListener implements UpdatesListener {
    private static final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final TelegramBot telegramBot;
    private final RecommendationService recommendationService;
    private final RecommendationsRepository recommendationsRepository;

    public TelegramBotUpdatesListener(TelegramBot telegramBot,
                                      RecommendationService recommendationService,
                                      RecommendationsRepository recommendationsRepository) {
        this.telegramBot = telegramBot;
        this.recommendationService = recommendationService;
        this.recommendationsRepository = recommendationsRepository;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }


    /**
     * Обрабатывает список обновлений от Telegram. Если обновление содержит сообщение,
     * бот проверяет, является ли сообщение командой. Если это так, бот обрабатывает команду.
     * Если сообщение не является командой, бот игнорирует его.
     *
     * @param updates список обновлений для обработки
     * @return {@link UpdatesListener#CONFIRMED_UPDATES_ALL} для подтверждения обработки всех обновлений
     */
    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);

            if (update.message() != null) {
                Message message = update.message();
                String text = message.text();
                long chatId = message.chat().id();

                if (text == null) {
                    SendMessage sendMessage = new SendMessage(chatId, "Ошибка: текст команды не может быть пустым.");
                    telegramBot.execute(sendMessage);
                    return;
                }

                if ("/start".equals(text)) {

                    String welcomeMessage = "Привет! Я Star Bank Assistant. " +
                            "Используйте команду /recommend <имя_пользователя> для получения рекомендаций.";

                    SendMessage sendMessage = new SendMessage(chatId, welcomeMessage);
                    telegramBot.execute(sendMessage);
                } else if (text.startsWith("/recommend")) {
                    String[] commandParts = text.split(" ");


                    if (commandParts.length <= 1) {
                        SendMessage sendMessage = new SendMessage(chatId, "Пожалуйста, " +
                                "укажите имя пользователя, через пробел, после команды /recommend.");

                        telegramBot.execute(sendMessage);
                        return;
                    }
                    handleRecommendationRequest(chatId, commandParts[1]);
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }


        /**
         * Обрабатывает запрос на получение рекомендаций.
         * <p>
         * Метод проверяет, существует ли пользователь с указанным именем.
         * Если пользователь не существует, бот отправляет сообщение об ошибке.
         * Если пользователь существует, бот отправляет список рекомендаций для него.
         *
         * @param chatId  ID чата, в котором был отправлен запрос
         * @param userName имя пользователя, для которого нужно получить рекомендации
         */
    private void handleRecommendationRequest(long chatId, String userName) {
        if (userName == null || userName.isEmpty()) {
            throw new IllegalArgumentException("Имя пользователя не должно быть пустым");
        }
        boolean check = recommendationsRepository.validateUserNameInDataBase(userName);
        if (!check) {
            String answer = "Пользователь с именем " + userName + " не найден.";
            SendMessage sendMessage = new SendMessage(chatId, answer);
            telegramBot.execute(sendMessage);
        }
        List<RecommendationDTO> response = recommendationService.getRecommendations(userName);
        String fullUserName = recommendationsRepository.getFullNameByUsername(userName);
        String result = "Рекомендации для " + fullUserName + ":\n";
        for (RecommendationDTO recommendation : response) {
            result += recommendation.toString() + "\n";
        }
        SendMessage sendMessage = new SendMessage(chatId, result);
        telegramBot.execute(sendMessage);
    }
}