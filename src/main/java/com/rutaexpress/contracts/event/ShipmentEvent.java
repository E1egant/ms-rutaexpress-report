package com.rutaexpress.contracts.event;

import com.rutaexpress.contracts.ShipmentStatus;
import java.time.Instant;
import java.util.UUID;

/**
 * Evento de dominio publicado en Kafka cuando cambia el estado de un envío.
 * Lo consumen los servicios de auditoría y reportería.
 */
public record ShipmentEvent(
        UUID eventId,
        Long shipmentId,
        ShipmentStatus status,
        Instant occurredAt) {
}
