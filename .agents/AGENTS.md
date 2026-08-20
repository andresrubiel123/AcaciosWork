## graphify

This project has a knowledge graph at graphify-out/ with god nodes, community structure, and cross-file relationships.

When the user types `/graphify`, use the installed graphify skill or instructions before doing anything else.

Rules:
- For codebase questions, first run `graphify query "<question>"` when graphify-out/graph.json exists. Use `graphify path "<A>" "<B>"` for relationships and `graphify explain "<concept>"` for focused concepts. These return a scoped subgraph, usually much smaller than GRAPH_REPORT.md or raw grep output.
- Dirty graphify-out/ files are expected after hooks or incremental updates; dirty graph files are not a reason to skip graphify. Only skip graphify if the task is about stale or incorrect graph output, or the user explicitly says not to use it.
- If graphify-out/wiki/index.md exists, use it for broad navigation instead of raw source browsing.
- Read graphify-out/GRAPH_REPORT.md only for broad architecture review or when query/path/explain do not surface enough context.
- After modifying code, run `graphify update .` to keep the graph current (AST-only, no API cost).

---

## acacioswork — Reglas Globales del Proyecto

### Regla Universal: Límite de 300 Líneas
Ningún archivo fuente (Java, Kotlin, nuevo o modificado) debe superar **300 líneas de código**. Si un archivo amenaza este límite, se debe modularizar creando helpers, componentes o sub-pantallas adicionales.

### Paridad de 12 Secciones (Web → Desktop → Android)
El orden de navegación es idéntico en las tres plataformas:

| # | Sección | ID/Ruta |
|---|---------|---------|
| 1 | Inicio | `welcome` |
| 2 | Inventario | `inventario` |
| 3 | Vender | `vender` |
| 4 | Proveedores | `proveedores` |
| 5 | Clientes | `clientes` |
| 6 | Reportes | `reportes` |
| 7 | Alertas Stock | `alertas` |
| 8 | Preguntas Inteligentes | `preguntas-inteligentes` |
| 9 | Gráficos | `graficos` |
| 10 | Historial | `historial` |
| 11 | Usuarios | `usuarios` |
| 12 | Configuración | `configuracion` |

### Reglas Android (Jetpack Compose)
- Cada pantalla vive en su propio paquete `ui/<nombre>/`. Separar pantalla principal, componentes y helpers en archivos distintos.
- Los iconos deben importarse de `material-icons-extended` (ya añadido al `build.gradle`). Usar `Icons.AutoMirrored.Filled.*` cuando el IDE lo sugiera.
- El `DatePicker` nativo Material3 debe usarse para selección de fechas en lugar de campos de texto libres.
- Los colores del tema deben venir siempre de `ui/theme/` (`Primary`, `BgDark`, `BgCard`, `TextLight`, `TextMuted`, `AccentGreen`, `AccentOrange`, `AlertRed`).
- Nunca usar `Divider` — usar `HorizontalDivider` (API no deprecada).

### Reglas Desktop (Java Swing)
- Separación estricta por dominio: `InventoryReportBuilder` y `FinanceReportBuilder` coordinados por `ReportExporter`.
- Los gráficos vectoriales van en `GraficosTab`; la lista de historial en `HistorialTab`.
- El footer del sidebar siempre muestra "Cerrar Sesión" con texto de copyright y contacto.

### Endpoints REST Clave
- `GET /api/productos` — Catálogo de productos con stock y vencimiento
- `GET /api/ventas` — Historial completo de ventas con detalles
- `GET /api/clientes` — Directorio de clientes
- `GET /api/proveedores` — Directorio de proveedores
- `POST /api/ventas` — Registrar nueva venta (payload: `{idUsuario, idCliente, detalles[]}`)
- `POST /api/movimientos-inventario` — Entradas/salidas de stock con lotes y vencimiento

---

## Historial de Actualizaciones por Sesión

### Sesión A (Mensajes 1–50): Refactorización Modular y Paridad Multiplataforma
- **Mod. 1–10**: Refactorización monolítica → pestañas autónomas en Desktop (`WelcomeTab`, `InventarioTab`, `ProveedoresTab`, `ClientesTab`, `UsuariosTab`, `AlertasTab`). Motor analítico extraído a `IntelligenceEngine.java`. Clases legadas eliminadas (`GestionInventario`, `GestionProveedores`, `GestionClientes`, `GestionUsuarios`, `GestionCategorias`, `GestionDevoluciones`).
- **Mod. 11–20**: Creadas `GraficosTab` e `HistorialTab` en Desktop. Agregadas 10 tarjetas de reportes PDF con vencimiento (5 y 15 días). Alineación web completada en Desktop.
- **Mod. 21–30**: Columnas `Vencimiento` y `Movimientos` en `InventarioTab.java`. Creados `MovimientosPanel.java` y `MovimientoDialog.java` para el flujo de entradas/salidas de stock.
- **Mod. 31–40**: Paridad Android iniciada — 12 vistas del menú lateral alineadas. Creadas `WelcomeTab.kt`, `AlertasTab.kt`, `PreguntasIaScreen.kt`, `GraficosTab.kt`, `HistorialTab.kt`, `UsuariosTab.kt`. Añadida columna Vencimiento y botones Entrada/Salida en `ProductoCard`.
- **Mod. 41–50**: Actualización de documentación maestra (`project-context.md`, `arquitectura_acacioswork.md`). Creación de `Bitacora.md` y `Bitacora.docx` (Normas APA 7 / SENA). Corrección masiva de 21 warnings del IDE en el cliente Android.

