package com.multipartyloops.evochia.entrypoints.exceptions;

import com.multipartyloops.evochia.core.identity.exceptions.InvalidCredentialsFormatException;
import com.multipartyloops.evochia.entrypoints.exceptions.dtos.ErrorResponseBody;
import com.multipartyloops.evochia.persistance.exceptions.RowNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice
public class EvochiaExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {SQLIntegrityConstraintViolationException.class})
    protected ResponseEntity<Object> dataIntegrityViolation(SQLIntegrityConstraintViolationException ex, WebRequest request) {
        Object bodyOfResponse = new ErrorResponseBody("Data integrity violation occurred.");
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(value = {CannotUpdateDeactivatedUserException.class})
    protected ResponseEntity<Object> conflict(RuntimeException ex, WebRequest request) {
        Object bodyOfResponse = new ErrorResponseBody(ex.getMessage());
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    //TODO: think about the NumberFormatException
    @ExceptionHandler(value = {RowNotFoundException.class, NumberFormatException.class})
    protected ResponseEntity<Object> valueNotFound(RuntimeException ex, WebRequest request) {
        String message = ex.getMessage();
        if (ex instanceof NumberFormatException) {
            message = "User not found.";
        }
        Object bodyOfResponse = new ErrorResponseBody(message);
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class, InvalidCredentialsFormatException.class})
    protected ResponseEntity<Object> badRequest(RuntimeException ex, WebRequest request) {
        Object bodyOfResponse = new ErrorResponseBody(ex.getMessage());
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
