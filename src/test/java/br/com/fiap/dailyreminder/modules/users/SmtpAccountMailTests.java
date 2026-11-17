package br.com.fiap.dailyreminder.modules.users;
import br.com.fiap.dailyreminder.modules.users.infrastructure.mail.SmtpAccountMail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.mockito.ArgumentCaptor;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class SmtpAccountMailTests {
    @Test void enabledDeliveryUsesConfiguredSenderAndFromAddress() {
        var sender=mock(JavaMailSender.class);
        var provider=new StaticListableBeanFactory(Map.of("mailer",sender)).getBeanProvider(JavaMailSender.class);
        new SmtpAccountMail(provider,"noreply@example.com",true).send("test@example.com","Verify email","Verification message");
        var message=ArgumentCaptor.forClass(SimpleMailMessage.class);verify(sender).send(message.capture());
        assertEquals("noreply@example.com",message.getValue().getFrom());assertArrayEquals(new String[]{"test@example.com"},message.getValue().getTo());assertEquals("Verification message",message.getValue().getText());
    }
    @Test void disabledDeliveryNeverContactsTheMailTransport() {
        var sender=mock(JavaMailSender.class);
        var provider=new StaticListableBeanFactory(Map.of("mailer",sender)).getBeanProvider(JavaMailSender.class);
        new SmtpAccountMail(provider,"noreply@example.com",false).send("test@example.com","Verify email","Verification message");
        verifyNoInteractions(sender);
    }
}
