
---

# EasyOrder — Sistema de Gestión de Pedidos para Restaurantes

> Plataforma **multi-tenant** de digitalización integral para restaurantes: desde el menú digital que escanea el cliente hasta la pantalla de cocina que prepara el plato.

---

## Índice

1. [Descripción General](https://www.google.com/search?q=%23descripci%C3%B3n-general)
2. [Características Principales](https://www.google.com/search?q=%23caracter%C3%ADsticas-principales)
3. [Stack Tecnológico](https://www.google.com/search?q=%23stack-tecnol%C3%B3gico)
4. [Roles y Perfiles de Usuario](https://www.google.com/search?q=%23roles-y-perfiles-de-usuario)
5. [Interfaces de Usuario](https://www.google.com/search?q=%23interfaces-de-usuario)
6. [Actualización en Tiempo Real](https://www.google.com/search?q=%23actualizaci%C3%B3n-en-tiempo-real)
7. [Multi-Tenancy y Arquitectura](https://www.google.com/search?q=%23multi-tenancy-y-arquitectura)
8. [Seguridad y Despliegue](https://www.google.com/search?q=%23seguridad-y-despliegue)

---

## Descripción General

**EasyOrder** es una aplicación web full-stack diseñada para digitalizar y agilizar el proceso de pedidos en restaurantes. Su objetivo principal es eliminar la dependencia del papel y reducir los malentendidos entre la sala y la cocina, ofreciendo a cada perfil de usuario (cliente, camarero, cocinero y administrador) una herramienta adaptada a sus necesidades exactas.

La plataforma destaca por su arquitectura **multi-tenant**: una única instancia del sistema es capaz de dar servicio a múltiples restaurantes de forma completamente aislada, accediendo cada establecimiento a través de su propio identificador único.

---

## Características Principales

- **Menú digital accesible por QR:** El cliente escanea el código de su mesa y accede al menú desde su móvil sin necesidad de instalar ninguna aplicación.
- **Pedidos en tiempo real:** Las comandas llegan instantáneamente a la cocina y a la sala mediante eventos enviados desde el servidor, sin tener que recargar la página.
- **Kitchen Display System (KDS):** Pantalla interactiva para la cocina organizada por zonas de trabajo, con indicadores visuales de tiempo de espera.
- **Gestión de mesas:** Control total para abrir o cerrar sesiones, monitorizar el estado de cada mesa y generar accesos QR dinámicos.
- **Comandas manuales:** Interfaz rápida para que el personal registre pedidos directamente si el cliente prefiere no usar su dispositivo.
- **Modificadores de producto:** Opciones configurables por artículo (puntos de carne, extras, tamaños) con la posibilidad de añadir suplementos de precio.
- **Control de alérgenos:** Información detallada adaptada a la normativa europea, diferenciando claramente entre presencia directa y posibles trazas.
- **Gestión autónoma de la cuenta:** El comensal puede consultar su consumo acumulado y solicitar la cuenta directamente desde la interfaz de su mesa.
- **Panel de administración integral:** Herramientas completas para la gestión del catálogo, el personal y la configuración del local.

---

## Stack Tecnológico

El proyecto está construido utilizando tecnologías modernas orientadas a la escalabilidad, el rendimiento y la mantenibilidad.

### Backend

- **Java & Spring Boot:** Base del servidor y construcción de la API REST.
- **Spring Security:** Gestión de autenticación y autorización basada en roles.
- **MariaDB & Hibernate:** Base de datos relacional y mapeo objeto-relacional con soporte para separación de datos multi-tenant.
- **Redis:** Sistema de caché de alto rendimiento.
- **Server-Sent Events (SSE):** Protocolo para la comunicación unidireccional y en tiempo real del servidor hacia los clientes.

### Frontend

- **React & TypeScript:** Construcción de una Single Page Application (SPA) robusta y fuertemente tipada.
- **Vite:** Herramienta de empaquetado para una experiencia de desarrollo rápida y compilación optimizada.
- **CSS Modular:** Estilos independientes por componente para evitar conflictos visuales y mejorar la escalabilidad del diseño.

---

## Roles y Perfiles de Usuario

El sistema adapta su interfaz y permisos dependiendo de quién inicie sesión:

| Rol          | Descripción                     | Nivel de Acceso                                                      |
| ------------ | ------------------------------- | -------------------------------------------------------------------- |
| **ADMIN**    | Responsable del establecimiento | Panel de administración, vistas de staff y pantalla de cocina (KDS). |
| **PERSONAL** | Equipo de sala y camareros      | Panel de mesas, gestión de pedidos y creación de comandas.           |
| **KDS**      | Equipo de cocina                | Pantalla exclusiva de comandas en tiempo real.                       |
| **Cliente**  | Comensal                        | Menú digital interactivo (acceso anónimo validado por sesión QR).    |

---

## Interfaces de Usuario

### Menú Digital (Vista del Cliente)

- Navegación fluida por categorías con buscador instantáneo.
- Fichas de producto visuales con precio, descripción y etiquetas de alérgenos.
- Sistema de selección de modificadores y campo para notas personalizadas (ej. "sin cebolla").
- Carrito en vivo y vista de consumo acumulado.

### Panel de Staff — Mesas, Pedidos y Comandas Manuales

- Mapa visual de mesas con código de colores según su estado (Libre, Ocupada, Esperando cuenta).
- Tablero tipo Kanban para los pedidos, categorizados por su estado de preparación.
- Sistema de un solo toque para enviar comandas manuales directamente a la cocina.

### KDS — Pantalla de Cocina

- Interfaz optimizada para tablets o monitores táctiles horizontales.
- Tarjetas de preparación que incluyen cantidad, modificadores y notas especiales.
- Alertas visuales de urgencia que cambian de color según el tiempo que el pedido lleve en espera.

### Panel de Administración

- Dashboard con métricas rápidas sobre el estado del restaurante.
- Gestor avanzado del catálogo de productos, categorías y modificadores.
- Administración de empleados, roles y control de acceso.

---

## Actualización en Tiempo Real

Para garantizar un servicio fluido, EasyOrder utiliza **Server-Sent Events (SSE)**. Esto permite que cualquier cambio de estado se propague al instante a todos los dispositivos involucrados, eliminando la necesidad de actualizar la pantalla manualmente.

| Acción                     | Receptores Inmediatos                                 |
| -------------------------- | ----------------------------------------------------- |
| Apertura / Cierre de mesa  | Personal de sala, cliente asociado a la mesa          |
| Nuevo pedido enviado       | Personal de sala, monitores de cocina (KDS)           |
| Plato marcado como "Listo" | Personal de sala                                      |
| Cliente pide la cuenta     | Personal de sala (alerta visual en el panel de mesas) |

---

## Multi-Tenancy y Arquitectura

El sistema está diseñado bajo una arquitectura cliente-servidor clásica, potenciada por una capa de comunicación en tiempo real.

La característica más destacada es su capacidad **multi-tenant**. Esto significa que una sola instalación del programa puede gestionar decenas de restaurantes simultáneamente sin que los datos se mezclen.

- El aislamiento se garantiza desde el núcleo de la base de datos hasta la interfaz.
- Cada restaurante cuenta con un identificador único que personaliza la URL y la experiencia de usuario.
- El backend resuelve dinámicamente a qué restaurante pertenece cada petición, garantizando la privacidad absoluta de los datos, empleados y facturación de cada cliente.

---

## Seguridad y Despliegue

La plataforma sigue los estándares actuales de seguridad y operaciones web:

- **Autenticación sin estado:** Se utilizan JSON Web Tokens (JWT) con un sistema de tokens de refresco, asegurando accesos rápidos y seguros sin sobrecargar la memoria del servidor.
- **Aislamiento de clientes:** Los comensales no necesitan crear cuentas de usuario; acceden mediante tokens de sesión efímeros vinculados temporalmente a su mesa, protegiendo su privacidad.
- **Contenedores:** Toda la infraestructura (base de datos, caché, servidor y cliente) está dockerizada. Esto garantiza que el proyecto sea agnóstico al entorno, facilitando despliegues predecibles, seguros y escalables en cualquier proveedor de la nube.
- **Gestión de secretos:** La configuración sensible, como credenciales de base de datos o firmas criptográficas, se inyecta mediante variables de entorno seguras en el momento del despliegue, sin exponerse nunca en el código fuente.

---

## Licencia

Este proyecto fue desarrollado como Trabajo de Fin de Grado. Todos los derechos reservados.
