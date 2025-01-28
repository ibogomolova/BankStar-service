package bank.recommendationservice.fintech.controller;

import bank.recommendationservice.fintech.exception.BaseBadRequestException;
import bank.recommendationservice.fintech.exception.BaseNotFoundException;
import bank.recommendationservice.fintech.exception.RepositoryNotInitializedException;
import bank.recommendationservice.fintech.exception.RulesNotFoundException;
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
}