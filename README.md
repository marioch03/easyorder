
---

# EasyOrder — Sistema de Gestión de Pedidos para Restaurantes

> Plataforma **multi-tenant** de digitalización integral para restaurantes: desde el menú digital accesible por QR para el comensal hasta el Kitchen Display System (KDS) en tiempo real para cocina y el panel de administración.

---

## Índice

1. [Descripción General](#descripción-general)
2. [Características Principales](#características-principales)
3. [Stack Tecnológico](#stack-tecnológico)
4. [Arquitectura del Backend y Multi-Tenancy](#arquitectura-del-backend-y-multi-tenancy)
5. [Comunicación en Tiempo Real y Eventos](#comunicación-en-tiempo-real-y-eventos)
6. [Seguridad y Control de Acceso](#seguridad-y-control-de-acceso)
7. [Roles y Perfiles de Usuario](#roles-y-perfiles-de-usuario)
8. [Interfaces de Usuario](#interfaces-de-usuario)
9. [Calidad y Suite de Pruebas](#calidad-y-suite-de-pruebas)
10. [Despliegue y Puesta en Marcha](#despliegue-y-puesta-en-marcha)
11. [Licencia](#licencia)

---

## Descripción General

**EasyOrder** es una plataforma web full-stack diseñada para digitalizar, automatizar y agilizar el ciclo completo de pedidos en el sector de la restauración. Su propósito principal es suprimir la dependencia del papel físico, eliminar errores y malentendidos entre sala y cocina, y proporcionar una experiencia adaptada y en tiempo real para cada perfil (clientes, camareros, cocineros y administradores).

La plataforma implementa una arquitectura **multi-tenant con aislamiento estricto**: una única instancia centralizada da servicio a múltiples restaurantes simultáneos, garantizando total privacidad de catálogo, comensales, pedidos, empleados y métricas de facturación mediante un identificador de tenant único (`id_tenant`).

---

## Características Principales

- **Menú digital accesible por QR:** El comensal escanea el QR de su mesa y accede instantáneamente al catálogo interactivo desde su navegador móvil, sin necesidad de registro ni descarga de aplicaciones.
- **Comandas y pedidos en tiempo real:** Los pedidos emitidos se propagan en milisegundos a cocina (KDS) y sala vía Server-Sent Events (SSE), sin recargas de página.
- **Kitchen Display System (KDS):** Interfaz interactiva de cocina organizada por zonas de preparación con temporizadores y alertas visuales de tiempo de espera.
- **Gestión integral de mesas:** Monitorización visual por estados (Libre, Ocupada, Esperando cuenta), apertura/cierre de sesiones y generación de accesos QR dinámicos.
- **Comandas manuales:** TPV rápido para que el personal de sala registre pedidos cuando el cliente prefiere atención tradicional.
- **Modificadores y opciones de producto:** Extras configurables (puntos de carne, salsas, suplementos) con impacto directo en el cálculo de precios.
- **Control de alérgenos:** Información adaptada a la normativa europea, distinguiendo presencia directa y trazas.
- **Gestión de cuenta y cierre:** El comensal puede revisar su consumo acumulado y solicitar la cuenta desde la aplicación web.
- **Panel de administración completo:** Dashboard con métricas, gestión de catálogo, empleados, zonas de restaurante y configuración multi-tenant.

---

## Stack Tecnológico

### Backend

- **Java 21 LTS & Spring Boot 3.5.x:** Núcleo de la API REST de alto rendimiento.
- **Virtual Threads (Project Loom):** Habilitados de forma nativa (`spring.threads.virtual.enabled=true`) para optimizar el rendimiento y la concurrencia I/O sin saturar hilos del sistema operativo.
- **PostgreSQL 16 & Hibernate 6:** Base de datos relacional con aislamiento multi-tenant nativo mediante anotación `@TenantId` y políticas de seguridad a nivel de fila (**PostgreSQL RLS**).
- **Flyway:** Versionado y migración incremental de esquemas de base de datos (`V1__init.sql`, `V2__tenant.sql`, etc.).
- **Redis / Valkey 8:** Caché distribuida (`@Cacheable` / `@CacheEvict`) y message broker mediante **Redis Pub/Sub** para el escalado horizontal de eventos en tiempo real.
- **Server-Sent Events (SSE):** Canal de comunicación reactivo unidireccional servidor-cliente con pings periódicos de heartbeat (15s) y reconexión resiliente.
- **Spring Security & JJWT 0.12.x:** Autenticación stateless basada en tokens JWT con rotación de Refresh Tokens, control de expiración y blacklist en Redis.
- **Springdoc OpenAPI 3.0 / Swagger UI:** Documentación interactiva de endpoints (`/swagger-ui/index.html`).
- **Lombok & Jakarta Validation:** Código limpio, mantenible y validación declarativa de datos de entrada.

### Frontend

- **React 19 & TypeScript:** Single Page Application (SPA) tipada, moderna y modular.
- **Vite:** Empaquetador ultra-rápido para desarrollo ágil y compilación de producción optimizada.
- **CSS Modular:** Estilos encapsulados por componente, asegurando mantenibilidad y evitando colisiones de diseño.

---

## Arquitectura del Backend y Multi-Tenancy

EasyOrder implementa un modelo **multi-tenant híbrido con defensa en profundidad** (aislamiento en capa de aplicación + capa de base de datos):

```
                       ┌───────────────────────────────────────────────┐
                       │                Petición HTTP                  │
                       └───────────────────────┬───────────────────────┘
                                               │
                                               ▼
                       ┌───────────────────────────────────────────────┐
                       │   SessionCodeFilter / TenantFilter            │
                       │   (Extrae tenant desde JWT o X-Tenant-Slug)   │
                       └───────────────────────┬───────────────────────┘
                                               │
                                               ▼
                       ┌───────────────────────────────────────────────┐
                       │        TenantContext (ThreadLocal)            │
                       └───────────────────────┬───────────────────────┘
                                               │
                        ┌──────────────────────┴──────────────────────┐
                        ▼                                             ▼
       ┌─────────────────────────────────┐           ┌─────────────────────────────────┐
       │   Capa ORM (Hibernate 6)        │           │   TenantAwareTaskDecorator      │
       │   Anotación @TenantId           │           │   Propagación a hilos @Async    │
       │   Inyección automática          │           └─────────────────────────────────┘
       │   WHERE id_tenant = ?           │
       └────────────────┬────────────────┘
                        │
                        ▼
       ┌─────────────────────────────────┐
       │   PostgreSQL 16 Engine          │
       │   Row-Level Security (RLS)      │
       │   FORCE ROW LEVEL SECURITY      │
       └─────────────────────────────────┘
```

1. **Resolución de Tenant:**
   - Para personal del restaurante: `TenantFilter` intercepta la petición, verifica el token JWT y extrae el identificador o slug del tenant (`X-Tenant-Slug`), almacenándolo en `TenantContext` (`ThreadLocal`).
   - Para clientes en mesa: `SessionCodeFilter` valida el código de sesión efímero de la mesa y asocia el contexto del tenant correspondiente.
2. **Filtrado Automático en ORM (Hibernate 6):**
   - Las entidades de negocio incorporan la anotación `@TenantId` gestionada por `TenantIdentifierResolver`, inyectando de forma transparente la cláusula `WHERE id_tenant = ?` en cada consulta SQL generada.
3. **Aislamiento en Base de Datos (PostgreSQL RLS):**
   - Políticas de seguridad a nivel de fila (`ROW LEVEL SECURITY`) con `FORCE ROW LEVEL SECURITY` para garantizar que ninguna consulta manual o bypass de aplicación pueda exponer datos de otros restaurantes.
4. **Propagación Asíncrona Segura:**
   - La clase `TenantAwareTaskDecorator` asegura que el contexto `TenantContext` se transfiera fielmente a cualquier ejecución asíncrona (`@Async`, tareas en segundo plano o listeners de eventos).

---

## Comunicación en Tiempo Real y Eventos

El sistema utiliza un patrón de eventos desacoplado y transaccional:

1. **Persistencia Transaccional Segura:**
   - Cuando se modifica un estado (ej. nuevo pedido, cambio a "En Preparación", solicitud de cuenta), el servicio emite un evento Spring.
   - Los escuchadores utilizan `@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)`, garantizando que solo se envíen notificaciones cuando los datos se han consolidado definitivamente en PostgreSQL.
2. **Distribución Multi-Instancia (Redis Pub/Sub):**
   - El evento se publica en el topic Redis `easyorder:sse-events`. Esto permite escalar el backend en un cluster con múltiples nodos manteniendo la sincronización entre todos ellos.
3. **Emisión SSE Filtrada por Tenant:**
   - `SseNotificationService` canaliza el evento únicamente a los clientes suscritos al canal correspondiente (`tenantId:kds`, `tenantId:pedidos`, `tenantId:mesas`).
   - Un mecanismo de heartbeat emite pings periódicos cada 15 segundos para mantener las conexiones HTTP/2 activas y detectar desconexiones.

| Evento                     | Receptores Inmediatos                                 |
| -------------------------- | ----------------------------------------------------- |
| Apertura / Cierre de mesa  | Personal de sala, cliente asociado a la mesa          |
| Nuevo pedido creado        | Personal de sala, monitores de cocina (KDS)           |
| Cambio de estado de plato  | Personal de sala, monitores de cocina (KDS)           |
| Cliente solicita la cuenta | Personal de sala (alerta visual en el panel de mesas) |

---

## Seguridad y Control de Acceso

- **Arquitectura Stateless:** Spring Security configurado con `SessionCreationPolicy.STATELESS`.
- **Cadena de Filtros Personalizada:**
  ```
  SessionCodeFilter ──► TenantFilter ──► JwtAuthFilter ──► SecurityFilterChain
  ```
- **Tokens Criptográficos:** Tokens de acceso JWT firmados con HMAC-SHA256 y tokens de refresco con rotación y revocación.
- **Protección de Contraseñas:** Algoritmo BCrypt con salt criptográfico.
- **Acceso Anónimo Efímero para Clientes:** Los comensales operan mediante códigos de sesión temporales con caducidad vinculados a su mesa, sin necesidad de registrar datos personales.
- **Alineación CORS y Headers Seguros:** Control granular de orígenes permitidos mediante variable de entorno `APP_CORS_ALLOWED_ORIGINS`.

---

## Roles y Perfiles de Usuario

| Rol          | Descripción                     | Nivel de Acceso                                                      |
| ------------ | ------------------------------- | -------------------------------------------------------------------- |
| **ADMIN**    | Responsable del establecimiento | Panel de administración, vistas de staff y pantalla de cocina (KDS). |
| **PERSONAL** | Equipo de sala y camareros      | Panel de mesas, gestión de pedidos y creación de comandas manuales.  |
| **KDS**      | Equipo de cocina                | Pantalla interactiva de comandas en tiempo real por zonas.           |
| **Cliente**  | Comensal                        | Menú digital interactivo (acceso anónimo validado por sesión QR).    |

---

## Interfaces de Usuario

### Menú Digital (Vista del Cliente)
- Navegación fluida por categorías con buscador instantáneo de platos.
- Fichas de producto con precio, descripción y distintivos de alérgenos.
- Selector de modificadores y notas personalizadas (ej. "sin cebolla").
- Carrito en vivo, estado de pedidos y vista de consumo acumulado.

### Panel de Staff (Mesas, Pedidos y TPV Manual)
- Mapa visual de mesas con código de colores por estado (Libre, Ocupada, Esperando cuenta).
- Tablero tipo Kanban para pedidos clasificados por estado de preparación.
- Creación rápida de comandas manuales enviadas directamente a cocina.

### KDS (Pantalla de Cocina)
- Interfaz táctil optimizada para monitores horizontales y tablets.
- Tarjetas de preparación con cantidades, modificadores y notas destacadas.
- Alertas de urgencia dinámicas que cambian de color según el tiempo de espera acumulado.

### Panel de Administración
- Dashboard con métricas clave del restaurante.
- Gestor avanzado de catálogo (productos, categorías, modificadores, alérgenos).
- Gestión de empleados, asignación de roles y control de mesas/zonas.

---

## Calidad y Suite de Pruebas

El backend cuenta con una completa suite de **153 pruebas automatizadas (100% aprobadas)** implementadas con JUnit 5, Mockito, AssertJ y Spring Security Test:

- **Pruebas Unitarias de Servicios:** Validación exhaustiva de reglas de negocio, transaccionalidad, cálculo de suplementos y control de excepciones para todos los módulos (Auth, Mesas, Pedidos, Productos, Zonas, Usuarios, SSE, Redis).
- **Pruebas de Controladores (`@WebMvcTest`):** Verificación de contratos REST, códigos de respuesta HTTP, validaciones de DTOs (`@Valid`) y serialización JSON.
- **Pruebas de Multi-Tenancy y Seguridad:** Validación de `TenantFilter`, aislamiento en `TenantContext` y propagación de contexto en hilos asíncronos con `TenantAwareTaskDecoratorTest`.
- **Prueba de Carga de Contexto:** Comprobación del ciclo de vida del contenedor Spring Boot (`BackendApplicationTests`).

```bash
# Ejecución de la suite completa de pruebas del backend
cd backend
mvn test
```

---

## Despliegue y Puesta en Marcha

Toda la infraestructura está dockerizada para garantizar portabilidad y despliegues reproducibles.

### Prerrequisitos
- Docker y Docker Compose
- Java 21 LTS y Maven 3.9+ (opcional para ejecución en local sin Docker)
- Node.js 20+ (opcional para frontend en local)

### Estructura de Servicios Docker

| Servicio               | Imagen / Base           | Puerto | Propósito                                 |
| ---------------------- | ----------------------- | ------ | ----------------------------------------- |
| `easyorder-backend`    | `eclipse-temurin:21`    | 8080   | API REST Spring Boot con Virtual Threads  |
| `easyorder-frontend`   | Node.js / Vite SPA      | 5173   | Aplicación web React 19 + TypeScript      |
| `easyorder-db`         | `postgres:16-alpine`    | 5432   | Base de datos relacional con soporte RLS  |
| `easyorder-cache`      | `valkey/valkey:8.0`     | 6379   | Caché de alto rendimiento y Redis Pub/Sub |

### Iniciar el Proyecto

1. **Configurar las variables de entorno:**
   Copia el archivo `.env.example` (o revisa `.env`) en la raíz del proyecto y ajusta las credenciales requeridas (`JWT_SECRET`, contraseñas de BD, etc.).

2. **Levantar todos los servicios con Docker Compose:**
   ```bash
   docker compose up --build -d
   ```

3. **Acceder a las interfaces:**
   - **Frontend:** `http://localhost:5173`
   - **Backend API:** `http://localhost:8080`
   - **Swagger / OpenAPI:** `http://localhost:8080/swagger-ui/index.html`

---

## Licencia

Este proyecto fue desarrollado como Trabajo de Fin de Grado (TFG). Todos los derechos reservados.
