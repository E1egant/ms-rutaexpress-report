package com.rutaexpress.report.messaging;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rutaexpress.contracts.ShipmentStatus;
import com.rutaexpress.contracts.event.ShipmentEvent;
import com.rutaexpress.report.service.ReportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ShipmentEventConsumerTest {

    @Mock
    ReportService service;

    private ShipmentEventConsumer consumer() {
        return new ShipmentEventConsumer(new ObjectMapper().findAndRegisterModules(), service);
    }

    @Test
    void unEventoValidoSeRegistra() {
        consumer().onEvent("{\"eventId\":\"6f1c0d2e-5b1a-4f7a-9d2e-1a2b3c4d5e6f\",\"shipmentId\":5,"
                + "\"status\":\"DELIVERED\",\"occurredAt\":\"2026-09-21T12:00:00Z\"}");

        ArgumentCaptor<ShipmentEvent> captor = ArgumentCaptor.forClass(ShipmentEvent.class);
        verify(service).record(captor.capture());
        assertThat(captor.getValue().shipmentId()).isEqualTo(5L);
        assertThat(captor.getValue().status()).isEqualTo(ShipmentStatus.DELIVERED);
    }

    @Test
    void unJsonInvalidoSeIgnoraSinLanzarExcepcion() {
        consumer().onEvent("esto no es json");

        verify(service, never()).record(any());
    }
}
