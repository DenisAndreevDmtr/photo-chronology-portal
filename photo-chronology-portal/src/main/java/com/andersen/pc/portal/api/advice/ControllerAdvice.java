package com.andersen.pc.portal.api.advice;

import com.andersen.pc.common.exception.AuthenticationException;
import com.andersen.pc.common.exception.DbObjectConflictException;
import com.andersen.pc.common.exception.DbObjectNotFoundException;
import com.andersen.pc.common.exception.DomainObjectValidationException;
import com.andersen.pc.common.model.dto.response.ErrorResponse;
import io.jsonwebtoken.JwtException;
import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.Objects;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(DbObjectNotFoundException.class)
    public ResponseEntity<ErrorResponse> processResourceNotFoundException(Exception exception) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler({DomainObjectValidationException.class,
            HttpMessageNotReadableException.class,
            ConstraintViolationException.class,
            MissingServletRequestPartException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            InvalidDataAccessApiUsageException.class})
    public ResponseEntity<ErrorResponse> processRequiredFieldAbsentException(Exception exception) {
        ErrorResponse response = new ErrorResponse(
                BAD_REQUEST.value(),
                exception.getMessage());
        return ResponseEntity.status(BAD_REQUEST).body(response);
    }

    @ExceptionHandler({DbObjectConflictException.class})
    public ResponseEntity<ErrorResponse> processConflictException(Exception exception) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler({AuthenticationException.class,
            JwtException.class})
    public ResponseEntity<ErrorResponse> processAuthenticationException(AuthenticationException exception) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException exception) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                exception.getMessage());
        return ResponseEntity.status(FORBIDDEN).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) throws Exception {
        throw ex;
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(BindException ex) {
        String errorMessage = getErrorMessages(ex);
        ErrorResponse response = new ErrorResponse(BAD_REQUEST.value(), errorMessage);
        return ResponseEntity.status(BAD_REQUEST).body(response);
    }

    private String getErrorMessages(BindException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        if (!bindingResult.getFieldErrors().isEmpty()) {
            return bindingResult.getFieldErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(StringUtils.EMPTY);
        } else {
            return ex.getMessage();
        }
    }
}
