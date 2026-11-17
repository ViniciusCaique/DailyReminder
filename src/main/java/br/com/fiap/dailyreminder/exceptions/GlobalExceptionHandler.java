package br.com.fiap.dailyreminder.exceptions;

import org.springframework.http.ProblemDetail;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> handleException(Exception exception) {
    ProblemDetail problem;
    if (exception instanceof MethodArgumentNotValidException validation) {
      Map<String, String> fields = new HashMap<>();
      validation.getBindingResult().getFieldErrors().forEach(error -> fields.put(error.getField(), error.getDefaultMessage()));
      problem = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), "Check the submitted fields");
      problem.setProperty("errors", fields);
    } else if (exception instanceof br.com.fiap.dailyreminder.modules.activities.application.ActivityConflict) {
      problem = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(409), exception.getMessage());
    } else if (exception instanceof org.springframework.dao.OptimisticLockingFailureException) {
      problem = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(409), "This activity changed. Reload it before saving your edits.");
    } else if (exception instanceof org.springframework.dao.DataIntegrityViolationException) {
      problem = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(409), "This change conflicts with an existing item. Refresh and try again.");
    } else if (exception instanceof HttpMessageNotReadableException) {
      problem = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), "Invalid request body");
    } else if (exception instanceof IllegalArgumentException || exception instanceof java.time.DateTimeException) {
      problem = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), "Check the submitted values");
    } else if (exception instanceof EmailNotVerified) {
      problem = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), "Verify your email before signing in");
      problem.setProperty("description", "Email verification required");
    } else if (exception instanceof InvalidCredentials) {
      problem = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), "Invalid credentials or session");
      problem.setProperty("description", "Invalid credentials");
    } else if (exception instanceof EmailAlreadyRegistered) {
      problem = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(409), "This email is already registered");
      problem.setProperty("description", "Email already registered");
    } else if (exception instanceof NotFound || exception instanceof RestNotFoundException) {
      problem = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404), "Resource not found");
    } else {
      log.error("Unexpected API error", exception);
      problem = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), "An unexpected error occurred");
    }
    return ResponseEntity.status(problem.getStatus()).body(problem);
  }
}
