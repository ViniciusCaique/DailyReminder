package br.com.fiap.dailyreminder;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest @AutoConfigureMockMvc @Transactional
class PlanningFlowTests {
    @Autowired MockMvc mvc; @Autowired ObjectMapper json;
    @Test void recurringCompletionIsIdempotentAndActivitiesCanDetachReminders() throws Exception {
        String email="planning-"+UUID.randomUUID()+"@example.com";
        mvc.perform(post("/api/auth/sign-up").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("name","Planning Test","email",email,"password","planning-password-123")))).andExpect(status().isCreated());
        var login=mvc.perform(post("/api/auth/sign-in").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("email",email,"password","planning-password-123")))).andReturn();
        String bearer="Bearer "+json.readTree(login.getResponse().getContentAsString()).get("token").asText();
        String occurrence=Instant.now().minusSeconds(3600).toString();
        var created=mvc.perform(post("/api/notes").header("Authorization",bearer).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("message","Daily study","scheduledAt",occurrence,"recurrence","DAILY","timeZone","America/Sao_Paulo")))).andExpect(status().isCreated()).andReturn();
        var reminder=json.readTree(created.getResponse().getContentAsString());String id=reminder.get("id").asText();
        String completion=json.writeValueAsString(Map.of("completed",true,"occurrence",reminder.get("scheduledAt").asText()));
        var completed=mvc.perform(patch("/api/notes/"+id).header("Authorization",bearer).contentType(MediaType.APPLICATION_JSON).content(completion)).andExpect(status().isOk()).andExpect(jsonPath("$.completed").value(false)).andReturn();
        String next=json.readTree(completed.getResponse().getContentAsString()).get("scheduledAt").asText();assertTrue(Instant.parse(next).isAfter(Instant.now()));
        mvc.perform(patch("/api/notes/"+id).header("Authorization",bearer).contentType(MediaType.APPLICATION_JSON).content(completion)).andExpect(status().isOk()).andExpect(jsonPath("$.scheduledAt").value(next));
        var activityResult=mvc.perform(post("/api/activities/").header("Authorization",bearer).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("name","Study","duration",30,"dataDia","2026-11-11","noteId",id)))).andExpect(status().isCreated()).andReturn();
        String activityId=json.readTree(activityResult.getResponse().getContentAsString()).get("id").asText();
        mvc.perform(patch("/api/activities/"+activityId).header("Authorization",bearer).contentType(MediaType.APPLICATION_JSON).content("{\"completed\":true,\"clearReminder\":true}")).andExpect(status().isOk()).andExpect(jsonPath("$.completed").value(true)).andExpect(jsonPath("$.noteId").isEmpty()).andExpect(jsonPath("$.completedAt").isNotEmpty());
    }
}
