package com.moroccoit.attestation.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateUtils {

    private static final DateTimeFormatter FRENCH_DATE_FORMATTER =
        DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRENCH);

    private static final DateTimeFormatter SHORT_DATE_FORMATTER =
        DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter DATETIME_FORMATTER =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static String formatFrenchDate(LocalDate date) {
        if (date == null) return "";
        return date.format(FRENCH_DATE_FORMATTER);
    }

    public static String formatShortDate(LocalDate date) {
        if (date == null) return "";
        return date.format(SHORT_DATE_FORMATTER);
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DATETIME_FORMATTER);
    }
}
