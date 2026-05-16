# AcaciosWork - Desktop (La Interfaz Administrativa)

## Descripción
Este es el cliente de escritorio (Desktop App) del ecosistema **AcaciosWork**, desarrollado en **Java 25** utilizando **Swing**. Su objetivo principal es proporcionar una interfaz robusta, rápida y moderna para la administración de la tienda y la operación del Punto de Venta (POS).

A diferencia de la interfaz web, el cliente desktop está optimizado para flujos de trabajo intensivos, ofreciendo una experiencia de usuario premium gracias al uso de temas modernos y una navegación fluida entre módulos administrativos.

### Funcionalidades Clave:
- **Panel Administrativo**: Centro de control con navegación lateral para gestionar todos los recursos del sistema.
- **Punto de Venta (POS)**: Interfaz dedicada para el registro rápido de ventas y búsqueda de productos.
- **Gestión de Entidades**: Formularios avanzados para el control de Usuarios, Clientes, Proveedores e Inventario.
- **Consumo de API**: Comunicación segura con el backend mediante JWT y serialización JSON.
- **UI Moderna**: Implementación de **FlatLaf** para una estética profesional (Light/Dark mode ready).

## Tecnologías
- **Java 25** (Compilación y ejecución de última generación).
- **Swing** (Toolkit gráfico estándar para aplicaciones de escritorio).
- **FlatLaf 3.4.1** (Look and Feel moderno y limpio).
- **Jackson / JSON** (Procesamiento de datos provenientes de la API).
- **Maven** (Gestión de dependencias y empaquetado).

## Estructura de Paquetes
- `interfaz_usuario`: Contiene todos los paneles (JPanels) y ventanas (JFrame) de la aplicación.
- `model`: Modelos de datos locales para el mapeo de respuestas de la API.
- `util`: Utilidades de conexión, gestión de tokens JWT y herramientas de diseño.
- `App.java`: Clase principal que inicia el ciclo de vida de la aplicación.

---

# Estado del Proyecto (Avance)

### ✅ Finalizado y Estabilizado
- **Sistema de Login**: Autenticación funcional contra el backend y persistencia temporal del token.
- **Dashboard Principal**: Estructura de `Administrador.java` con barra lateral y cambio dinámico de paneles.
- **Módulos CRUD**: Interfaces completas para Gestión de Usuarios, Clientes, Proveedores y Categorías.
- **Inventario**: Visualización y edición de productos con integración total a la API.
- **Estética**: Aplicación global del tema FlatLaf para una apariencia uniforme.

### 🔄 En Desarrollo / Estabilización
- **Punto de Venta (POS)**: Refinamiento de la lógica de carrito y cálculo de cambios en tiempo real.
- **Gestión de Devoluciones**: Implementación inicial de la lógica para revertir transacciones.
- **Alertas Visuales**: Notificaciones emergentes para stock bajo y errores de red.

---

# Sugerencias y Próximos Pasos (Lo que falta)

1.  **Módulo de Estadísticas**: Implementar gráficos (JFreeChart) en el panel principal para visualizar ventas y ganancias.
2.  **Impresión de Tickets**: Desarrollar la lógica para impresión térmica (ESC/POS) de comprobantes de venta.
3.  **Atajos de Teclado**: Añadir hotkeys (ej. F5 para cobrar, F1 para buscar) para agilizar la operación en el POS.
4.  **Modo Offline Parcial**: Implementar un sistema de caché para permitir consultas básicas si el servidor no está disponible.
5.  **Validaciones de Entrada**: Mejorar la validación visual en tiempo real de los formularios (campos obligatorios, formatos de correo, etc.).

---

# Documentación de Desarrollo

## REGLAS TÉCNICAS OBLIGATORIAS
*   **API-ONLY**: Queda PROHIBIDO incluir drivers de MySQL o realizar conexiones directas a la base de datos desde este módulo. Todo debe pasar por la `ApiClient`.
*   **ENTORNO**: Desarrollo mandatorio en **Java 25**.
*   **VERSIONAMIENTO**: Mantener sincronizados los modelos locales con las entidades del Backend para evitar errores de deserialización.

---

## ESTÁNDAR DE DOCUMENTACIÓN Y COMENTARIOS
Todo bloque de código generado debe incluir la firma del autor y una descripción funcional breve:

*  **Lenguaje Tecnología Formato Requerido** |

* **Java** | `/** Descripción breve. @author RADJ */` |

---

## STACK TECNOLÓGICO DETALLADO
*   **Lenguaje**: Java 25.
*   **UI Framework**: Swing + FlatLaf 3.4.1.
*   **Networking**: HTTP Client (Java Native) + Jackson 2.15.2.
*   **Build Tool**: Maven 3.x.
