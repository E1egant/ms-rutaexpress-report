package com.rutaexpress.report.service;

import com.rutaexpress.contracts.ShipmentStatus;
import com.rutaexpress.contracts.dto.KpiReportDto;
import com.rutaexpress.contracts.event.ShipmentEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    private final ConcurrentHashMap<ShipmentStatus, Long> byStatus = new ConcurrentHashMap<>();
    private final AtomicLong totalEvents = new AtomicLong();

    public void record(ShipmentEvent event) {
        byStatus.merge(event.status(), 1L, Long::sum);
        totalEvents.incrementAndGet();
    }

    public KpiReportDto kpis() {
        Map<ShipmentStatus, Long> snapshot = new HashMap<>(byStatus);
        return new KpiReportDto(snapshot, totalEvents.get());
    }
}
