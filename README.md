# Recommendation Service for Fintech

Сервис рекомендаций предоставляет персонализированные предложения для клиентов на основе их транзакций и
динамических правил.

[![Build and Test](https://github.com/ibogomolova/BankStar-service/actions/workflows/main.yml/badge.svg?branch=master)](https://github.com/ibogomolova/BankStar-service/actions/workflows/main.yml)

1. Build Status (Статус сборки)


## Содержание

- [Технологии](#технологии)
- [Начало работы](#начало-работы)
- [Запуск приложения](#запуск-приложения)
- [Использование API](#использование-api)
- [Тестирование](#тестирование)
- [Deploy и CI/CD](#deploy-и-cicd)
- [Contributing](#contributing)
- [Команда проекта](#команда-проекта)

## Технологии

- Java (https://www.oracle.com/java/)
- Spring Boot (https://spring.io/projects/spring-boot)
- Spring Data JPA (https://spring.io/projects/spring-data-jpa)
- PostgreSQL (https://www.postgresql.org/)
- Swagger (https://swagger.io/)
- Lombok (https://projectlombok.org/)
- Maven (https://maven.apache.org/)

## Начало работы

▌Требования

Для установки и запуска проекта, необходимы:

- Java 17 (https://www.oracle.com/java/)
- Maven (https://maven.apache.org/)
- Docker (https://www.docker.com/) (для локального развертывания PostgreSQL)

▌Установка зависимостей

Для установки зависимостей, выполните команду:

```
sh
mvn clean install
```

▌Запуск приложения

1. Запуск PostgreSQL с помощью Docker:

```
sh
docker run -d --name postgres -p 5432:5432 -e POSTGRES_USER=youruser -e POSTGRES_PASSWORD=yourpassword -e POSTGRES_DB=yourdb postgres:15
```

2. Настройте application.properties:

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

3. Запуск приложения:

```
sh
mvn spring-boot:run
```

Приложение будет доступно по адресу: http://localhost:8080

## Использование API

▌Документация API

Для просмотра документации API, используйте Swagger UI, перейдя по адресу: http://localhost:8080/swagger-ui/index.html.

## Примеры запросов

▍Получить рекомендации для пользователя

```
http
GET /recommendation/{user_id}
```

▍Создать динамическое правило

```
http
POST /rule
Content-Type: application/json

{
"productName": "Кредитная карта",
  "productId": "550e8400-e29b-41d4-a716-446655440000",
  "productText": "Предлагаем вам кредитную карту с выгодными условиями",
  "rule": [
    {
        "query": "USER_OF",
        "arguments": [
            "CREDIT"
        ],
        "negate": true
    }
]
}
```

▍Удалить динамическое правило

```
http
DELETE /rule/{id}
```

▍Получить все динамические правила

```
http
GET /rule
```

## Тестирование

В проекте используются JUnit и Mockito для тестирования.

Для запуска тестов выполните команду:

```
sh
mvn test
```

## Deploy и CI/CD

Для автоматической сборки и деплоя используется Github Action.
При изменения в main ветке запускается workflow, который выполняет сборку приложения, создает Docker образ и выполняет
deployment в Kubernetes.

## Contributing

Мы приветствуем вклад в проект!
Для этого:

1. Создайте форк репозитория.
2. Сделайте ваши изменения в новой ветке.
3. Оформите Pull Request в ветку dev.

Пожалуйста, соблюдайте code style и пишите информативные commit message.

## FAQ

▌Какие типы исключений обрабатываются?
Обрабатываются RulesNotFoundException, RecommendationNotFoundException, NoTransactionsFoundException и
IllegalArgumentException.

## Команда проекта

- [Irina Bogomolova](https://github.com/ibogomolova) — Main developer
- [Alina Cheremiskina](https://github.com/linskay) — PM
- [Vitaly Dineka](https://github.com/Rafnes) — Developer
- [Ivan Pesterev](https://github.com/gface34rus) — QA


## Источники

- Spring Boot Documentation (https://spring.io/projects/spring-boot)
- Swagger Documentation (https://swagger.io/docs/)

