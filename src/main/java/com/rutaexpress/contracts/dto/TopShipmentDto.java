package com.rutaexpress.contracts.dto;

import com.rutaexpress.contracts.ShipmentStatus;
import java.time.Instant;

/** Envío rankeado por cantidad de eventos (más movimientos primero). */
public record TopShipmentDto(Long shipmentId, long eventCount, Instant firstSeen,
        ShipmentStatus lastStatus, Instant lastSeen) {
}
