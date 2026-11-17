package br.com.fiap.dailyreminder.modules.activities;
import br.com.fiap.dailyreminder.exceptions.RestNotFoundException;
import br.com.fiap.dailyreminder.modules.activities.application.usecases.ActivityUseCase;
import br.com.fiap.dailyreminder.modules.activities.domain.Activity;
import br.com.fiap.dailyreminder.modules.activities.application.OrganizationService;
import br.com.fiap.dailyreminder.modules.activities.infrastructure.dtos.request.CreateActivityRequest;
import br.com.fiap.dailyreminder.modules.activities.infrastructure.repositories.ActivityRepository;
import br.com.fiap.dailyreminder.modules.notes.application.ReminderAccess;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class ActivityRegressionTests {
    @Test void createWithoutReminderAndComplete() {
        var repository = mock(ActivityRepository.class); var reminders = mock(ReminderAccess.class);
        var clock = Clock.fixed(Instant.parse("2026-11-11T12:00:00Z"), ZoneOffset.UTC);
        var service = new ActivityUseCase(repository, reminders, clock, mock(OrganizationService.class));
        when(repository.save(any())).thenAnswer(call -> call.getArgument(0));
        UUID user = UUID.randomUUID();
        var activity = service.create(user, "Study", 30, LocalDate.now(clock), null);
        assertNull(activity.getNoteId()); assertEquals(user, activity.getUserId());
        activity.setId(UUID.randomUUID()); when(repository.findByIdAndUserId(activity.getId(), user)).thenReturn(Optional.of(activity));
        service.update(activity.getId(), user, null, null, null, null, false, true);
        assertTrue(activity.isCompleted()); assertEquals(Instant.now(clock), activity.getCompletedAt());
        service.update(activity.getId(), user, null, null, null, null, false, false);
        assertNull(activity.getCompletedAt());
        verifyNoInteractions(reminders);
    }
    @Test void durationIsOptionalAndBounded() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            for (Integer duration : new Integer[]{0, -1, 1441, 10000}) assertFalse(factory.getValidator().validate(new CreateActivityRequest(duration, LocalDate.now(), "Study", null)).isEmpty());
            assertTrue(factory.getValidator().validate(new CreateActivityRequest(null, null, "Study", null)).isEmpty());
            assertTrue(factory.getValidator().validate(new CreateActivityRequest(1440, LocalDate.now(), "Study", null)).isEmpty());
        }
    }
    @Test void ownershipIsRequiredBeforeMutation() {
        var repository = mock(ActivityRepository.class); var service = new ActivityUseCase(repository, mock(ReminderAccess.class), Clock.systemUTC(), mock(OrganizationService.class));
        var id = UUID.randomUUID(); var user = UUID.randomUUID(); when(repository.findByIdAndUserId(id, user)).thenReturn(Optional.empty());
        assertThrows(RestNotFoundException.class, () -> service.owned(id, user));
        assertThrows(RestNotFoundException.class, () -> service.update(id, user, null, null, null, null, false, true));
        assertThrows(RestNotFoundException.class, () -> service.delete(id, user));
        verify(repository, never()).save(any()); verify(repository, never()).delete(any(Activity.class));
    }
    @Test void completionTimestampSurvivesRepeatedCompletionAndClearsOnReopen() {
        var repository = mock(ActivityRepository.class); var clock = mock(Clock.class);
        var first = Instant.parse("2026-11-11T12:00:00Z"); var second = first.plusSeconds(60);
        when(clock.instant()).thenReturn(first, second);
        when(repository.save(any())).thenAnswer(call -> call.getArgument(0));
        var service = new ActivityUseCase(repository, mock(ReminderAccess.class), clock, mock(OrganizationService.class));
        UUID user = UUID.randomUUID();
        var activity = service.create(user, "Capture idea", null, null, null);
        activity.setId(UUID.randomUUID());
        when(repository.findByIdAndUserId(activity.getId(), user)).thenReturn(Optional.of(activity));
        service.update(activity.getId(), user, null, null, null, null, false, true);
        service.update(activity.getId(), user, null, null, null, null, false, true);
        assertEquals(first, activity.getCompletedAt());
        service.update(activity.getId(), user, null, null, null, null, false, false);
        assertNull(activity.getCompletedAt());
        service.update(activity.getId(), user, null, null, null, null, false, true);
        assertEquals(second, activity.getCompletedAt());
    }

}
