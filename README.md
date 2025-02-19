# StarBank Recommendation Service

Сервис рекомендаций предоставляет персонализированные предложения для клиентов на основе их транзакций по статическим
(вшитым в исходный код) и динамическим правилам.


1. Build Status (Статус сборки)

• Сервисы CI/CD:
• GitHub Actions: Если вы используете GitHub Actions, URL-адрес статуса сборки можно получить из настроек вашего
репозитория:

1. Перейдите в ваш репозиторий на GitHub.
2. Нажмите на вкладку "Actions".
3. Выберите ваш workflow.
4. В разделе "Badge" скопируйте markdown-код)

## Содержание

- [Технологии](#технологии)
- [Начало работы](#начало-работы)
- [Запуск приложения](#запуск-приложения)
- [Использование API](#использование-api)
- [Тестирование](#тестирование)
- [Deploy и CI/CD](#deploy-и-cicd)
- [Contributing](#contributing)
- [To do](#to-do)
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
docker run -d --name postgres -p 5432:5432 -e POSTGRES_USER=bankStar -e POSTGRES_PASSWORD=2222 -e POSTGRES_DB=DynamicRules postgres:15
```

2. Настройте application.properties:

```
properties
spring.application.name=fintech
build.version=1.0.0
application.fintech_service-db.url=jdbc:h2:file:./src/main/resources/transaction
#spring.datasource.driver-class-name=org.h2.Driver
#spring.h2.console.enabled=true
#spring.h2.console.path=/h2-console

spring.datasource.url=jdbc:postgresql://localhost:5432/DynamicRules
spring.datasource.username=bankStar
spring.datasource.password=2222
spring.datasource.driver-class-name=org.postgresql.Driver
spring.liquibase.default-schema=public

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

spring.liquibase.change-log=classpath:db/changelog-master.yml
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


## FAQ

▌Какие типы исключений обрабатываются?
Обрабатываются RulesNotFoundException, RecommendationNotFoundException, NoTransactionsFoundException и
IllegalArgumentException.


## Команда проекта

- [Irina bogomolova](https://github.com/samka-bogomola-02) — TeamLead
- [Alina Cheremiskina](https://github.com/linskay) — PM
- [Vitaly Dineka](https://github.com/Rafnes) — Developer
- [Ivan Pesterev](https://github.com/gface34rus) — QA


## Источники

- Spring Boot Documentation (https://spring.io/projects/spring-boot)
- Swagger Documentation (https://swagger.io/docs/)

```