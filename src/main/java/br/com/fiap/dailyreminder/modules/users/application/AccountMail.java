package br.com.fiap.dailyreminder.modules.users.application;
/** Delivery port implemented by the identity module's infrastructure. */
public interface AccountMail { void send(String email, String subject, String body); }
