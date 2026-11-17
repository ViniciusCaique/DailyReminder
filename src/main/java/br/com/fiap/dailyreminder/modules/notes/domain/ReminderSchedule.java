package br.com.fiap.dailyreminder.modules.notes.domain;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Set;

/** Calendar arithmetic retains local wall-clock time across daylight-saving changes. */
public final class ReminderSchedule {
    private ReminderSchedule() {}
    public static Instant next(Instant scheduled, Instant now, String timeZone, Recurrence recurrence, Set<Integer> days) {
        var zone = ZoneId.of(timeZone);
        var start = scheduled.atZone(zone);
        var today = now.atZone(zone).toLocalDate();
        var date = start.toLocalDate();
        if (recurrence == Recurrence.WEEKLY) {
            long weeks = Math.max(1, ChronoUnit.DAYS.between(date, today) / 7);
            date = date.plusWeeks(weeks);
            var candidate = ZonedDateTime.of(date, start.toLocalTime(), zone);
            return candidate.toInstant().isAfter(now) ? candidate.toInstant() : candidate.plusWeeks(1).toInstant();
        }
        date = date.plusDays(1);
        if (date.isBefore(today)) date = today;
        for (int i = 0; i < 8; i++, date = date.plusDays(1)) {
            var candidate = ZonedDateTime.of(date, start.toLocalTime(), zone).toInstant();
            if (candidate.isAfter(now) && (recurrence != Recurrence.CUSTOM || days.contains(date.getDayOfWeek().getValue()))) return candidate;
        }
        throw new IllegalArgumentException("Choose at least one weekday");
    }
}
