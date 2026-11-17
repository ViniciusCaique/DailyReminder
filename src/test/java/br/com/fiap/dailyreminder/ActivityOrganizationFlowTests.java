package br.com.fiap.dailyreminder;

import jakarta.persistence.EntityManager;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc @Transactional
class ActivityOrganizationFlowTests {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired EntityManager entities;
    private String account() throws Exception {
        String email = "organization-" + UUID.randomUUID() + "@example.com";
        mvc.perform(post("/api/auth/sign-up").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("name", "Organization Test", "email", email, "password", "test-password-123")))).andExpect(status().isCreated());
        var result = mvc.perform(post("/api/auth/sign-in").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("email", email, "password", "test-password-123")))).andExpect(status().isOk()).andReturn();
        return "Bearer " + json.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }
    private String create(String bearer, String path, Object payload) throws Exception {
        var result = mvc.perform(post("/api/activities" + path).header("Authorization", bearer).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(payload))).andExpect(status().isCreated()).andReturn();
        entities.flush(); entities.clear(); return json.readTree(result.getResponse().getContentAsString()).get("id").asText();
    }
    private void patchActivity(String bearer, String id, Object payload) throws Exception {
        mvc.perform(patch("/api/activities/" + id).header("Authorization", bearer).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(payload))).andExpect(status().isOk());
        entities.flush(); entities.clear();
    }
    @Test void checklistIsOrderedOwnedAndIndependentOfActivityCompletion() throws Exception {
        String owner = account(), other = account();
        String id = create(owner, "/", Map.of("name", "Explore review inbox", "checklist", List.of(Map.of("text", "Read the requirements", "completed", true), Map.of("text", "Build a small prototype", "completed", false))));
        var result = mvc.perform(get("/api/activities/" + id).header("Authorization", owner)).andExpect(status().isOk()).andExpect(jsonPath("$.checklist.length()").value(2)).andReturn();
        var body = json.readTree(result.getResponse().getContentAsString());
        String first = body.get("checklist").get(0).get("id").asText(), second = body.get("checklist").get(1).get("id").asText();
        patchActivity(owner, id, Map.of("checklist", List.of(Map.of("id", second, "text", "Build a small prototype", "completed", true), Map.of("id", first, "text", "Read and clarify requirements", "completed", true))));
        mvc.perform(get("/api/activities/" + id).header("Authorization", owner)).andExpect(status().isOk()).andExpect(jsonPath("$.checklist[0].id").value(second)).andExpect(jsonPath("$.checklist[1].text").value("Read and clarify requirements")).andExpect(jsonPath("$.completed").value(false)).andExpect(jsonPath("$.status").value("INBOX"));
        mvc.perform(patch("/api/activities/" + id).header("Authorization", other).contentType(MediaType.APPLICATION_JSON).content("{\"checklist\":[]}")).andExpect(status().isNotFound());
        mvc.perform(patch("/api/activities/" + id).header("Authorization", owner).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("checklist", List.of(Map.of("id", UUID.randomUUID(), "text", "Foreign step", "completed", false)))))).andExpect(status().isNotFound());
        mvc.perform(patch("/api/activities/" + id).header("Authorization", owner).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("checklist", List.of(Map.of("id", first, "text", "One", "completed", false), Map.of("id", first, "text", "Duplicate", "completed", false)))))).andExpect(status().isBadRequest());
        patchActivity(owner, id, Map.of("name", "Renamed activity"));
        mvc.perform(get("/api/activities/" + id).header("Authorization", owner)).andExpect(jsonPath("$.checklist.length()").value(2));
        patchActivity(owner, id, Map.of("checklist", List.of()));
        mvc.perform(get("/api/activities/" + id).header("Authorization", owner)).andExpect(jsonPath("$.checklist").isEmpty());
    }
    @Test void projectsAndLabelsAreScopedAndDeletionKeepsActivities() throws Exception {
        String owner = account(), other = account();
        String project = create(owner, "/projects", Map.of("name", "Personal website"));
        String label = create(owner, "/labels", Map.of("name", "Learning"));
        String foreignProject = create(other, "/projects", Map.of("name", "Personal website"));
        String foreignLabel = create(other, "/labels", Map.of("name", "Learning"));
        mvc.perform(post("/api/activities/projects").header("Authorization", owner).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\" personal WEBSITE \"}")).andExpect(status().isConflict());
        mvc.perform(get("/api/activities/projects").header("Authorization", owner)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1)).andExpect(jsonPath("$[0].id").value(project));
        mvc.perform(post("/api/activities/").header("Authorization", owner).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("name", "Invalid project", "projectId", foreignProject)))).andExpect(status().isNotFound());
        mvc.perform(post("/api/activities/").header("Authorization", owner).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("name", "Invalid label", "labelIds", List.of(foreignLabel))))).andExpect(status().isNotFound());
        String id = create(owner, "/", Map.of("name", "Build home page", "projectId", project, "labelIds", List.of(label), "checklist", List.of(Map.of("text", "Sketch layout", "completed", true))));
        mvc.perform(patch("/api/activities/projects/" + project).header("Authorization", other).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Stolen\"}")).andExpect(status().isNotFound());
        mvc.perform(delete("/api/activities/labels/" + label).header("Authorization", other)).andExpect(status().isNotFound());
        mvc.perform(patch("/api/activities/projects/" + project).header("Authorization", owner).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Portfolio\"}")).andExpect(status().isOk());
        mvc.perform(delete("/api/activities/projects/" + project).header("Authorization", owner)).andExpect(status().isNoContent()); entities.flush(); entities.clear();
        mvc.perform(get("/api/activities/" + id).header("Authorization", owner)).andExpect(status().isOk()).andExpect(jsonPath("$.projectId").isEmpty()).andExpect(jsonPath("$.labelIds[0]").value(label)).andExpect(jsonPath("$.checklist[0].completed").value(true));
        mvc.perform(delete("/api/activities/labels/" + label).header("Authorization", owner)).andExpect(status().isNoContent()); entities.flush(); entities.clear();
        mvc.perform(get("/api/activities/" + id).header("Authorization", owner)).andExpect(status().isOk()).andExpect(jsonPath("$.labelIds").isEmpty()).andExpect(jsonPath("$.checklist.length()").value(1));
        mvc.perform(delete("/api/activities/" + id).header("Authorization", owner)).andExpect(status().isNoContent()); entities.flush(); entities.clear();
        assertEquals(0L, entities.createNativeQuery("select count(*) from tb_activity_checklist where activity_id = :id", Long.class).setParameter("id", UUID.fromString(id)).getSingleResult());
    }
    @Test void staleEditsAndInvalidOrganizationAreRejected() throws Exception {
        String owner = account();
        String id = create(owner, "/", Map.of("name", "Concurrent edits"));
        var before = mvc.perform(get("/api/activities/" + id).header("Authorization", owner)).andReturn();
        long version = json.readTree(before.getResponse().getContentAsString()).get("version").asLong();
        patchActivity(owner, id, Map.of("description", "Newer context", "version", version));
        mvc.perform(patch("/api/activities/" + id).header("Authorization", owner).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("description", "Old context", "version", version)))).andExpect(status().isConflict());
        mvc.perform(post("/api/activities/labels").header("Authorization", owner).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"  \"}")).andExpect(status().isBadRequest());
        mvc.perform(post("/api/activities/").header("Authorization", owner).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Empty step\",\"checklist\":[{\"text\":\"  \"}]}")).andExpect(status().isBadRequest());
        var steps = java.util.stream.IntStream.range(0, 101).mapToObj(i -> Map.of("text", "Step " + i)).toList();
        mvc.perform(post("/api/activities/").header("Authorization", owner).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("name", "Too many steps", "checklist", steps)))).andExpect(status().isBadRequest());
        mvc.perform(get("/api/activities/" + id).header("Authorization", owner)).andExpect(jsonPath("$.description").value("Newer context"));
    }
}
