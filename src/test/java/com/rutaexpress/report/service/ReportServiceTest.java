package com.rutaexpress.report.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.rutaexpress.contracts.ShipmentStatus;
import com.rutaexpress.contracts.dto.HourlyBucketDto;
import com.rutaexpress.contracts.dto.KpiReportDto;
import com.rutaexpress.contracts.dto.LeadTimeDto;
import com.rutaexpress.contracts.dto.TopShipmentDto;
import com.rutaexpress.contracts.event.ShipmentEvent;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ReportServiceTest {

    private static ShipmentEvent event(ShipmentStatus status) {
        return event(1L, status, Instant.now());
    }

    private static ShipmentEvent event(Long shipmentId, ShipmentStatus status, Instant occurredAt) {
        return new ShipmentEvent(UUID.randomUUID(), shipmentId, status, null, occurredAt);
    }

    @Test
    void sinEventosLosKpisEstanEnCero() {
        KpiReportDto kpis = new ReportService().kpis();

        assertThat(kpis.totalEvents()).isZero();
        assertThat(kpis.byStatus()).isEmpty();
    }

    @Test
    void cuentaLosEventosPorEstado() {
        ReportService service = new ReportService();
        service.record(event(ShipmentStatus.CREATED));
        service.record(event(ShipmentStatus.CREATED));
        service.record(event(ShipmentStatus.DELIVERED));

        KpiReportDto kpis = service.kpis();

        assertThat(kpis.totalEvents()).isEqualTo(3);
        assertThat(kpis.byStatus()).containsEntry(ShipmentStatus.CREATED, 2L)
                .containsEntry(ShipmentStatus.DELIVERED, 1L);
    }

    @Test
    void elSnapshotNoCambiaSiLlegaOtroEvento() {
        ReportService service = new ReportService();
        service.record(event(ShipmentStatus.CREATED));
        KpiReportDto snapshot = service.kpis();

        service.record(event(ShipmentStatus.CREATED));

        assertThat(snapshot.byStatus()).containsEntry(ShipmentStatus.CREATED, 1L);
    }

    @Test
    void kpisConRangoFiltraVentana() {
        ReportService service = new ReportService();
        Instant now = Instant.now();
        service.record(event(1L, ShipmentStatus.CREATED, now.minus(1, ChronoUnit.HOURS)));
        service.record(event(2L, ShipmentStatus.CREATED, now.minus(30, ChronoUnit.HOURS)));

        KpiReportDto kpis = service.kpis(24);

        assertThat(kpis.totalEvents()).isEqualTo(1);
        assertThat(kpis.byStatus()).containsEntry(ShipmentStatus.CREATED, 1L);
    }

    @Test
    void hourlyAgrupaPorHora() {
        ReportService service = new ReportService();
        Instant hour = Instant.now().truncatedTo(ChronoUnit.HOURS);
        service.record(event(1L, ShipmentStatus.CREATED, hour.plus(10, ChronoUnit.MINUTES)));
        service.record(event(2L, ShipmentStatus.DELIVERED, hour.plus(20, ChronoUnit.MINUTES)));
        service.record(event(3L, ShipmentStatus.CREATED, hour.minus(2, ChronoUnit.HOURS)));

        List<HourlyBucketDto> buckets = service.hourly(24);

        assertThat(buckets).hasSize(2);
        HourlyBucketDto current = buckets.stream()
                .filter(b -> b.hour().equals(hour.toString())).findFirst().orElseThrow();
        assertThat(current.created()).isEqualTo(1);
        assertThat(current.delivered()).isEqualTo(1);
        assertThat(current.total()).isEqualTo(2);
    }

    @Test
    void leadtimePromediaEntregas() {
        ReportService service = new ReportService();
        Instant t0 = Instant.now().minus(5, ChronoUnit.HOURS);
        service.record(event(1L, ShipmentStatus.CREATED, t0));
        service.record(event(1L, ShipmentStatus.DELIVERED, t0.plus(1, ChronoUnit.HOURS)));
        service.record(event(2L, ShipmentStatus.CREATED, t0));
        service.record(event(2L, ShipmentStatus.DELIVERED, t0.plus(3, ChronoUnit.HOURS)));
        service.record(event(3L, ShipmentStatus.CREATED, t0));

        LeadTimeDto leadtime = service.leadtime(24);

        assertThat(leadtime.count()).isEqualTo(2);
        assertThat(leadtime.averageSeconds()).isEqualTo(7200.0);
    }

    @Test
    void topServicesOrdenaPorActividadYLimita() {
        ReportService service = new ReportService();
        service.record(event(1L, ShipmentStatus.CREATED, Instant.now()));
        service.record(event(1L, ShipmentStatus.ASSIGNED, Instant.now()));
        service.record(event(1L, ShipmentStatus.PICKED_UP, Instant.now()));
        service.record(event(2L, ShipmentStatus.CREATED, Instant.now()));

        List<TopShipmentDto> top = service.topServices(24, 1);

        assertThat(top).hasSize(1);
        assertThat(top.get(0).shipmentId()).isEqualTo(1L);
        assertThat(top.get(0).eventCount()).isEqualTo(3L);
        assertThat(top.get(0).lastStatus()).isEqualTo(ShipmentStatus.PICKED_UP);
    }

    @Test
    void rangoInvalidoLanzaIllegalArgument() {
        ReportService service = new ReportService();

        assertThatThrownBy(() -> service.hourly(0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.topServices(24, 0)).isInstanceOf(IllegalArgumentException.class);
    }
}
