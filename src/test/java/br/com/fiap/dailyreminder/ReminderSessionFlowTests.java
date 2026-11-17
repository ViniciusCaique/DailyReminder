package br.com.fiap.dailyreminder;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReminderSessionFlowTests {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Value("${jwt.secret}") String secret;

    private JsonNode registerAndLogin() throws Exception {
        String email = "reminder-test-" + UUID.randomUUID() + "@example.com";
        mvc.perform(post("/api/auth/sign-up").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("name", "Reminder Test", "email", email, "password", "test-password-123"))))
                .andExpect(status().isCreated());
        var result = mvc.perform(post("/api/auth/sign-in").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("email", email, "password", "test-password-123"))))
                .andExpect(status().isOk()).andReturn();
        return json.readTree(result.getResponse().getContentAsString());
    }

    @Test void remindersArePrivateAndSupportTheFullLifecycle() throws Exception {
        String owner = "Bearer " + registerAndLogin().get("token").asText();
        String other = "Bearer " + registerAndLogin().get("token").asText();
        String body = "{\"message\":\"Review notes\",\"scheduledAt\":\"2026-11-12T13:30:00Z\"}";
        var created = mvc.perform(post("/api/notes").header("Authorization", owner).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.completed").value(false)).andReturn();
        String id = json.readTree(created.getResponse().getContentAsString()).get("id").asText();
        mvc.perform(get("/api/notes").header("Authorization", other)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get("/api/notes/" + id).header("Authorization", other)).andExpect(status().isNotFound());
        mvc.perform(put("/api/notes/" + id).header("Authorization", other).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isNotFound());
        mvc.perform(patch("/api/notes/" + id).header("Authorization", other).contentType(MediaType.APPLICATION_JSON).content("{\"completed\":true}")).andExpect(status().isNotFound());
        mvc.perform(delete("/api/notes/" + id).header("Authorization", other)).andExpect(status().isNotFound());
        mvc.perform(put("/api/notes/" + id).header("Authorization", owner).contentType(MediaType.APPLICATION_JSON)
                .content(body.replace("Review notes", "Updated reminder"))).andExpect(status().isOk()).andExpect(jsonPath("$.message").value("Updated reminder"));
        mvc.perform(patch("/api/notes/" + id).header("Authorization", owner).contentType(MediaType.APPLICATION_JSON).content("{\"completed\":true}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.completed").value(true));
        mvc.perform(post("/api/notes").header("Authorization", owner).contentType(MediaType.APPLICATION_JSON).content("{\"message\":\" \"}"))
                .andExpect(status().isBadRequest());
        // A reminder can be attached only by its owner; deleting it preserves the activity.
        String activityBody = "{\"name\":\"Study\",\"duration\":30,\"dataDia\":\"2026-11-12\",\"noteId\":\"" + id + "\"}";
        mvc.perform(post("/api/activities/").header("Authorization", other).contentType(MediaType.APPLICATION_JSON).content(activityBody)).andExpect(status().isNotFound());
        var activityResult = mvc.perform(post("/api/activities/").header("Authorization", owner).contentType(MediaType.APPLICATION_JSON).content(activityBody))
                .andExpect(status().isCreated()).andReturn();
        String activityId = json.readTree(activityResult.getResponse().getContentAsString()).get("id").asText();
        mvc.perform(delete("/api/notes/" + id).header("Authorization", owner)).andExpect(status().isNoContent());
        mvc.perform(get("/api/activities/" + activityId).header("Authorization", owner)).andExpect(status().isOk()).andExpect(jsonPath("$.noteId").isEmpty());
    }

    @Test void refreshRotatesAndTokenTypesCannotBeInterchanged() throws Exception {
        var login = registerAndLogin();
        String access = login.get("token").asText();
        String refresh = login.get("refreshToken").asText();
        mvc.perform(get("/api/notes").header("Authorization", "Bearer " + refresh)).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/auth/refresh").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("refreshToken", access)))).andExpect(status().isUnauthorized());
        var rotation = mvc.perform(post("/api/auth/refresh").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("refreshToken", refresh)))).andExpect(status().isOk()).andReturn();
        var next = json.readTree(rotation.getResponse().getContentAsString());
        assertNotEquals(refresh, next.get("refreshToken").asText());
        mvc.perform(post("/api/auth/refresh").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("refreshToken", refresh)))).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/notes").header("Authorization", "Bearer " + next.get("token").asText())).andExpect(status().isOk());
    }

    @Test void expiredMalformedAndMissingTokensReturnUnauthorized() throws Exception {
        String expired = JWT.create().withIssuer("Daily").withSubject(UUID.randomUUID().toString()).withClaim("type", "access")
                .withExpiresAt(Instant.now().minusSeconds(60)).sign(Algorithm.HMAC256(secret));
        for (String token : new String[]{expired, "not-a-token"}) {
            mvc.perform(get("/api/notes").header("Authorization", "Bearer " + token)).andExpect(status().isUnauthorized());
        }
        mvc.perform(get("/api/notes")).andExpect(status().isUnauthorized());
    }
}
