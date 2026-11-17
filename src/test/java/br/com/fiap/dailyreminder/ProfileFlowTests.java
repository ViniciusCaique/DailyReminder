package br.com.fiap.dailyreminder;

import tools.jackson.databind.ObjectMapper;
import br.com.fiap.dailyreminder.modules.users.application.TokenJwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties={"app.mail.enabled=false","app.auth.require-verification=false"})
@AutoConfigureMockMvc
@Transactional
class ProfileFlowTests {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired TokenJwtService jwt;
    private record Account(String email, String token) {}
    private Account account(String name) throws Exception {
        String email="profile-"+UUID.randomUUID()+"@example.com";
        mvc.perform(post("/api/auth/sign-up").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("name",name,"email",email,"password","profile-password-123")))).andExpect(status().isCreated());
        var result=mvc.perform(post("/api/auth/sign-in").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("email",email,"password","profile-password-123")))).andExpect(status().isOk()).andReturn();
        return new Account(email,json.readTree(result.getResponse().getContentAsString()).get("token").asText());
    }
    @Test void profileChangesAreScopedAndCannotChangeCredentialsOrVerification() throws Exception {
        var owner=account("Original Name");var other=account("Other Account");
        String otherId=jwt.validateAccess(other.token()).getSubject();
        mvc.perform(patch("/api/auth/profile").header("Authorization","Bearer "+owner.token()).contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("name","  Updated Name  ","email",other.email(),"userId",otherId,"emailVerified",true))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Updated Name")).andExpect(jsonPath("$.email").value(owner.email())).andExpect(jsonPath("$.emailVerified").value(false));
        mvc.perform(get("/api/auth").header("Authorization","Bearer "+owner.token())).andExpect(jsonPath("$[0].name").value("Updated Name"));
        mvc.perform(get("/api/auth").header("Authorization","Bearer "+other.token())).andExpect(jsonPath("$[0].name").value("Other Account"));
        for(String name:List.of(" ","x".repeat(256)))mvc.perform(patch("/api/auth/profile").header("Authorization","Bearer "+owner.token()).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("name",name)))).andExpect(status().isBadRequest());
        mvc.perform(patch("/api/auth/profile").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Anonymous\"}")).andExpect(status().isUnauthorized());
    }
}
