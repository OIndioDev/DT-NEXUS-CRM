package com.dtnexus.crm.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail validation(Exception exception) {
        String detail;
        if (exception instanceof MethodArgumentNotValidException methodException) {
            detail = methodException.getBindingResult().getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining("; "));
        } else {
            detail = ((BindException) exception).getBindingResult().getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining("; "));
        }
        return problem(HttpStatus.BAD_REQUEST, "Dados inválidos", detail);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail malformedBody() {
        return problem(HttpStatus.BAD_REQUEST, "Corpo inválido", "O corpo da requisição não pôde ser interpretado.");
    }

    @ExceptionHandler({DataIntegrityViolationException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail conflict(Exception exception) {
        log.warn("Persistence constraint violation", exception);
        return problem(HttpStatus.CONFLICT, "Conflito de dados", "A operação viola uma regra de integridade.");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail unexpected(Exception exception, HttpServletRequest request) {
        log.error("Unhandled request failure: {} {}", request.getMethod(), request.getRequestURI(), exception);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Não foi possível concluir a operação.");
    }

    @SuppressWarnings("deprecation")
	private ProblemDetail problem(@NonNull HttpStatus status, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        return problem;
    }
}
