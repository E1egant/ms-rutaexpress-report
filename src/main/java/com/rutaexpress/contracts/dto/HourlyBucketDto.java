package com.rutaexpress.contracts.dto;

/** Conteo de envíos en una hora (ISO-8601 truncada a la hora). */
public record HourlyBucketDto(String hour, long created, long delivered, long total) {
}
