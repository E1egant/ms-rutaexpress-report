package com.rutaexpress.contracts;

/**
 * Nombres canónicos de los recursos de mensajería (cola RabbitMQ y topic Kafka).
 */
public final class MessagingConstants {

    private MessagingConstants() {
    }

    public static final String NOTIFICATIONS_QUEUE = "rutaexpress.notifications";
    public static final String SHIPMENT_EVENTS_TOPIC = "shipment-events";
}
