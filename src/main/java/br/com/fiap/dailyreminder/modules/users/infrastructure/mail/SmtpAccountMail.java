package br.com.fiap.dailyreminder.modules.users.infrastructure.mail;
import br.com.fiap.dailyreminder.modules.users.application.AccountMail;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.slf4j.LoggerFactory;
@Component
public class SmtpAccountMail implements AccountMail {
    private final ObjectProvider<JavaMailSender> sender;
    private final String from;
    private final boolean enabled;
    public SmtpAccountMail(ObjectProvider<JavaMailSender> sender, @Value("${app.mail.from:noreply@dailyreminder.local}") String from, @Value("${app.mail.enabled:false}") boolean enabled) { this.sender = sender; this.from = from; this.enabled = enabled; }
    public void send(String email, String subject, String body) {
        if (!enabled) return;
        var mailer = sender.getIfAvailable();
        if (mailer == null) { LoggerFactory.getLogger(getClass()).info("Account email delivery is disabled: configure spring.mail.host to enable it"); return; }
        var message = new SimpleMailMessage(); message.setFrom(from); message.setTo(email); message.setSubject(subject); message.setText(body);
        try { mailer.send(message); } catch (org.springframework.mail.MailException exception) { LoggerFactory.getLogger(getClass()).error("Account email delivery failed; check mail transport configuration"); }
    }
}
