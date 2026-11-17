package br.com.fiap.dailyreminder.modules.notes;
import br.com.fiap.dailyreminder.modules.notes.domain.*;
import org.junit.jupiter.api.Test;
import java.time.*;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
class RecurrenceTests {
    @Test void dailyRecurrencePreservesWallClockAcrossDaylightSaving() {
        var start = ZonedDateTime.of(2026, 3, 7, 9, 0, 0, 0, ZoneId.of("America/New_York"));
        var next = ReminderSchedule.next(start.toInstant(), start.toInstant(), "America/New_York", Recurrence.DAILY, Set.of());
        assertEquals(9, next.atZone(start.getZone()).getHour()); assertEquals(23, Duration.between(start.toInstant(), next).toHours());
    }
    @Test void weeklySkipsMissedOccurrencesAndCustomUsesSelectedWeekdays() {
        var now = Instant.parse("2026-11-11T12:00:00Z");
        assertEquals(Instant.parse("2026-11-16T09:00:00Z"), ReminderSchedule.next(Instant.parse("2026-10-05T09:00:00Z"), now, "UTC", Recurrence.WEEKLY, Set.of()));
        assertEquals(Instant.parse("2026-11-13T09:00:00Z"), ReminderSchedule.next(Instant.parse("2026-11-09T09:00:00Z"), now, "UTC", Recurrence.CUSTOM, Set.of(1,5)));
        assertThrows(IllegalArgumentException.class, () -> ReminderSchedule.next(now, now, "UTC", Recurrence.CUSTOM, Set.of()));
    }
}
