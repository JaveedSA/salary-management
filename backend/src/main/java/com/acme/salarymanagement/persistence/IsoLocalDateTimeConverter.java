package com.acme.salarymanagement.persistence;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class IsoLocalDateTimeConverter implements AttributeConverter<LocalDateTime, String> {

        private static final DateTimeFormatter STORAGE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        private static final DateTimeFormatter READ_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd HH:mm:ss")
            .optionalStart().appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).optionalEnd()
            .toFormatter();

    @Override
    public String convertToDatabaseColumn(LocalDateTime value) {
        return value == null ? null : STORAGE_FORMAT.format(value);
    }

    @Override
    public LocalDateTime convertToEntityAttribute(String value) {
        if (value == null || value.isBlank()) return null;
        if (value.matches("\\d+")) {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(value)), ZoneOffset.UTC);
        }
        return LocalDateTime.parse(value.replace('T', ' '), READ_FORMAT);
    }
}