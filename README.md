# Prueba Tecnica - Backend (Spring Boot)

API REST para el caso de uso de banca: clientes, cuentas, movimientos y reporte de estado de cuenta.

## Stack

- Java 21, Spring Boot 3.5.9, Maven 3.9.x
- Spring Web + Spring Data JPA + Spring Validation + Spring Security
- MySQL 8 (via Docker Compose) / H2 in-memory para tests
- Lombok + MapStruct
- springdoc-openapi (Swagger UI)

## Requisitos

- JDK 21
- Maven 3.9+
- Docker Desktop (para la base de datos MySQL local)

## Como levantar

```bash
# 1. MySQL en docker
docker compose up -d

# 2. Compilar y arrancar
mvn clean spring-boot:run
```

La API queda disponible en `http://localhost:8081`.
Swagger UI: `http://localhost:8081/swagger-ui.html`.

## Tests

```bash
mvn test
```

Cubre los casos:
- `POST /movimientos` con saldo cero → 400 "Saldo no disponible".
- `POST /movimientos` cuando se excede el cupo diario → 400 "Cupo diario Excedido".
- `POST /movimientos` deposito valido → 201.
- `POST /clientes` datos validos → 201.
- `GET /clientes/{id}` no existe → 404.
- `POST /clientes` sin nombre → 400 (validacion bean).

## Postman

`postman/PruebaTecnica.postman_collection.json` contiene todas las peticiones.
Variable `baseUrl = http://localhost:8081`.

## Scripts SQL

- `scripts/01-schema.sql` - DDL de las 3 tablas (cliente, cuenta, movimiento).
- `scripts/02-inserts.sql` - 4 clientes + 5 cuentas del caso de uso del PDF.

Docker Compose monta `./scripts` en `/docker-entrypoint-initdb.d/`, asi que la
primera vez que MySQL arranca aplica el schema y los inserts automaticamente.
Las contrasenas de los inserts son hashes BCrypt (`$2a$10$...`); el plano por defecto
es `1234`.

## Reglas de negocio implementadas

- `MovimientoService.registrar`:
  - Suma con signo: deposito positivo, retiro negativo.
  - Si la cuenta esta inactiva → `ValidacionException`.
  - Si el saldo actual es 0 o el saldo resultante seria negativo → `SaldoNoDisponibleException` ("Saldo no disponible").
  - Si la suma de retiros del dia (incluido el actual) supera `banca.cupo-diario-retiro` → `CupoDiarioExcedidoException` ("Cupo diario Excedido"). Configurable en `application.yml`, default 1000.
- Operacion `@Transactional` para que saldo + movimiento se persistan atomicamente.

## Endpoints

| Metodo | Path                                | Descripcion                         |
| ------ | ----------------------------------- | ----------------------------------- |
| GET    | /clientes                           | Listar clientes                     |
| GET    | /clientes/{id}                      | Obtener cliente por id              |
| GET    | /clientes/by-cliente-id/{clienteId} | Obtener cliente por clienteId       |
| POST   | /clientes                           | Crear cliente                       |
| PUT    | /clientes/{id}                      | Actualizar cliente                  |
| DELETE | /clientes/{id}                      | Eliminar cliente                    |
| GET    | /cuentas                            | Listar cuentas (filtra ?clienteId=) |
| GET    | /cuentas/{numeroCuenta}             | Obtener cuenta                      |
| POST   | /cuentas                            | Crear cuenta                        |
| PUT    | /cuentas/{numeroCuenta}             | Actualizar cuenta                   |
| DELETE | /cuentas/{numeroCuenta}             | Eliminar cuenta                     |
| GET    | /movimientos                        | Listar (filtra ?numeroCuenta=)      |
| GET    | /movimientos/{id}                   | Obtener movimiento                  |
| POST   | /movimientos                        | Registrar deposito o retiro         |
| DELETE | /movimientos/{id}                   | Eliminar movimiento                 |
| GET    | /reportes?clienteId=&desde=&hasta=  | Reporte estado de cuenta            |

## Estructura de excepciones

`BancaException` base + `EntidadNotFoundException`, `SaldoNoDisponibleException`,
`CupoDiarioExcedidoException`, `ValidacionException`. Todas mapeadas en
`GlobalExceptionHandler` a un body JSON estandar:

```json
{
  "timestamp": "2026-04-24T22:00:00-05:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Saldo no disponible",
  "path": "/movimientos"
}
```
