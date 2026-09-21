package com.rutaexpress.report.web;

import com.rutaexpress.contracts.ShipmentStatus;
import java.util.Map;

public record KpiReportDto(Map<ShipmentStatus, Long> byStatus, long totalEvents) {
}
