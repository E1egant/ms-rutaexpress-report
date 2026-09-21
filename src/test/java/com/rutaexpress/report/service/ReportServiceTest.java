package com.rutaexpress.report.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.rutaexpress.contracts.ShipmentStatus;
import com.rutaexpress.contracts.dto.KpiReportDto;
import com.rutaexpress.contracts.event.ShipmentEvent;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ReportServiceTest {

    private static ShipmentEvent event(ShipmentStatus status) {
        return new ShipmentEvent(UUID.randomUUID(), 1L, status, Instant.now());
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
}
