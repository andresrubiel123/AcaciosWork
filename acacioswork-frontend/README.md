# AcaciosWork - Frontend Web (La Ventana Ligera)

## Descripción
Este es el cliente web del ecosistema **AcaciosWork**, diseñado para ofrecer una experiencia ligera, rápida y accesible desde cualquier dispositivo con navegador. Construido bajo la filosofía de **Vanilla Web Development**, evita el uso de frameworks pesados para garantizar tiempos de carga mínimos y una compatibilidad total con dispositivos móviles y tablets.

Este módulo permite a los administradores y gerentes consultar el estado del negocio, inventarios y ventas en tiempo real sin necesidad de instalar software adicional.

### Funcionalidades Clave:
- **Acceso Universal**: Compatible con Chrome, Firefox, Safari y navegadores móviles.
- **Consumo Centralizado**: Cliente API optimizado (`api.js`) que maneja automáticamente los tokens JWT.
- **Dashboard de Control**: Panel visual para la supervisión de operaciones.
- **Arquitectura Zero-Dependency**: Minimiza vulnerabilidades y mantenimiento mediante el uso de estándares web puros (HTML5, CSS3, ES6+).

## Tecnologías
- **HTML5**: Estructura semántica para SEO y accesibilidad.
- **CSS3 Moderno**: Diseño basado en variables y Flexbox/Grid para responsividad.
- **JavaScript (ES6+)**: Lógica asíncrona mediante `async/await` y `fetch` API.
- **JWT Storage**: Manejo seguro de sesiones en `localStorage`.

## Estructura de Carpetas
- `login.html`: Interfaz de acceso al sistema.
- `dashboard.html`: Panel principal con integración de módulos.
- `js/api.js`: Núcleo de comunicación con el backend.
- `css/styles.css`: Definición de la identidad visual y layouts.

---

# Estado del Proyecto (Avance)

### ✅ Finalizado y Estabilizado
- **Flujo de Autenticación**: Inicio de sesión completo con validación de credenciales y redirección.
- **Cliente API**: Implementación robusta de `apiRequest` con manejo de errores 401/403.
- **Layout Base**: Estructura visual del dashboard lista para recibir módulos de datos.
- **Seguridad**: Protección de rutas básica mediante validación de token.

### 🔄 En Desarrollo / Estabilización
- **Visualización de Datos**: Integración de tablas dinámicas para mostrar inventario.
- **Responsividad**: Ajustes finos para la visualización correcta en smartphones.
- **Modularización**: Extracción de la lógica JS de los archivos HTML a scripts externos por módulo.

---

# Sugerencias y Próximos Pasos (Lo que falta)

1.  **Modularización de Lógica**: Separar la gestión de eventos de `dashboard.html` en archivos como `usuarios.js`, `ventas.js`, etc.
2.  **Gráficos Interactivos**: Implementar una librería ligera como **Chart.js** para mostrar estadísticas de ventas en el dashboard.
3.  **Soporte PWA**: Convertir el sitio en una **Progressive Web App** para permitir su instalación y funcionamiento básico offline.
4.  **Sistema de Notificaciones**: Añadir "toasts" o alertas visuales para avisar sobre stock bajo sin interrumpir el flujo.
5.  **Refactorización a CSS Grid**: Mejorar la adaptabilidad del dashboard para pantallas de gran tamaño (2K/4K).

---

# Guía de Ejecución
Para desplegar la interfaz web:
1. Asegúrate de que el **Backend** esté corriendo en el puerto configurado (predeterminado: 8081).
2. Abre `login.html` directamente en el navegador o utiliza una extensión como **Live Server** para desarrollo fluido.

---

# Estándares de Código
- Todo bloque de JavaScript debe usar la firma: `/** Descripción. @author RADJ */`.
- Seguir la convención de nombres `camelCase` para variables y funciones.
- Mantener el aislamiento: El frontend NUNCA conoce la estructura de la base de datos, solo consume el DTO de la API.
