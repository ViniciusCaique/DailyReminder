package br.com.fiap.dailyreminder.modules.activities.application.usecases;

import br.com.fiap.dailyreminder.exceptions.RestNotFoundException;
import br.com.fiap.dailyreminder.modules.activities.domain.Activity;
import br.com.fiap.dailyreminder.modules.activities.infrastructure.dtos.request.CreateActivityRequest;
import br.com.fiap.dailyreminder.modules.activities.infrastructure.dtos.response.ActivityResponse;
import br.com.fiap.dailyreminder.modules.activities.infrastructure.dtos.response.CreateActivityResponse;
import br.com.fiap.dailyreminder.modules.activities.infrastructure.repositories.ActivityRepository;
import br.com.fiap.dailyreminder.modules.users.infrastructure.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ActivityUseCase {

  private final ActivityRepository activityRepository;
  private final UserRepository userRepository;

  public ActivityUseCase(ActivityRepository activityRepository, UserRepository userRepository) {
    this.activityRepository = activityRepository;
    this.userRepository = userRepository;
  }

  public List<ActivityResponse> findAll() {
    return activityRepository.findAll()
            .stream()
            .map(ActivityResponse::from)
            .toList();
  }

  public List<ActivityResponse> findAllUserActivies(String id) {
    return activityRepository.findByUserId(UUID.fromString(id))
            .orElseThrow(() -> new RestNotFoundException("Nenhuma atividade encontrada para esse usuario"))
            .stream()
            .map(ActivityResponse::from)
            .toList();
  }

  public CreateActivityResponse create(String userId, CreateActivityRequest activityRequest) {
    Activity activity = new Activity();
    activity.setDuration(activityRequest.duration());
    activity.setDataDia(activityRequest.dataDia());
    activity.setName(activityRequest.name());
    activity.setLembrete(activityRequest.note());
    activity.setUser(userRepository.getReferenceById(UUID.fromString(userId)));

    activityRepository.save(activity);

    return new CreateActivityResponse(
            activity.getId().toString(),
            activity.getDuration(),
            activity.getDataDia(),
            activity.getName(),
            activity.getLembrete(),
            activity.getUser().getId().toString()
    );
  }
}
