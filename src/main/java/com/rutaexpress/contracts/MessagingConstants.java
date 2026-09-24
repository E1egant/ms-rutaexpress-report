package com.rutaexpress.contracts;

/**
 * Nombres canónicos de los recursos de mensajería (cola RabbitMQ y topic Kafka).
 */
public final class MessagingConstants {

    private MessagingConstants() {
    }

    public static final String NOTIFICATIONS_QUEUE = "rutaexpress.notifications";
    public static final String SHIPMENT_EVENTS_TOPIC = "shipment-events";
    /** Nombre del caso; se escucha junto al anterior durante la migración del productor. */
    public static final String SHIPMENT_EVENTS_TOPIC_V2 = "shipments.events";
}
