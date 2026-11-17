package br.com.fiap.dailyreminder;
import tools.jackson.databind.ObjectMapper;
import br.com.fiap.dailyreminder.modules.users.application.AccountMail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest(properties="app.auth.require-verification=true") @AutoConfigureMockMvc @Transactional
class VerificationRequiredTests {
    @Autowired MockMvc mvc; @Autowired ObjectMapper json; @MockitoBean AccountMail mail;
    @Test void mandatoryVerificationBlocksSignInUntilVerified() throws Exception {
        String email="verify-"+UUID.randomUUID()+"@example.com";var bodies=new ArrayList<String>();
        doAnswer(call->{bodies.add(call.getArgument(2));return null;}).when(mail).send(eq(email),anyString(),anyString());
        mvc.perform(post("/api/auth/sign-up").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("name","Verify Test","email",email,"password","verification-password-123")))).andExpect(status().isCreated());
        String login=json.writeValueAsString(Map.of("email",email,"password","verification-password-123"));
        mvc.perform(post("/api/auth/sign-in").contentType(MediaType.APPLICATION_JSON).content(login)).andExpect(status().isForbidden());
        String token=bodies.get(0).split("#token=")[1].split("\\n")[0];
        mvc.perform(post("/api/auth/verify-email").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("token",token)))).andExpect(status().isNoContent());
        mvc.perform(post("/api/auth/sign-in").contentType(MediaType.APPLICATION_JSON).content(login)).andExpect(status().isOk());
    }
}