---

### Sesión B (Checkpoint 7): Paridad Android — Reescritura Completa de Pantallas

**Enfoque de la sesión**: Eliminar las implementaciones Android anteriores pantalla por pantalla y reconstruirlas desde cero con paridad al 100% de la versión Web. NO compilar APK hasta completar todos los módulos.

**Dependencia añadida al `build.gradle`:**
```
implementation 'androidx.compose.material:material-icons-extended'
```
Esto habilita todos los iconos extendidos de Material (`TrendingUp`, `Lightbulb`, `History`, etc.) en todo el proyecto.

**Pantallas reescritas (en orden):**

#### 1. InventarioTab (`ui/inventario/`)
- `InventarioTab.kt` — Pantalla principal con LazyColumn, barra de búsqueda y botón "+ Nuevo Producto"
- Sin tarjetas de resumen en la vista principal (paridad web: solo tabla + búsqueda)
- Columnas: Producto, Código, Categoría, Stock, Precio Compra, Precio Venta, Vencimiento, Movimientos

#### 2. VenderTab (`ui/vender/`)
- POS completo: buscador de producto, dropdown de resultados, carrito de compras, resumen financiero
- Selector de cliente con `DropdownMenu`
- Botón "Registrar Venta" con validación y feedback en tiempo real
- Paridad web con `VentasComponents.kt` para el carrito y `DashboardProductCard.kt`

#### 3. ProveedoresTab (`ui/proveedores/`)
- Lista de proveedores con búsqueda en tiempo real
- Tarjetas con: nombre, NIT, teléfono, email, ciudad
- Botón "+ Nuevo Proveedor" con dialog de creación
- Paridad web con la sección `proveedores.html`

#### 4. ClientesTab (`ui/clientes/`)
- Lista de clientes activos con búsqueda
- Tarjetas con: nombre, documento, teléfono, email, estado
- Botón "+ Nuevo Cliente" con dialog de creación
- Paridad web con la sección `clientes.html`

#### 5. ReportesTab (`ui/reportes/`)
- **`ReportesTab.kt`** — Reorganizado como **Hub/Coordinador de Navegación** que unifica 3 sub-pantallas
- **Sub-pantalla 1: Hub principal** con 3 botones interactivos de acceso rápido
- **Sub-pantalla 2: Reportes PDF** (las 10 tarjetas originales con soporte de Share Intent y botón de regreso)
- **Sub-pantalla 3: Preguntas Inteligentes** (redirecciona a `PreguntasIaScreen` con botón de regreso)
- **Sub-pantalla 4: Gráficos Estadísticos** (redirecciona a `GraficosTab` con botón de regreso)

#### 6. AlertasTab (`ui/alertas/`)
- Lista de productos con stock crítico (≤ stock mínimo)
- Chips de filtro: Crítico / Bajo / Vencimiento próximo
- Paridad web con la sección `alertas.html`

#### 7. PreguntasIaScreen (`ui/preguntas_ia/`)
- **`PreguntasIaScreen.kt`** — Pantalla principal con LazyColumn + LazyVerticalGrid 2 columnas, ahora con soporte para botón Atrás (`onBack`)
- **`IQCard.kt`** — Tarjeta IQ: badge de categoría, emoji, pregunta, respuesta con borde naranja pulsante
- **`IQFilterCard.kt`** — Panel de filtro de fechas con chips "Desde/Hasta" e indicador verde/gris
- **`IQDatePickerDialog.kt`** — DatePicker nativo Material3 con colores del tema oscuro
- Las 9 preguntas IQ en grid 2 columnas. La tarjeta "Próximos a Vencer" siempre activa, las demás requieren filtro de fechas
- `IntelligenceEngine.kt` e `IntelligenceEngineHelper.kt` sin cambios (lógica de negocio intacta)

