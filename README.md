# ms-rutaexpress-report

KPIs y analítica. Consume eventos de Kafka.

Spring Boot 3.3.5, Java 17+, Maven (`./mvnw`). Puerto local: **8084**. Responsable: compañero / opencode.

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/reports/kpis` | KPIs |

## Perfiles

- **por defecto (dev)**: H2 en memoria y **sin seguridad** (solo para desarrollo local).
- **`secure`**: valida el JWT de Azure AD (`AZURE_TENANT_ID`) y aplica roles desde el claim `roles`.
- **`prod`**: PostgreSQL.

## Variables de entorno

`AZURE_TENANT_ID`, `AZURE_API_AUDIENCE` (`api://<API_CLIENT_ID>`, perfil `secure`), KAFKA_BOOTSTRAP

## Pruebas

`./mvnw test` ejecuta 9 pruebas: cálculo de KPIs, consumidor Kafka y seguridad por perfil `secure`. No necesitan brokers ni base de datos externos (H2 en memoria; los listeners de RabbitMQ/Kafka se desactivan en los tests).

## Ejecutar

```bash
./mvnw test
./mvnw spring-boot:run
SPRING_PROFILES_ACTIVE=secure AZURE_TENANT_ID=<tenant> AZURE_API_AUDIENCE=api://<api-client-id> ./mvnw spring-boot:run
```

Los DTOs compartidos están copiados en `src/main/java/com/rutaexpress/contracts`; la fuente de verdad de los contratos está en el repo `Cloud-Native-1` (`contratos/`).
