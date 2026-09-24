package com.rutaexpress.contracts.dto;

/** Tiempo medio CREATED→DELIVERED en segundos, sobre entregas de la ventana. */
public record LeadTimeDto(double averageSeconds, long count) {
}
