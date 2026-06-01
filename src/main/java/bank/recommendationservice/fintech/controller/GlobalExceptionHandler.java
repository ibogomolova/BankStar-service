package bank.recommendationservice.fintech.controller;

import bank.recommendationservice.fintech.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RulesNotFoundException.class)
    public ResponseEntity<String> handleRulesNotFoundException(RulesNotFoundException ex) {
        String message = String.format("%s (ID: %d)", ex.getMessage(), ex.getRuleId());
        logger.error(message);
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BaseNotFoundException.class)
    public ResponseEntity<String> handleNotFoundExceptions(BaseNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BaseBadRequestException.class)
    public ResponseEntity<String> handleBadRequestExceptions(BaseBadRequestException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RepositoryNotInitializedException.class)
    public ResponseEntity<String> handleInternalServerException(RepositoryNotInitializedException ex) {
        logger.error("Repository not initialized error: ", ex);
        return new ResponseEntity<>("Repository not initialized. Please check the server logs for more details.",
                HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        logger.error("An unexpected error occurred: ", ex);
        return new ResponseEntity<>("An unexpected error occurred: " + ex.getMessage(),
                HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(IllegalQueryArgumentsException.class)
    public ResponseEntity<String> handleIllegalQueryArgumentException(Exception ex) {
        logger.error("Переданы некорректные аргументы запроса query: " + ex.getMessage());
        return new ResponseEntity<>("Переданы некорректные аргументы запроса query" + ex.getMessage(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnknownComparisonTypeException.class)
    public ResponseEntity<String> handleComparisonTypeException(Exception ex) {
        logger.error("Передан некорректный тип сравнения: " + ex.getMessage());
        return new ResponseEntity<>("Передан некорректный тип сравнения " + ex.getMessage(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnknownProductTypeException.class)
    public ResponseEntity<String> handleProductTypeException(Exception ex) {
        logger.error("Передан некорректный тип продукта: " + ex.getMessage());
        return new ResponseEntity<>("Передан некорректный тип продукта " + ex.getMessage(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnknownQueryTypeException.class)
    public ResponseEntity<String> handleQueryTypeException(Exception ex) {
        logger.error("Передан некорректный тип запроса: " + ex.getMessage());
        return new ResponseEntity<>("Передан некорректный тип запроса " + ex.getMessage(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnknownTransactionTypeException.class)
    public ResponseEntity<String> handleTransactionTypeException(Exception ex) {
        logger.error("Передан некорректный тип транзакции: " + ex.getMessage());
        return new ResponseEntity<>("Передан некорректный тип транзакции " + ex.getMessage(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(Exception ex) {
        logger.error("Пользователь не найден: " + ex.getMessage());
        return new ResponseEntity<>("Пользователь не найден " + ex.getMessage(),
                HttpStatus.NOT_FOUND);
    }
}