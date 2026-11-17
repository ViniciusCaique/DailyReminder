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
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc @Transactional
class AccountRecoveryFlowTests {
    @Autowired MockMvc mvc; @Autowired ObjectMapper json; @MockitoBean AccountMail mail;
    @Test void verificationAndRecoveryAreSingleUseAndResetRevokesSessions() throws Exception {
        String email="recovery-"+UUID.randomUUID()+"@example.com"; String password="original-password-123";
        var bodies=new ArrayList<String>(); doAnswer(call->{bodies.add(call.getArgument(2));return null;}).when(mail).send(eq(email),anyString(),anyString());
        mvc.perform(post("/api/auth/sign-up").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("name","Recovery Test","email",email,"password",password)))).andExpect(status().isCreated());
        String verification=bodies.get(0).split("#token=")[1].split("\\n")[0];
        mvc.perform(post("/api/auth/verify-email").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("token",verification)))).andExpect(status().isNoContent());
        mvc.perform(post("/api/auth/verify-email").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("token",verification)))).andExpect(status().isBadRequest());
        var logged=mvc.perform(post("/api/auth/sign-in").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("email",email,"password",password)))).andExpect(status().isOk()).andReturn();
        var tokens=json.readTree(logged.getResponse().getContentAsString());
        mvc.perform(get("/api/auth").header("Authorization","Bearer "+tokens.get("token").asText())).andExpect(jsonPath("$[0].emailVerified").value(true));
        for(String target:List.of(email,"unknown-"+UUID.randomUUID()+"@example.com"))mvc.perform(post("/api/auth/forgot-password").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("email",target)))).andExpect(status().isAccepted()).andExpect(content().string(""));
        assertEquals(2,bodies.size());String reset=bodies.get(1).split("#token=")[1].split("\\n")[0];
        var resetBody=json.writeValueAsString(Map.of("token",reset,"password","updated-password-123"));
        mvc.perform(post("/api/auth/reset-password").contentType(MediaType.APPLICATION_JSON).content(resetBody)).andExpect(status().isNoContent());
        mvc.perform(post("/api/auth/reset-password").contentType(MediaType.APPLICATION_JSON).content(resetBody)).andExpect(status().isBadRequest());
        mvc.perform(get("/api/notes").header("Authorization","Bearer "+tokens.get("token").asText())).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/auth/refresh").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("refreshToken",tokens.get("refreshToken").asText())))).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/auth/sign-in").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("email",email,"password","updated-password-123")))).andExpect(status().isOk());
    }
    @Autowired br.com.fiap.dailyreminder.modules.users.infrastructure.repositories.AccountTokenRepository accountTokens;
    @Autowired br.com.fiap.dailyreminder.modules.users.application.TokenJwtService jwt;
    @Test void expiredResetLinksCannotChangePasswords() throws Exception {
        String email="expired-"+UUID.randomUUID()+"@example.com";var bodies=new ArrayList<String>();
        doAnswer(call->{bodies.add(call.getArgument(2));return null;}).when(mail).send(eq(email),anyString(),anyString());
        mvc.perform(post("/api/auth/sign-up").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("name","Expired Test","email",email,"password","original-password-123")))).andExpect(status().isCreated());
        mvc.perform(post("/api/auth/forgot-password").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("email",email)))).andExpect(status().isAccepted());
        String raw=bodies.get(1).split("#token=")[1].split("\\n")[0];
        var stored=accountTokens.findUsable(jwt.hashToken(raw),"RESET_PASSWORD").orElseThrow();stored.setExpiresAt(java.time.Instant.now().minusSeconds(1));accountTokens.saveAndFlush(stored);
        mvc.perform(post("/api/auth/reset-password").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("token",raw,"password","updated-password-123")))).andExpect(status().isBadRequest());
        mvc.perform(post("/api/auth/sign-in").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("email",email,"password","original-password-123")))).andExpect(status().isOk());
    }

}
