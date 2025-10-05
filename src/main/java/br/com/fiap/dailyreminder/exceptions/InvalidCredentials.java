package br.com.fiap.dailyreminder.exceptions;

public class InvalidCredentials extends RuntimeException {
  public InvalidCredentials(String message) {
    super(message);
  }
}
