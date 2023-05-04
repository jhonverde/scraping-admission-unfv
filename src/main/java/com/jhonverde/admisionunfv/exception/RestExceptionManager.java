package com.jhonverde.admisionunfv.exception;

import com.jhonverde.admisionunfv.model.dto.ApiErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class RestExceptionManager {

    private final String MESSAGE_GENERIC_EXCEPTION = "En estos momentos no lo podemos atender, intente nuevamente en unos minutos";

    @ExceptionHandler(value = {MethodArgumentNotValidException.class, HttpRequestMethodNotSupportedException.class, HttpMessageNotReadableException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiErrorResponseDto handleBadRequestException(Exception ex) {
        log.error("BadRequestException: {}", ex.getMessage());

        List<FieldError> fieldErrors;
        String message;

        if(ex instanceof MethodArgumentNotValidException) {
            fieldErrors = ((MethodArgumentNotValidException) ex).getBindingResult().getFieldErrors();
            message = fieldErrors.stream().map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("|"));
        }else if(ex instanceof HttpRequestMethodNotSupportedException) {
            message = "Path request inválido, agregar párametros o variables";
        }else if(ex instanceof HttpMessageNotReadableException) {
            message = "Body JSON de la solicitud es inválida";
        }else {
            message = MESSAGE_GENERIC_EXCEPTION;
        }

        return createResponseError(message);
    }

    @ExceptionHandler(value = {NotFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ApiErrorResponseDto handleNotFoundException(Exception ex) {
        log.error("NotFoundException: {}", ex.getMessage(), ex);

        return createResponseError(ex.getMessage());
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponseDto handleException(Exception ex) {
        log.error("Exception: {}", ex.getMessage(), ex);

        return createResponseError(MESSAGE_GENERIC_EXCEPTION);
    }

    private ApiErrorResponseDto createResponseError(String message) {
        return ApiErrorResponseDto.builder()
                .message(message)
                .build();
    }
}
