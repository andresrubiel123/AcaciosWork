# ﻿GA5-220501095-AA1-EV08

**Maquetación de la Interfaz gráfica en XML - Android**

##

**Aprendiz Sena:**

Rubiel Andrés Díaz Jiménez.

Tecnólogo en Análisis y Desarrollo de Software, Centro de Gestión y Desarrollo Sostenible Surcolombiano, Servicio Nacional de Aprendizaje.

**Ficha: 3118313**

Instructor Transversal de emprendimiento: Herley Antonio Puentes Peñaloza

5 de agosto de 2026

# TABLA DE CONTENIDO

[**﻿GA5-220501095-AA1-EV08 1**](#_heading=h.v71ub3ktphn8)

[**TABLA DE CONTENIDO 2**](#_heading=)

[**INTRODUCCIÓN 3**](#_heading=)

[**OBJETIVOS 4**](#_heading=)

[**1. Diseño Visual — Identidad Gráfica de AcaciosWork 4**](#_heading=)

[**1.1 Paleta de Colores 4**](#_heading=)

[**1.2 Tipografía 5**](#_heading=)

[**1.3 Iconografía 5**](#_heading=)

[**1.4 Componentes UI Reutilizables 5**](#_heading=)

[**2. Layouts Android Utilizados 6**](#_heading=)

[**2.1 ConstraintLayout 6**](#_heading=)

[**2.2 LazyColumn (RecyclerView en Compose) 6**](#_heading=)

[**2.3 LinearLayout 6**](#_heading=)

[**2.4 FrameLayout / Box (Compose) 6**](#_heading=)

[**2.5 ScrollView / Column con verticalScroll 6**](#_heading=)

[**2.6 Panel de navegación — Navigation Drawer 7**](#_heading=)

[**2.7 LazyVerticalGrid 7**](#_heading=)

[**3. Maquetas — Prototipos de Alta Fidelidad 8**](#_heading=)

[**Pantalla 1 — Login (Iniciar Sesión) 8**](#_heading=)

[**Pantalla 2 — Dashboard / Ventana de Inicio 9**](#_heading=)

[**Pantalla 3 — Menú Principal (Navigation Drawer) 10**](#_heading=)

[**Pantalla 4 — Inventario de Productos 11**](#_heading=)

[**Pantalla 5 — Vender (Punto de Venta POS) 12**](#_heading=)

[**Pantalla 6 — Clientes 13**](#_heading=)

[**Pantalla 7 — Proveedores 14**](#_heading=)

[**Pantalla 8 — Módulo de Reportes 15**](#_heading=)

[**Pantalla 9 — Alertas de Inventario 16**](#_heading=)

[**Pantalla 10 — Historial de Ventas 17**](#_heading=)

[**Pantalla 11 — Usuarios del Sistema 18**](#_heading=)

[**Pantalla 12 — Configuración del Sistema 19**](#_heading=)

[**4. Responsividad 20**](#_heading=)

[**5. Relación XML ↔ Componentes por Pantalla 20**](#_heading=)

[**6. Herramientas Utilizadas 21**](#_heading=)

[**CONCLUSIONES 22**](#_heading=)

[**REFERENCIAS 23**](#_heading=)

# INTRODUCCIÓN

Este documento expone la propuesta de interfaz gráfica de la aplicación móvil **AcaciosWork**, una solución de gestión de inventario, ventas y análisis comercial desarrollada en el ecosistema Android. La maquetación se construyó aplicando los principios del sistema de diseño **Material Design 3 (MD3)**, garantizando usabilidad, accesibilidad y diseño responsivo para dispositivos de distintos tamaños de pantalla.

La evidencia documenta la composición visual de cada pantalla: tipografía, paleta de colores, formas, tamaños, iconografía y animaciones; así como la identificación de los layouts XML empleados en Android Studio para estructurar cada vista. El prototipo presentado corresponde a las capturas reales de la aplicación en ejecución, complementadas con la descripción técnica de los componentes XML que las sustentan.

# OBJETIVOS

**1.** Diseñar la interfaz gráfica de AcaciosWork aplicando principios de Material Design 3.

**2.** Identificar y documentar los componentes XML nativos de Android utilizados en cada pantalla.

**3.** Elaborar prototipos de alta fidelidad que reflejen el estado real de la aplicación móvil.

**4.** Garantizar una experiencia de usuario intuitiva, accesible y responsiva.

**5.** Evidenciar el conocimiento en layouts Android: ConstraintLayout, RecyclerView, Navigation Drawer, entre otros.

**6.** Demostrar la relación entre el diseño visual y su implementación técnica en Jetpack Compose / XML.

# 1. Diseño Visual — Identidad Gráfica de AcaciosWork

## 1.1 Paleta de Colores

| **Token de Color** | **Valor HEX** | **Uso en la App** |
| --- | --- | --- |
| BgDark | #0D1B2A | Fondo principal de todas las pantallas |
| BgCard | #1A2B3C | Fondo de tarjetas (CardView / Surface) |
| Primary (Verde) | #00C853 | Logo, títulos principales, botón + Nuevo |
| AccentOrange | #FF6F00 | Botones de acción primaria, íconos de módulo |
| AccentGreen | #4CAF50 | Indicadores de estado Activo, badges positivos |
| AlertRed | #D32F2F | Alertas críticas, botón Salida, textos de error |
| TextLight | #FFFFFF | Texto principal sobre fondo oscuro |
| TextMuted | #9E9E9E | Subtítulos y etiquetas secundarias |
| SurfaceVariant | #162636 | Drawer lateral, campos de texto |

## 1.2 Tipografía

La aplicación utiliza la fuente del sistema Android complementada con la familia **Roboto** (fuente predeterminada de Material Design):

| **Estilo** | **Tamaño** | **Peso** | **Uso** |
| --- | --- | --- | --- |
| Display Large | 32 sp | Bold | Logo AcaciosWork en Login |
| Headline Medium | 24 sp | Bold | Títulos de pantalla |
| Title Medium | 18 sp | SemiBold | Subtítulos de sección |
| Body Large | 16 sp | Regular | Contenido de tarjetas, formularios |
| Body Medium | 14 sp | Regular | Etiquetas, metadatos |
| Label Small | 12 sp | Medium | Badges, chips, etiquetas de estado |

## 1.3 Iconografía

Todos los íconos provienen de la biblioteca **Material Icons Extended**:

* **Navegación:** Icons.Filled.Home, Icons.Filled.ShoppingCart, Icons.Filled.People
* **Acciones:** Icons.Filled.Add, Icons.Filled.Edit, Icons.Filled.Delete, Icons.Filled.Search
* **Módulos:** Icons.Filled.Inventory, Icons.Filled.Warning, Icons.Filled.Settings
* **Alertas:** Icons.Filled.Warning, Icons.Filled.ErrorOutline

## 1.4 Componentes UI Reutilizables

| **Componente** | **Material 3 API** | **Uso** |
| --- | --- | --- |
| Botón principal | FilledButton | Iniciar Sesión, + Nuevo, Registro Venta |
| Botón contorno | OutlinedButton | Vaciar, acciones secundarias |
| Tarjeta | ElevatedCard | Productos, clientes, proveedores |
| Campo de texto | OutlinedTextField | Búsquedas, formularios |
| Chip de filtro | FilterChip | Alertas: Crítico, Bajo, Vencimiento |
| Barra superior | TopAppBar | Encabezado con menú hamburguesa y avatar |
| Menú lateral | ModalNavigationDrawer | Navegación de 10 secciones |
| Indicador estado | Badge / Surface coloreado | Admin / Activo / Inactivo |

# 2. Layouts Android Utilizados

## 2.1 ConstraintLayout

Layout flexible que posiciona widgets mediante restricciones relativas. Es el layout de referencia para pantallas principales en Android.

**Uso:** Login (posiciona la CardView central), Dashboard (tarjetas de métricas), pantallas con elementos flotantes.

Atributos clave:

<androidx.constraintlayout.widget.ConstraintLayout
 android:layout\_width="match\_parent"
 android:layout\_height="match\_parent"
 app:layout\_constraintTop\_toTopOf="parent"
 app:layout\_constraintBottom\_toBottomOf="parent" />

## 2.2 LazyColumn (RecyclerView en Compose)

Equivalente al RecyclerView en XML clásico. Renderiza de forma eficiente listas largas, cargando solo los ítems visibles en pantalla (virtualización).

**Uso:** Inventario, Clientes, Proveedores, Historial de Ventas, Usuarios, Alertas.

## 2.3 LinearLayout

Organiza vistas en una sola fila (horizontal) o columna (vertical). Ideal para agrupar botones o elementos de igual peso.

**Uso:** Botones Entrada/Salida en inventario, botones Vaciar/Registrar en ventas, Tabs General/Hardware/Ticket.

## 2.4 FrameLayout / Box (Compose)

Contenedor que apila vistas una sobre otra. Se usa como contenedor de fragmentos o para superponer elementos.

**Uso:** Contenedor del ModalNavigationDrawer, superposición del menú lateral sobre el dashboard.

## 2.5 ScrollView / Column con verticalScroll

Permite el desplazamiento vertical cuando el contenido supera la altura de pantalla.

**Uso:** Configuración del sistema, módulo de ventas con carrito extenso.

## 2.6 Panel de navegación — Navigation Drawer

Panel lateral que se desliza desde el borde izquierdo. Patrón de navegación principal de AcaciosWork.

| **#** | **Sección** | **Ícono Material** |
| --- | --- | --- |
| 1 | Inicio | Home |
| 2 | Inventario | ShoppingCart |
| 3 | Vender | AddCircle |
| 4 | Proveedores | Build |
| 5 | Clientes | Person |
| 6 | Reporters | List |
| 7 | Alertas Stock | Warning |
| 8 | Historial | Refresh |
| 9 | Usuarios | ManageAccounts |
| 10 | Configuración | Settings |
| — | Cerrar Sesión | ExitToApp (rojo) |

## 2.7 LazyVerticalGrid

Grid de 2 columnas para mostrar tarjetas de igual tamaño en cuadrícula.

**Uso:** Módulo de Preguntas Inteligentes — 9 tarjetas IQ en cuadrícula 2×N.

# 3. Maquetas — Prototipos de Alta Fidelidad

## Pantalla 1 — Login (Iniciar Sesión)

Punto de entrada al sistema. Solicita credenciales y valida el acceso al backend REST.

**Layout raíz:** ConstraintLayout (fondo BgDark completo)

| **Componente** | **Tipo** | **Descripción** |
| --- | --- | --- |
| Fondo | Box con color #0D1B2A | Fondo oscuro completo |
| Tarjeta central | Card con ElevatedCardDefaults | Contenedor del formulario |
| Logo texto | Text estilo Display / verde | AcaciosWork en color Primary |
| Subtítulo | Text Body Large / TextMuted | Acceso al sistema administrativo |
| Campo Usuario | OutlinedTextField | Ícono de persona |
| Campo Contraseña | OutlinedTextField con visualTransformation | Enmascaramiento de contraseña |
| Botón Ingresar | Button con gradiente naranja a rojo | Iniciar Sesión con animación |
| Footer | Text centrado pequeño | Copyright 2026 AcaciosWork |

![](data:image/jpeg;base64...)

## Pantalla 2 — Dashboard / Ventana de Inicio

Pantalla principal tras el login. Muestra métricas financieras del negocio y resumen de bodega Top 5.

**Layout raíz:** Scaffold con TopAppBar + ModalNavigationDrawer + LazyColumn

| **Componente** | **Tipo** | **Descripción** |
| --- | --- | --- |
| TopAppBar | CenterAlignedTopAppBar | Menú hamburguesa + AcaciosWork verde + Avatar |
| Saludo | Text Headline Large | ¡Bienvenido de nuevo! con fecha |
| Tarjeta Ganancia | ElevatedCard ancho completo | Ícono naranja + monto verde + label |
| Tarjetas Costo/Inventario | Row con 2 ElevatedCard 50/50 | Métricas secundarias en paralelo |
| Buscador | OutlinedTextField con ícono Search | Buscar producto... |
| Header tabla | Row con Text mayúsculas | Nombre / UnidadStock / Estado |
| Filas bodega | ElevatedCard con Row | Indicador color + nombre + stock + badge |

![](data:image/jpeg;base64...)

## Pantalla 3 — Menú Principal (Navigation Drawer)

Drawer lateral con 10 módulos. Se activa con el botón hamburguesa de la TopAppBar.

**Layout raíz:** ModalNavigationDrawer

| **Componente** | **Tipo** | **Descripción** |
| --- | --- | --- |
| Header drawer | Column con Text | Logo verde + nombre usuario naranja |
| Ítem activo | NavigationDrawerItem fondo marrón | Ítem seleccionado resaltado |
| Ítems inactivos | NavigationDrawerItem transparente | Ícono + texto blanco |
| Divider | HorizontalDivider | Separador antes del footer |
| Cerrar sesión | TextButton rojo con ícono | ExitToApp en rojo |
| Footer copyright | Text centrado pequeño | Copyright y nombre del autor |

![](data:image/jpeg;base64...)

## Pantalla 4 — Inventario de Productos

Listado completo del catálogo con control de stock, creación de productos y movimientos de entrada/salida.

**Layout raíz:** Scaffold + LazyColumn

| **Componente** | **Tipo** | **Descripción** |
| --- | --- | --- |
| Barra búsqueda + botón | Row con OutlinedTextField + FilledButton verde | Buscar producto / + Nuevo |
| Tarjeta producto | ElevatedCard por ítem | Nombre, código, unidad |
| Stock badge | Text coloreado | Rojo=0, naranja=bajo, verde=ok |
| Precios | Row con 3 columnas | P. Compra / IVA / P. Venta naranja |
| Vencimiento | Text + fecha | N/A o fecha real del lote |
| Botones movimiento | FilledButton verde Entrada + rojo Salida | Registra lotes de inventario |
| Acciones | Ícono lápiz + basura roja | Editar / Eliminar con diálogo confirmación |

![](data:image/jpeg;base64...)

## Pantalla 5 — Vender (Punto de Venta POS)

Módulo POS. Permite buscar productos, agregar al carrito, asociar cliente y registrar la venta al backend.

**Layout raíz:** Scaffold + Column con verticalScroll

| **Componente** | **Tipo** | **Descripción** |
| --- | --- | --- |
| Encabezado | Text + emoji carrito | Venta de Productos |
| Buscador producto | OutlinedTextField con DropdownMenu | Búsqueda en tiempo real |
| Selector cliente | ElevatedCard clicable | Cliente Genérico + botón + Nuevo |
| Sección carrito | Text Título | Productos en la Venta |
| Resumen | ElevatedCard con Column | Subtotal / Total en verde |
| Botón Vaciar | OutlinedButton rojo | Limpia el carrito |
| Botón Registrar Venta | FilledButton | Envía POST al API con validación |

![](data:image/jpeg;base64...)

## Pantalla 6 — Clientes

Base de datos de clientes con búsqueda en tiempo real, métricas y CRUD completo.

**Layout raíz:** Scaffold + LazyColumn

| **Componente** | **Tipo** | **Descripción** |
| --- | --- | --- |
| Tarjetas métricas | Row con 2 ElevatedCard | Total Clientes / Activos con íconos |
| Barra búsqueda | OutlinedTextField + FilledButton verde | Buscar cliente + Nuevo |
| Tarjeta cliente | ElevatedCard por ítem | Avatar naranja + nombre + documento |
| Detalle | Column dentro de tarjeta | Teléfono / Email / Dirección |
| Badge frecuente | FilledButton verde pequeño | Frecuente para clientes recurrentes |
| Acciones | Ícono lápiz + basura roja | Editar / Eliminar |
| Dialog creación | AlertDialog de Material3 | Formulario de nuevo cliente |

![](data:image/jpeg;base64...)

## Pantalla 7 — Proveedores

Directorio de proveedores con datos de contacto completos y cuenta bancaria.

**Layout raíz:** Scaffold + LazyColumn

| **Componente** | **Tipo** | **Descripción** |
| --- | --- | --- |
| Barra búsqueda + botón | Row | Buscar proveedor + Nuevo verde |
| Tarjeta proveedor | ElevatedCard | Avatar verde + nombre + NIT |
| Detalle | Column | Teléfono / Email / Dirección |
| Cuenta bancaria | Text verde destacado | Cuenta: Bancolombia XXXXXXXXX |
| Acciones | Ícono lápiz + basura roja | Editar / Eliminar |
| Dialog creación | AlertDialog de Material3 | Formulario de nuevo proveedor |

![](data:image/jpeg;base64...)

## Pantalla 8 — Módulo de Reportes

Hub de análisis comercial: generación de PDF, preguntas inteligentes y gráficos estadísticos.

**Layout raíz:** Scaffold + Column + LazyColumn

| **Componente** | **Tipo** | **Descripción** |
| --- | --- | --- |
| Encabezado módulo | Row con ícono naranja + Text | Módulo de Reportes |
| Tarjeta Generar PDF | ElevatedCard grande | Ícono naranja + descripción 10 reportes |
| Tarjeta Preguntas IA | ElevatedCard grande | Ícono naranja + motor de análisis |
| Tarjeta Análisis Gráfico | ElevatedCard grande | Ícono verde + gráficos |
| Navegación sub-pantallas | Estado remember interno | Hub → sub-pantalla → regreso |
| Botón Atrás | IconButton ArrowBack | Regresa al hub principal |

![](data:image/jpeg;base64...)

## Pantalla 9 — Alertas de Inventario

Monitor de stock crítico y vencimientos. Filtra productos según umbral configurado.

**Layout raíz:** Scaffold + LazyColumn

| **Componente** | **Tipo** | **Descripción** |
| --- | --- | --- |
| Encabezado | Row con ícono triángulo + Text rojo | Alertas de Inventario |
| Header sección | Row con Text rojo + FilledButton naranja | Próximos a Vencer + Vencimientos PDF |
| Tarjeta alerta | ElevatedCard por producto | Nombre + fecha de vencimiento |
| Badge vencido | OutlinedButton pequeño rojo | Vencido hace N días |
| Chips filtro | FilterChip | Crítico / Bajo / Vencimiento próximo |

![](data:image/jpeg;base64...)

## Pantalla 10 — Historial de Ventas

Registro cronológico de todas las transacciones con filas expandibles para ver detalle de productos.

**Layout raíz:** Scaffold + LazyColumn

| **Componente** | **Tipo** | **Descripción** |
| --- | --- | --- |
| Tarjetas métricas | Row con 2 ElevatedCard | Total Ventas / Total Recaudado verde |
| Buscador | OutlinedTextField | Buscar venta, cliente o fecha... |
| Header tabla | Row con Text mayúsculas | # / Fecha / Cliente / Total |
| Fila venta | ElevatedCard expandible | ID + fecha + cliente + total verde |
| Badge productos | FilledButton índigo pequeño | 📦 N producto(s) por venta |
| Detalle expandido | Column animado | Lista de ítems con subtotales |

![](data:image/jpeg;base64...)

## Pantalla 11 — Usuarios del Sistema

Gestión de accesos y roles: crear, editar y eliminar usuarios con credenciales y permisos.

**Layout raíz:** Scaffold + LazyColumn

| **Componente** | **Tipo** | **Descripción** |
| --- | --- | --- |
| Encabezado | Row con ícono naranja + Text | Usuarios del Sistema |
| Barra de búsqueda | OutlinedTextField | Buscar en la tabla... |
| Header tabla | Row con Text mayúsculas | Nombre / Usuario / Rol / Estado |
| Fila usuario | ElevatedCard | Nombre completo + username + email |
| Badge Rol | Surface naranja | Admin / Auxiliar |
| Badge Estado | Surface verde/gris | Activo / Inactivo |
| Acciones | Ícono lápiz + basura roja | Editar / Eliminar con AlertDialog |

![](data:image/jpeg;base64...)

## Pantalla 12 — Configuración del Sistema

Panel de ajustes globales dividido en tres pestañas: General, Hardware POS y Ticket de venta.

**Layout raíz:** Scaffold + Column con TabRow + verticalScroll

| **Componente** | **Tipo** | **Descripción** |
| --- | --- | --- |
| Encabezado | Row con ícono engranaje naranja | Configuración del Sistema |
| Subtítulo | Text | Ajustes globales y hardware POS |
| Pestañas | TabRow con 3 Tab | General / Hardware / Ticket |
| Tab activa | Tab con fondo naranja | Indicador visual de selección |
| Campo Hardware | ElevatedCard por periférico | Lector de código de barras, Balanza, Impresora |
| Dropdown modo | ExposedDropdownMenuBox | Emulación de Teclado (USB HID) |
| Botón Guardar | FilledButton en cabecera | Persiste cambios en el backend |
| Cerrar Sesión | OutlinedButton rojo con ícono | Limpia sesión y regresa al Login |

![](data:image/jpeg;base64...)

# 4. Responsividad

El diseño fue pensado para adaptarse a las resoluciones más comunes del ecosistema Android:

| **Resolución** | **Dispositivo** | **Adaptación** |
| --- | --- | --- |
| 360 × 640 dp | Dispositivo compacto | Tarjetas apiladas, texto reducido |
| 393 × 852 dp | Smartphone estándar | Layout base de diseño |
| 412 × 915 dp | Smartphone grande | Mayor espaciado en tarjetas |
| 600 × 960 dp+ | Tablet | Grid 2 columnas en listas |

**Técnicas de responsividad aplicadas:**

* fillMaxWidth() en todos los contenedores para adaptarse al ancho disponible.
* weight(1f) en filas (Row) para distribución proporcional de espacio.
* wrapContentHeight() en tarjetas para adaptarse al contenido dinámico.
* Padding sistémico mediante WindowInsets para respetar barras del sistema.
* ConstraintLayout con guidelines del 50% para dividir métricas en dos columnas.
* LazyColumn y LazyVerticalGrid para virtualización eficiente en listas largas.
* sp para tipografía y dp para espaciados siguiendo convenciones de densidad Android.

# 5. Relación XML ↔ Componentes por Pantalla

| **Pantalla** | **Layout Raíz** | **Componentes XML / Compose Principales** |
| --- | --- | --- |
| Login | ConstraintLayout / Box | Card, OutlinedTextField, Button gradiente |
| Dashboard | Scaffold + ModalNavigationDrawer | TopAppBar, ElevatedCard, LazyColumn |
| Menú Principal | ModalNavigationDrawer | NavigationDrawerItem, HorizontalDivider |
| Inventario | Scaffold + LazyColumn | ElevatedCard, OutlinedTextField, FilledButton |
| Vender (POS) | Scaffold + Column scroll | DropdownMenu, ElevatedCard, OutlinedButton |
| Clientes | Scaffold + LazyColumn | ElevatedCard, AlertDialog, Badge |
| Proveedores | Scaffold + LazyColumn | ElevatedCard, AlertDialog, Text verde |
| Reportes | Scaffold + Column | ElevatedCard grandes, navegación por estado |
| Alertas | Scaffold + LazyColumn | FilterChip, OutlinedButton rojo, FilledButton |
| Historial | Scaffold + LazyColumn | ElevatedCard expandible, Badge índigo |
| Usuarios | Scaffold + LazyColumn | Surface badges, AlertDialog, IconButton |
| Configuración | Scaffold + TabRow | Tab, ExposedDropdownMenuBox, SwitchMaterial |

# 6. Herramientas Utilizadas

| **Herramienta** | **Categoría** | **Uso** |
| --- | --- | --- |
| Android Studio Ladybug | IDE | Desarrollo, emulación y depuración |
| Jetpack Compose | Framework UI | Construcción declarativa de interfaces |
| Material Design 3 | Sistema de diseño | Guía de componentes, colores y tipografía |
| Material Icons Extended | Iconografía | Biblioteca de íconos vectoriales |
| Kotlin | Lenguaje | Lógica de negocio y composables |
| Figma / Penpot | Prototipado | Wireframes iniciales de alta fidelidad |
| Postman | Testing API | Validación de endpoints REST |
| Git / GitHub | Control versiones | Repositorio del proyecto |

# CONCLUSIONES

La maquetación de la interfaz gráfica de AcaciosWork demuestra la aplicación práctica de los conocimientos en diseño XML para Android, abarcando desde la selección de paleta de colores y tipografía hasta la implementación de layouts complejos como el ModalNavigationDrawer, LazyColumn y ConstraintLayout.

El prototipo de alta fidelidad presentado en este documento corresponde a las pantallas reales de la aplicación en ejecución, lo que confirma la coherencia entre el diseño visual y su implementación técnica. Las 12 pantallas documentadas cubren el ciclo completo de operación del sistema.

El uso de Material Design 3 garantiza una experiencia de usuario moderna, accesible e intuitiva, mientras que la arquitectura modular de Jetpack Compose facilita el mantenimiento y la escalabilidad del proyecto. La implementación de responsividad mediante fillMaxWidth(), weight() y WindowInsets asegura que la aplicación funcione correctamente en dispositivos de distintos tamaños.

Este documento constituye la evidencia GA5-220501095-AA1-EV08 del programa de formación del SENA, demostrando competencia en el diseño y maquetación de interfaces gráficas para aplicaciones móviles en el ecosistema Android.

# REFERENCIAS

Servicio Nacional de Aprendizaje — SENA. (2026). *Maquetación interfaz gráfica XML*. <https://zajuna.sena.edu.co/>

Material Design. (2026). *Material Design 3 — Design system*. Google LLC. <https://m3.material.io/>

Android Developers. (2026). *Jetpack Compose — Build better apps faster*. Google LLC. <https://developer.android.com/compose>

Android Developers. (2026). *Layouts in Compose*. Google LLC. <https://developer.android.com/jetpack/compose/layouts>

Android Developers. (2024). *ConstraintLayout*. Google LLC. <https://developer.android.com/reference/androidx/constraintlayout/widget/ConstraintLayout>