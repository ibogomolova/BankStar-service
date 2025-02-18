# Recommendation Service Wiki

## Введение

Этот сервис предназначен для предоставления персонализированных рекомендаций клиентам SkyPro School. Он использует динамические правила, основанные на данных о транзакциях, для генерации соответствующих предложений.

## Архитектура

Сервис построен на основе:

•   **Spring Boot:** Для построения RESTful API.

•   **Spring Data JPA:** Для работы с базой данных (PostgreSQL).

•   **Lombok:** Для уменьшения шаблонного кода.

•   **Swagger:** Для автоматической генерации документации API.

## Сущности

•   **DynamicRule:** Представляет собой правило, используемое для генерации рекомендаций. Содержит имя продукта, текст продукта и набор запросов.

•   **DynamicRuleQuery:** Содержит конкретный запрос для правила (например, "транзакция > 1000") и флаг для отрицания.

•   **Recommendation:** DTO, представляющий рекомендацию для пользователя (ID, название, текст).

•   **RecommendationResponse:** Объект ответа, содержащий список рекомендаций для конкретного пользователя.

## Эндпоинты API

•   **Health Check:**
•   `/health`: Возвращает статус приложения и его версию.

•   **Recommendation:**
•   `GET /recommendation/{user_id}`: Получает список рекомендаций для пользователя с заданным ID.

•   **Dynamic Rule:**
•   `POST /rule`: Создает новое динамическое правило.
•   `DELETE /rule/{id}`: Удаляет динамическое правило по его ID.
•   `GET /rule`: Получает список всех динамических правил.

## Обработка ошибок

Сервис обрабатывает следующие исключения:

•   `RulesNotFoundException`: Когда не найдено ни одного подходящего правила.

•   `RecommendationNotFoundException`: Когда не найдено ни одной рекомендации для пользователя.

•   `NoTransactionsFoundException`: Когда у пользователя нет транзакций.

•   `NullArgumentException`: Когда передан null аргумент.

•   `UnknownQueryTypeException`: Когда тип запроса неизвестен.

•  `UnknownTransactionType`: Когда тип транзакции неизвестен.

•  `UnknownComparisonTypeException`: Когда тип сравнения неизвестен.

•   `RepositoryNotInitializedException`: Когда произошла ошибка при инициализации репозитория.

•   `Exception`: Для всех непредвиденных ошибок.

Подробности об обработке ошибок можно посмотреть в классе `GlobalExceptionHandler`.

## Конфигурация

Конфигурация приложения (например, подключение к БД) производится через `application.properties`.
Основные свойства:
```
properties
spring.datasource.url=jdbc:postgresql://localhost:5432/yourdb
spring.datasource.username=youruser
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.application.name=fintech-recommendation-service
build.version=1.0
```

## Развертывание

Сервис можно развернуть с помощью Docker или в любом другом окружении Java. Рекомендуется использовать Docker для простоты.
Деплой осуществляется с помощью Github Action в Kubernetes.

## Контакты

По всем вопросам обращайтесь: [Ира Богомолова](воткнуть ссыль)

