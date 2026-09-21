package com.rutaexpress.contracts;

/**
 * Estado de un envío y las transiciones válidas de su máquina de estados.
 */
public enum ShipmentStatus {

    CREATED,
    ASSIGNED,
    PICKED_UP,
    IN_TRANSIT,
    DELIVERED,
    CANCELLED,
    FAILED;

    /**
     * Indica si {@code this} puede transicionar a {@code target}.
     * Los estados terminales (DELIVERED, CANCELLED, FAILED) no tienen salidas.
     */
    public boolean canTransitionTo(ShipmentStatus target) {
        if (target == null || this == target) {
            return false;
        }
        return switch (this) {
            case CREATED -> target == ASSIGNED || target == CANCELLED;
            case ASSIGNED -> target == PICKED_UP || target == CANCELLED;
            case PICKED_UP -> target == IN_TRANSIT || target == FAILED;
            case IN_TRANSIT -> target == DELIVERED || target == FAILED;
            case DELIVERED, CANCELLED, FAILED -> false;
        };
    }

    public boolean isTerminal() {
        return this == DELIVERED || this == CANCELLED || this == FAILED;
    }
}
