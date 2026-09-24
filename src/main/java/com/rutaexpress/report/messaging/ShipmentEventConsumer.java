package com.rutaexpress.report.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rutaexpress.contracts.MessagingConstants;
import com.rutaexpress.contracts.event.ShipmentEvent;
import com.rutaexpress.report.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ShipmentEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ShipmentEventConsumer.class);

    private final ObjectMapper objectMapper;
    private final ReportService service;

    public ShipmentEventConsumer(ObjectMapper objectMapper, ReportService service) {
        this.objectMapper = objectMapper;
        this.service = service;
    }

    @KafkaListener(topics = {
            MessagingConstants.SHIPMENT_EVENTS_TOPIC,
            MessagingConstants.SHIPMENT_EVENTS_TOPIC_V2 }, groupId = "report")
    public void onEvent(String json) {
        try {
            ShipmentEvent event = objectMapper.readValue(json, ShipmentEvent.class);
            service.record(event);
        } catch (JsonProcessingException e) {
            log.error("No se pudo deserializar el evento de envío: {}", json, e);
        }
    }
}
