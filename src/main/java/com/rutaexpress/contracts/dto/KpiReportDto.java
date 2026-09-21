package com.rutaexpress.contracts.dto;

import com.rutaexpress.contracts.ShipmentStatus;
import java.util.Map;

/**
 * Resumen de KPIs de envíos calculado por el servicio de reportería.
 */
public record KpiReportDto(Map<ShipmentStatus, Long> byStatus, long totalEvents) {
}
