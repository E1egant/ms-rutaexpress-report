# ms-rutaexpress-report

KPIs y analítica. Consume eventos de Kafka.

Spring Boot 3.3.5, Java 17+, Maven (`./mvnw`). Puerto local: **8084**. Responsable: compañero / opencode.

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/reports/kpis` | KPIs acumulados (o ventana con `?range=` en horas) |
| GET | `/api/reports/hourly` | envíos por hora (`?range=`, default 24) |
| GET | `/api/reports/leadtime` | tiempo medio CREATED→DELIVERED (`?range=`, default 24) |
| GET | `/api/reports/top-services` | envíos con más movimientos (`?range=&limit=`) |

## Perfiles

- **por defecto (dev)**: **sin seguridad** (solo para desarrollo local).
- **`secure`**: valida el JWT de Azure AD (`AZURE_TENANT_ID` + `AZURE_API_AUDIENCE`) y exige rol `Admin`.
- **`prod`**: sin cambios (servicio sin BD; métricas en memoria).

## Variables de entorno

`AZURE_TENANT_ID`, `AZURE_API_AUDIENCE` (GUID de la API, `<API_CLIENT_ID>`; perfil `secure`), KAFKA_BOOTSTRAP

## Pruebas

`./mvnw test` ejecuta 18 pruebas: cálculo de KPIs y nuevas métricas, consumidor Kafka, API y seguridad por perfil `secure`. No necesitan brokers externos (los listeners de Kafka se desactivan en los tests).

## Ejecutar

```bash
./mvnw test
./mvnw spring-boot:run
SPRING_PROFILES_ACTIVE=secure AZURE_TENANT_ID=<tenant> AZURE_API_AUDIENCE=<api-client-id> ./mvnw spring-boot:run
```

Los DTOs compartidos están copiados en `src/main/java/com/rutaexpress/contracts`; la fuente de verdad de los contratos está en el repo `Cloud-Native-1` (`contratos/`).
