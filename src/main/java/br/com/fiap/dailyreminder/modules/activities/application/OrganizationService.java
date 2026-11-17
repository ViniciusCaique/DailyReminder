package br.com.fiap.dailyreminder.modules.activities.application;
import br.com.fiap.dailyreminder.modules.activities.domain.*;
import br.com.fiap.dailyreminder.modules.activities.infrastructure.repositories.*;
import br.com.fiap.dailyreminder.exceptions.RestNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service @Transactional
public class OrganizationService {
    private final ProjectRepository projects; private final LabelRepository labels; private final ActivityRepository activities;
    public OrganizationService(ProjectRepository projects, LabelRepository labels, ActivityRepository activities) {
        this.projects = projects; this.labels = labels; this.activities = activities;
    }
    @Transactional(readOnly = true) public List<Project> projects(UUID user) { return projects.findByUserIdOrderByNameAsc(user); }
    @Transactional(readOnly = true) public List<Label> labels(UUID user) { return labels.findByUserIdOrderByNameAsc(user); }
    public Project requireProject(UUID id, UUID user) { return projects.findByIdAndUserId(id, user).orElseThrow(() -> new RestNotFoundException("Project not found")); }
    public Label requireLabel(UUID id, UUID user) { return labels.findByIdAndUserId(id, user).orElseThrow(() -> new RestNotFoundException("Label not found")); }
    private String clean(String name) {
        String value = name == null ? "" : name.trim();
        if (value.isEmpty() || value.length() > 80) throw new IllegalArgumentException("Use a name between 1 and 80 characters");
        return value;
    }
    public Project saveProject(UUID user, UUID id, String name) {
        String value = clean(name); var item = id == null ? new Project() : requireProject(id, user);
        if (projects.existsByUserIdAndNameIgnoreCaseAndIdNot(user, value, id == null ? new UUID(0, 0) : id)) throw new ActivityConflict("A project with this name already exists");
        item.setUserId(user); item.setName(value); return projects.save(item);
    }
    public Label saveLabel(UUID user, UUID id, String name) {
        String value = clean(name); var item = id == null ? new Label() : requireLabel(id, user);
        if (labels.existsByUserIdAndNameIgnoreCaseAndIdNot(user, value, id == null ? new UUID(0, 0) : id)) throw new ActivityConflict("A label with this name already exists");
        item.setUserId(user); item.setName(value); return labels.save(item);
    }
    public void deleteProject(UUID user, UUID id) {
        var project = requireProject(id, user);
        activities.findByUserIdOrderByDataDiaAsc(user).stream().filter(a -> id.equals(a.getProjectId())).forEach(a -> a.setProjectId(null));
        activities.flush(); projects.delete(project);
    }
    public void deleteLabel(UUID user, UUID id) {
        var label = requireLabel(id, user);
        activities.findByUserIdOrderByDataDiaAsc(user).forEach(a -> { if (a.getLabelIds().contains(id)) { a.getLabelIds().remove(id); } });
        activities.flush(); labels.delete(label);
    }
}
