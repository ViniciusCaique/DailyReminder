package br.com.fiap.dailyreminder.exceptions;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> handleSecurityException(Exception exception) {
    ProblemDetail errorDetail = null;

    exception.printStackTrace();

    if (exception instanceof MethodArgumentNotValidException ex) {
      Map<String, String> fields = new HashMap<>();
      ex.getBindingResult().getFieldErrors().forEach(error -> fields.put(error.getField(), error.getDefaultMessage()));
      errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), "Validation failed");
      errorDetail.setProperty("errors", fields);
    }

    if (exception instanceof InvalidCredentials) {
      errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), "IDK");
      errorDetail.setProperty("description", "Invalid credentials");
    }

    if (exception instanceof EmailAlreadyRegistered) {
      errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), "IDK");
      errorDetail.setProperty("description", "Email already registered");
    }

    if (exception instanceof NotFound) {
      errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404), "IDK");
      errorDetail.setProperty("description", "Resource not found");
    }

    if (exception instanceof RestNotFoundException) {
      errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404), "IDK");
      errorDetail.setProperty("description", "Resource not found");
    }

    if (errorDetail == null) {
      errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), "");
      errorDetail.setProperty("description", "Internal Server Error");
    }


    return ResponseEntity.status(errorDetail.getStatus()).body(errorDetail);
  }
}
