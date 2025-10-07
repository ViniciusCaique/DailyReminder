package br.com.fiap.dailyreminder.modules.activities.application.usecases;

import br.com.fiap.dailyreminder.exceptions.RestNotFoundException;
import br.com.fiap.dailyreminder.modules.activities.domain.Activity;
import br.com.fiap.dailyreminder.modules.activities.infrastructure.dtos.request.CreateActivityRequest;
import br.com.fiap.dailyreminder.modules.activities.infrastructure.dtos.response.CreateActivityResponse;
import br.com.fiap.dailyreminder.modules.activities.infrastructure.repositories.ActivityRepository;
import br.com.fiap.dailyreminder.modules.users.infrastructure.dtos.response.CreateUserResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ActivityUseCase {

  private final ActivityRepository activityRepository;

  public ActivityUseCase(ActivityRepository activityRepository) {
    this.activityRepository = activityRepository;
  }

  public List<Activity> findAll() {
    return activityRepository.findAll();
  }

  public List<Activity> findAllUserActivies(String id) {
    return activityRepository.findByUserId(UUID.fromString(id)).orElseThrow(() -> new RestNotFoundException("Nenhuma atividade encontrada para esse usuário"));
  }

  public CreateActivityResponse create (String userId, CreateActivityRequest activityRequest) {
    Activity activity = new Activity();
    BeanUtils.copyProperties(activityRequest, activity);

    activity.setUserId(UUID.fromString(userId));

    activityRepository.save(activity);

    var response = new CreateActivityResponse(
            activity.getId().toString(),
            activity.getDuration(),
            activity.getDataDia(),
            activity.getName(),
            activity.getLembrete(),
            activity.getUserId().toString()
    );

    return response;
  }
}
