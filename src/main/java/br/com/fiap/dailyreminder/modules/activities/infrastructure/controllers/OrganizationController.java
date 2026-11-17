package br.com.fiap.dailyreminder.modules.activities.infrastructure.controllers;
import br.com.fiap.dailyreminder.modules.activities.application.OrganizationService;
import br.com.fiap.dailyreminder.shared.CurrentUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/activities")
public class OrganizationController {
    private final OrganizationService organization;
    public OrganizationController(OrganizationService organization) { this.organization = organization; }
    public record NameRequest(@NotBlank @Size(max = 80) String name) {}
    public record Item(UUID id, String name) {}
    @GetMapping("/projects") public List<Item> projects() { return organization.projects(CurrentUser.id()).stream().map(p -> new Item(p.getId(), p.getName())).toList(); }
    @GetMapping("/labels") public List<Item> labels() { return organization.labels(CurrentUser.id()).stream().map(p -> new Item(p.getId(), p.getName())).toList(); }
    @PostMapping("/projects") public ResponseEntity<Item> createProject(@Valid @RequestBody NameRequest request) { var p = organization.saveProject(CurrentUser.id(), null, request.name()); return ResponseEntity.status(201).body(new Item(p.getId(), p.getName())); }
    @PostMapping("/labels") public ResponseEntity<Item> createLabel(@Valid @RequestBody NameRequest request) { var p = organization.saveLabel(CurrentUser.id(), null, request.name()); return ResponseEntity.status(201).body(new Item(p.getId(), p.getName())); }
    @PatchMapping("/projects/{id}") public Item updateProject(@PathVariable UUID id, @Valid @RequestBody NameRequest request) { var p = organization.saveProject(CurrentUser.id(), id, request.name()); return new Item(p.getId(), p.getName()); }
    @PatchMapping("/labels/{id}") public Item updateLabel(@PathVariable UUID id, @Valid @RequestBody NameRequest request) { var p = organization.saveLabel(CurrentUser.id(), id, request.name()); return new Item(p.getId(), p.getName()); }
    @DeleteMapping("/projects/{id}") public ResponseEntity<Void> deleteProject(@PathVariable UUID id) { organization.deleteProject(CurrentUser.id(), id); return ResponseEntity.noContent().build(); }
    @DeleteMapping("/labels/{id}") public ResponseEntity<Void> deleteLabel(@PathVariable UUID id) { organization.deleteLabel(CurrentUser.id(), id); return ResponseEntity.noContent().build(); }
}
