package br.com.fiap.dailyreminder.exceptions;

public class NotFound extends RuntimeException {
  public NotFound(String message) {
    super(message);
  }
}
