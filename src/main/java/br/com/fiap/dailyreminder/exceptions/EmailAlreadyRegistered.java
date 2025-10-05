package br.com.fiap.dailyreminder.exceptions;

public class EmailAlreadyRegistered extends RuntimeException {
  public EmailAlreadyRegistered(String message) {
    super(message);
  }
}
