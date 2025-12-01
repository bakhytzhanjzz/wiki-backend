package com.kz.wiki.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Utility class for date operations.
 */
public class DateUtil {

    /**
     * Gets the start of the day (00:00:00) for the given date.
     */
    public static LocalDateTime getStartOfDay(LocalDate date) {
        return date.atStartOfDay();
    }

    /**
     * Gets the end of the day (23:59:59.999999999) for the given date.
     */
    public static LocalDateTime getEndOfDay(LocalDate date) {
        return date.atTime(23, 59, 59, 999999999);
    }

    /**
     * Gets the start of today.
     */
    public static LocalDateTime getStartOfToday() {
        return getStartOfDay(LocalDate.now());
    }

    /**
     * Gets the end of today.
     */
    public static LocalDateTime getEndOfToday() {
        return getEndOfDay(LocalDate.now());
    }

    /**
     * Converts LocalDateTime to epoch milliseconds.
     */
    public static long toEpochMillis(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}


