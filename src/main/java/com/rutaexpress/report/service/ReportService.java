package com.rutaexpress.report.service;

import com.rutaexpress.contracts.ShipmentStatus;
import com.rutaexpress.contracts.dto.HourlyBucketDto;
import com.rutaexpress.contracts.dto.KpiReportDto;
import com.rutaexpress.contracts.dto.LeadTimeDto;
import com.rutaexpress.contracts.dto.TopShipmentDto;
import com.rutaexpress.contracts.event.ShipmentEvent;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    static final int MAX_EVENTS = 10_000;

    private final ConcurrentHashMap<ShipmentStatus, Long> byStatus = new ConcurrentHashMap<>();
    private final AtomicLong totalEvents = new AtomicLong();
    private final Deque<EventRecord> log = new ConcurrentLinkedDeque<>();

    public void record(ShipmentEvent event) {
        byStatus.merge(event.status(), 1L, Long::sum);
        totalEvents.incrementAndGet();
        log.addLast(new EventRecord(event.shipmentId(), event.status(), event.occurredAt()));
        while (log.size() > MAX_EVENTS) {
            log.pollFirst();
        }
    }

    public KpiReportDto kpis() {
        Map<ShipmentStatus, Long> snapshot = new HashMap<>(byStatus);
        return new KpiReportDto(snapshot, totalEvents.get());
    }

    public KpiReportDto kpis(int rangeHours) {
        Instant since = cutoff(rangeHours);
        Map<ShipmentStatus, Long> window = new EnumMap<>(ShipmentStatus.class);
        long total = 0;
        for (EventRecord e : log) {
            if (!e.occurredAt().isBefore(since)) {
                window.merge(e.status(), 1L, Long::sum);
                total++;
            }
        }
        return new KpiReportDto(window, total);
    }

    public List<HourlyBucketDto> hourly(int rangeHours) {
        Instant since = cutoff(rangeHours);
        Map<Instant, long[]> buckets = new TreeMap<>();
        for (EventRecord e : log) {
            if (e.occurredAt().isBefore(since)) {
                continue;
            }
            long[] counts = buckets.computeIfAbsent(
                    e.occurredAt().truncatedTo(ChronoUnit.HOURS), h -> new long[3]);
            counts[2]++;
            if (e.status() == ShipmentStatus.CREATED) {
                counts[0]++;
            }
            if (e.status() == ShipmentStatus.DELIVERED) {
                counts[1]++;
            }
        }
        return buckets.entrySet().stream()
                .map(en -> new HourlyBucketDto(en.getKey().toString(),
                        en.getValue()[0], en.getValue()[1], en.getValue()[2]))
                .toList();
    }

    public LeadTimeDto leadtime(int rangeHours) {
        Instant since = cutoff(rangeHours);
        Map<Long, Instant> created = new HashMap<>();
        Map<Long, Instant> delivered = new HashMap<>();
        for (EventRecord e : log) {
            if (e.status() == ShipmentStatus.CREATED) {
                created.putIfAbsent(e.shipmentId(), e.occurredAt());
            }
            if (e.status() == ShipmentStatus.DELIVERED && !e.occurredAt().isBefore(since)) {
                delivered.putIfAbsent(e.shipmentId(), e.occurredAt());
            }
        }
        long totalSeconds = 0;
        long count = 0;
        for (Map.Entry<Long, Instant> en : delivered.entrySet()) {
            Instant start = created.get(en.getKey());
            if (start != null && !en.getValue().isBefore(start)) {
                totalSeconds += Duration.between(start, en.getValue()).getSeconds();
                count++;
            }
        }
        return new LeadTimeDto(count == 0 ? 0 : (double) totalSeconds / count, count);
    }

    public List<TopShipmentDto> topServices(int rangeHours, int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("limit debe ser positivo");
        }
        Instant since = cutoff(rangeHours);
        Map<Long, Stats> byShipment = new HashMap<>();
        for (EventRecord e : log) {
            if (e.occurredAt().isBefore(since)) {
                continue;
            }
            Stats stats = byShipment.computeIfAbsent(e.shipmentId(), id -> new Stats());
            stats.count++;
            if (stats.firstSeen == null || e.occurredAt().isBefore(stats.firstSeen)) {
                stats.firstSeen = e.occurredAt();
            }
            if (stats.lastSeen == null || !e.occurredAt().isBefore(stats.lastSeen)) {
                stats.lastSeen = e.occurredAt();
                stats.lastStatus = e.status();
            }
        }
        return byShipment.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue().count, a.getValue().count))
                .limit(limit)
                .map(en -> new TopShipmentDto(en.getKey(), en.getValue().count,
                        en.getValue().firstSeen, en.getValue().lastStatus, en.getValue().lastSeen))
                .toList();
    }

    private Instant cutoff(int rangeHours) {
        if (rangeHours <= 0) {
            throw new IllegalArgumentException("range debe ser positivo");
        }
        return Instant.now().minus(rangeHours, ChronoUnit.HOURS);
    }

    private record EventRecord(Long shipmentId, ShipmentStatus status, Instant occurredAt) {
    }

    private static class Stats {
        long count;
        Instant firstSeen;
        Instant lastSeen;
        ShipmentStatus lastStatus;
    }
}
