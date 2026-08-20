# AcaciosWork - Android App (La Movilidad)

## Descripción
Este es el cliente móvil nativo del ecosistema **AcaciosWork**, desarrollado en **Kotlin**. Su propósito es brindar portabilidad total a los operarios y dueños de tienda, permitiendo el control de inventario y la consulta de ventas directamente desde un smartphone o tablet.

La aplicación está diseñada bajo el patrón de arquitectura **MVVM (Model-View-ViewModel)** para garantizar un código limpio, testeable y mantenible.

### Funcionalidades Implementadas (Paridad 100% con Web):
- **Inventario Completo (`ui/inventario/`)**: LazyColumn con barra de búsqueda, catálogo de productos con columnas detalladas (incluyendo vencimiento y lote) y diálogo emergente de confirmación de eliminación con validaciones.
- **Punto de Venta POS Móvil (`ui/vender/`)**: Flujo completo de ventas, carrito de compras responsivo, buscador de productos con dropdown de sugerencias y selector de cliente.
- **Directorio de Proveedores y Clientes (`ui/proveedores/`, `ui/clientes/`)**: Formularios de creación y filtros de búsqueda en tiempo real.
- **Hub de Reportes (`ui/reportes/`)**: Coordinador de navegación que integra acceso a Reportes PDF, Preguntas Inteligentes y Gráficos Estadísticos con botones de regreso internos.
- **Alertas de Stock y Vencimiento (`ui/alertas/`)**: Filtros rápidos para productos con stock crítico, bajo o fechas de vencimiento próximas.
- **Preguntas IA Screen (`ui/preguntas_ia/`)**: 9 tarjetas en grid de 2 columnas para consultas de inteligencia de negocio con DatePicker nativo Material3 para filtrado de fechas.
- **Historial de Ventas (`ui/historial/`)**: Lista ordenada descendentemente por fecha con filas expandibles para detalles de venta y subtotales por producto.
- **Gestión de Usuarios (`ui/usuarios/`)**: CRUD completo integrado con la base de datos (con badges para roles y estados) y diálogos de creación/borrado real.
- **Configuración Completa (`ui/configuracion/`)**: 3 sub-pantallas (General, Hardware, Ticket) y botón de cierre de sesión.

## Tecnologías
- **Kotlin 2.x**: Lenguaje moderno y seguro.
- **Jetpack Compose**: UI declarativa nativa para interfaces fluidas y animadas.
- **Android SDK (Min SDK 26)**: Para compatibilidad con dispositivos Android 8.0+.
- **Retrofit 2**: Consumo eficiente de la API REST del backend.
- **Material Design 3**: Uso de directrices modernas de Google (DatePicker, chips, grids, etc.).
- **Material Icons Extended**: Biblioteca completa de iconos vectoriales integrados.

## Estructura de Carpetas (Modular)
- `ui/`: Subcarpetas por módulo (`welcome`, `inventario`, `vender`, `proveedores`, `clientes`, `reportes`, `alertas`, `preguntas_ia`, `historial`, `usuarios`, `configuracion`, `theme`).
- `network/`: Cliente Retrofit configurado para autenticación mediante token JWT.
- `model/` / `viewmodel/`: Lógica de negocio y persistencia de estados de interfaz.

---

# Estado del Proyecto (Avance)

### ✅ Finalizado y Estabilizado
- **Reescritura en Jetpack Compose**: Reconstrucción de las 10 pantallas principales desde cero con paridad total de funcionalidades frente a la versión web.
- **Navegación Modularizada**: Estructura de código limpia y archivos por debajo del límite estricto de **300 líneas de código**.
- **Exportación en Gráficos**: Botones "Generar PDF" implementados en ambos gráficos para compartir resúmenes textuales de ventas y ganancias usando `ReportSharing.kt`.
- **Botones PDF Homogéneos**: Todos los botones de exportación de las 10 tarjetas de reportes PDF unificados con el color naranja (`AccentOrange`).
- **Confirmación de Eliminación**: Diálogo emergente integrado en el inventario con feedback no intrusivo mediante toasts.

### 🔄 En Desarrollo / Estabilización
- **Pantallas Restantes**: Asegurar la paridad final de `GraficosTab` y `WelcomeTab` de la app móvil.
- **Caché y Sincronización Offline**: Implementación inicial de persistencia local.

---

# Próximos Pasos (Hoja de Ruta Móvil)

1.  **Notificación y Persistencia de Alertas**: Módulo de notificaciones push de stock crítico y vencimiento en tiempo real.
2.  **Cierre de Caja Contable**: Lógica y formularios en la app móvil para balance de facturación diario.
3.  **Compilación del APK Final**: Exportación automática de `acacioswork.apk` estable.

---

# Guía de Ejecución
1. Clona el repositorio y ábrelo en la última versión de **Android Studio**.
2. Configura la `BASE_URL` en el archivo de constantes de red para que apunte a la IP de tu servidor backend.
3. Compila y ejecuta en un emulador o dispositivo físico conectado.

---

# Estándares de Código
- Uso estricto de **Kdoc** para documentar funciones: `/** Descripción. @author RADJ */`.
- Seguir las guías de estilo de Kotlin (Google Android Style Guide).
- Toda petición de red debe manejarse dentro de un bloque `try-catch` en el ViewModel.
