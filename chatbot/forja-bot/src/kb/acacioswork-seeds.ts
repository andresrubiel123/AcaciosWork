// KB seeds — documentos que describen AcaciosWork para el bot
export const ACACIOSWORK_KB_SEEDS = [
  {
    title: "¿Qué es AcaciosWork?",
    content: `AcaciosWork es una plataforma inteligente de gestión empresarial para negocios físicos (tiendas de barrio, minimercados, ferreterías, etc.). Es un SaaS multiplataforma que combina gestión operativa (inventarios, ventas, clientes) con inteligencia de negocio automatizada. Está construida con Java/Spring Boot (backend), Swing/FlatLaf (escritorio), HTML/JS vanilla (frontend web) y Kotlin (Android). Tiene un módulo de Preguntas Inteligentes que analiza datos en tiempo real: rentabilidad, rotación de productos, tendencias de clientes, y más.`,
  },
  {
    title: "Módulos de AcaciosWork",
    content: `AcaciosWork tiene los siguientes módulos principales:

1. INVENTARIOS: Control de stock con alertas de stock mínimo y óptimo. Soporta múltiples unidades de medida y fechas de vencimiento.
2. VENTAS (POS): Punto de venta para registrar ventas al instante. Genera tickets y controla el flujo de caja.
3. CLIENTES: Gestión de clientes con historial de compras y datos de contacto.
4. PRODUCTOS: Catálogo de productos con códigos de barras, precios de compra/venta, IVA, y categorías.
5. PROVEEDORES: Registro de proveedores con precios y productos asociados.
6. REPORTES: Ventas diarias, ganancias, productos con stock bajo, productos más rentables.
7. USUARIOS: Control de acceso con roles (administrador, auxiliar) y autenticación JWT.
8. CONFIGURACIÓN: Ajustes del sistema, parámetros de negocio.

Los clientes acceden vía: app de escritorio (gestión pesada), web dashboard (consultas rápidas e inteligencia de negocio), y app Android (gestión móvil).`,
  },
  {
    title: "API de AcaciosWork",
    content: `AcaciosWork expone una API REST en el backend con los siguientes endpoints:

- GET /api/productos — Lista todos los productos
- GET /api/productos/{id} — Detalle de un producto
- GET /api/inventario — Estado del inventario
- GET /api/inventario/alertas — Alertas de inventario
- GET /api/clientes — Lista de clientes
- GET /api/ventas — Registro de ventas
- GET /api/reportes/ventas-diarias — Ventas del día
- GET /api/reportes/ganancias — Ganancias acumuladas
- GET /api/reportes/stock-bajo — Productos por debajo del stock mínimo
- GET /api/proveedores — Lista de proveedores
- GET /api/categorias — Categorías de productos`,
  },
  {
    title: "Preguntas inteligentes de AcaciosWork",
    content: `El módulo de Preguntas Inteligentes de AcaciosWork permite al usuario hacer preguntas predefinidas y obtener respuestas automáticas basadas en datos reales:

- ¿Cuáles fueron los productos más rentables? → Margen × volumen vendido
- ¿Qué productos tienen baja rotación? → Menor cantidad vendida
- ¿Qué productos debo reabastecer? → Stock actual vs. stock mínimo
- ¿Cuál proveedor vende más caro? → Promedio de costo por proveedor
- ¿Qué clientes compran más? → Volumen total de compras
- ¿Qué mes tuvo mayores ganancias? → Ganancia neta mensual
- ¿Qué producto genera pérdidas? → Precio venta < precio costo
- ¿Qué productos llevan sin venderse? → Sin presencia en ventas recientes`,
  },
];
