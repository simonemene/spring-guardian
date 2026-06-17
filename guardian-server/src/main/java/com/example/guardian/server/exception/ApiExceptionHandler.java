package com.example.guardian.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Maps backend exceptions to standard REST problem responses.
 *
 * @author Simone Meneghetti
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /**
     * Converts invalid requests to HTTP 400 responses.
     *
     * @param ex invalid request exception
     * @return problem response
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleBadRequest(IllegalArgumentException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("Richiesta non valida");
        detail.setDetail(ex.getMessage());
        return detail;
    }

    /**
     * Converts validation errors to HTTP 400 responses.
     *
     * @param ex validation exception
     * @return problem response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("Richiesta non valida");
        detail.setDetail(ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("La richiesta non è valida."));
        return detail;
    }

    /**
     * Converts unexpected processing errors to HTTP 500 responses.
     *
     * @param ex processing exception
     * @return problem response
     */
    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleInternal(IllegalStateException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        detail.setTitle("Errore interno");
        detail.setDetail(ex.getMessage());
        return detail;
    }
}
