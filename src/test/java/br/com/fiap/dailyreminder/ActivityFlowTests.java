package br.com.fiap.dailyreminder;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.UUID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ActivityFlowTests {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    @Test void registerSignInAndManageActivityWithoutReminder() throws Exception {
        String email = "regression-" + UUID.randomUUID() + "@example.com";
        mvc.perform(post("/api/auth/sign-up").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("name", "Regression Test", "email", email, "password", "test-password-123"))))
                .andExpect(status().isCreated());
        var login = mvc.perform(post("/api/auth/sign-in").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("email", email, "password", "test-password-123"))))
                .andExpect(status().isOk()).andReturn();
        String bearer = "Bearer " + json.readTree(login.getResponse().getContentAsString()).get("token").asText();
        mvc.perform(get("/api/auth").header("Authorization", bearer)).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value(email)).andExpect(jsonPath("$[0].password").doesNotExist());
        var created = mvc.perform(post("/api/activities/").header("Authorization", bearer).contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("name", "Study", "duration", 30, "dataDia", "2026-11-11"))))
                .andExpect(status().isCreated()).andReturn();
        String id = json.readTree(created.getResponse().getContentAsString()).get("id").asText();
        mvc.perform(get("/api/activities/" + id).header("Authorization", bearer)).andExpect(status().isOk());
        mvc.perform(patch("/api/activities/" + id).header("Authorization", bearer).contentType(MediaType.APPLICATION_JSON)
                .content("{\"duration\":45}")).andExpect(status().isOk()).andExpect(jsonPath("$.duration").value(45));
        mvc.perform(post("/api/activities/").header("Authorization", bearer).contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Study\",\"duration\":10000,\"dataDia\":\"2026-11-11\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(delete("/api/activities/" + id).header("Authorization", bearer)).andExpect(status().isNoContent());
        mvc.perform(get("/api/activities/" + id).header("Authorization", bearer)).andExpect(status().isNotFound());
    }
    @Test void richActivitiesSupportInboxSchedulingStatusAndOwnership() throws Exception {
        String owner = account(); String other = account();
        var created = mvc.perform(post("/api/activities/").header("Authorization", owner).contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Capture an idea\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("INBOX"))
                .andExpect(jsonPath("$.duration").isEmpty()).andExpect(jsonPath("$.dataDia").isEmpty()).andReturn();
        String id = json.readTree(created.getResponse().getContentAsString()).get("id").asText();
        String path = "/api/activities/" + id;
        mvc.perform(patch(path).header("Authorization", owner).contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\":\"Next step: explore the idea\",\"status\":\"IN_PROGRESS\",\"duration\":45,\"dataDia\":\"2026-11-12\",\"dueDate\":\"2026-11-15\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.description").value("Next step: explore the idea"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS")).andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.dueDate").value("2026-11-15"));
        mvc.perform(patch(path).header("Authorization", other).contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"DONE\"}"))
                .andExpect(status().isNotFound());
        mvc.perform(patch(path).header("Authorization", owner).contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"DONE\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.completed").value(true)).andExpect(jsonPath("$.completedAt").isNotEmpty());
        mvc.perform(patch(path).header("Authorization", owner).contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"TODO\",\"clearDuration\":true,\"clearScheduledDate\":true,\"clearDueDate\":true,\"description\":\"\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.completedAt").isEmpty()).andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.duration").isEmpty()).andExpect(jsonPath("$.dataDia").isEmpty()).andExpect(jsonPath("$.dueDate").isEmpty())
                .andExpect(jsonPath("$.description").value(""));
        mvc.perform(patch(path).header("Authorization", owner).contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"UNKNOWN\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(patch(path).header("Authorization", owner).contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"DONE\",\"completed\":false}"))
                .andExpect(status().isBadRequest());
        mvc.perform(patch(path).header("Authorization", owner).contentType(MediaType.APPLICATION_JSON).content("{\"completed\":true}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("DONE"));
        mvc.perform(post("/api/activities/").header("Authorization", owner).contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("name", "Too much context", "description", "x".repeat(10001)))))
                .andExpect(status().isBadRequest());
    }
    private String account() throws Exception {
        String email = "rich-" + UUID.randomUUID() + "@example.com";
        mvc.perform(post("/api/auth/sign-up").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("name", "Activity Test", "email", email, "password", "test-password-123"))))
                .andExpect(status().isCreated());
        var response = mvc.perform(post("/api/auth/sign-in").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("email", email, "password", "test-password-123"))))
                .andExpect(status().isOk()).andReturn();
        return "Bearer " + json.readTree(response.getResponse().getContentAsString()).get("token").asText();
    }

}