#### 8. HistorialTab (`ui/historial/`)
- **`HistorialTab.kt`** — Pantalla principal con LazyColumn, buscador y header de columnas de tabla
- **`HistorialComponents.kt`** — `HistorialStatCard` + `HistorialVentaRow` (fila expandible)
- Ventas ordenadas descendentemente por fecha (más reciente primero)
- Filas expandibles que muestran detalle de productos con subtotales
- Badge indigo `📦 N productos` por venta (paridad web)
- Buscador filtra por cliente, ID y fecha

#### 9. UsuariosTab (`ui/usuarios/`)
- **`UsuariosTab.kt`** — Pantalla principal con LazyColumn, buscador, dialogs de creación/borrado y header
- **`UsuarioRow.kt`** — Fila individual con badges coloreados para rol (Admin/Auxiliar) y estado (Activo/Inactivo)
- Lógica de borrado real integrada con llamada DELETE al API

#### 10. ConfiguracionTab (`ui/configuracion/`)
- **`ConfiguracionScreen.kt`** — Navegación de 3 pestañas (General, Hardware, Ticket), botón Guardar en cabecera y cierre de sesión
- **`GeneralTabContent.kt`** — Campos de información general
- **`HardwareTabContent.kt`** — Ajustes avanzados de periféricos (Lector, Balanza, Impresora, Cajón, Datáfono)
- **`TicketTabContent.kt`** — Opciones de logotipo, márgenes y texto del ticket de venta
- **`ConfiguracionTheme.kt`** — Colores unificados de entrada de texto
- Integrado el botón "Cerrar Sesión" y el footer con copyright y correo de contacto de Rubiel Andrés Díaz

**Nota de compilación**: Compilación total completada con éxito. El APK se ha exportado a la raíz del proyecto.

---

## 🎯 Roadmap Actualizado

### ✅ Completado
- Paridad de 12 secciones en Web, Desktop y Android
- Refactorización modular estricta (< 300 líneas por archivo)
- Ciclo de vida de inventario: lotes, vencimiento, movimientos (entradas/salidas)
- Reescritura completa de 10 pantallas Android con paridad Web (Inventario, Vender, Proveedores, Clientes, Reportes, Alertas, Preguntas Inteligentes, Historial, Usuarios y Configuración)
- **Reorganización del Módulo de Reportes (Android + Web)**: Consolidación de Reportes PDF, Preguntas IA y Gráficos dentro de un Hub de Reportes con botones de regreso e interactivos, reduciendo y simplificando el menú/navbar principal en ambas plataformas
- **Homogeneización del Botón PDF (Android)**: Todos los botones de exportación PDF de las 10 tarjetas de reportes ahora tienen el color naranja (`AccentOrange`).
- **Exportación en Análisis Gráficos (Android)**: Botones "Generar PDF" implementados en ambos gráficos para compartir resúmenes textuales interactivos de rentabilidad mensual y ventas por categorías usando `ReportSharing.kt`.
- **Confirmación de Eliminación de Productos (Android)**: Diálogo emergente ("¿En verdad deseas eliminar?") implementado en `InventarioTab.kt` con Toast no disruptivo que maneja las restricciones de llaves foráneas en base de datos.
- Dependencia `material-icons-extended` añadida al proyecto Android
- Corrección de 21 warnings de compilación Android
- **Corrección de Advertencias de IDE (Backend + Desktop)**: Limpieza integral de null type safety, imports no utilizados y dependencias inactivas en `JwtUtil.java`, `LoteService.java`, `ProductoService.java`, `VentaService.java`, `Administrador.java`, `AlertasTab.java`, `GraficosTab.java` y `HistorialTab.java`. Modularización del botón de navegación de Swing en `AcaciosToolbarButton.java` reduciendo el archivo `Administrador.java` por debajo del límite de 300 líneas.
- Bitácora APA 7 (`Bitacora.md` + `Bitacora.docx`)

### 🔲 Pendiente (Próximos Hitos)
- **Pantallas Android restantes**: Revisar y reescribir `GraficosTab` y `WelcomeTab` con paridad web
- **Compilación del APK final**: Generar `acacioswork.apk` una vez completadas todas las pantallas
- **Notificación y Persistencia de Alertas**: Módulo de notificaciones push de stock crítico y vencimiento en tiempo real
- **Cierre de Caja Contable**: API REST + UI para balance de facturación diario
- **Gráficos Interactivos en Android**: Representación vectorial de rentabilidad mensual en la app móvil

### 🔭 Visión del Proyecto
- **Ecosistema ERP/POS Cloud-Native Ready**: AcaciosWork 100% listo para producción, modular y estable para PYME
- **Estándar de Calidad Certificable**: Bajo prácticas de **ISO/IEC 25000**, **CMMI Nivel 2** y **PSP** para presentación académica y comercial
