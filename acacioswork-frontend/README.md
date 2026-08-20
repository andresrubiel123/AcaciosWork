# AcaciosWork - Frontend Web (La Ventana Ligera)

## Descripción
Este es el cliente web del ecosistema **AcaciosWork**, diseñado para ofrecer una experiencia ligera, rápida y accesible desde cualquier dispositivo con navegador. Construido bajo la filosofía de **Vanilla Web Development** y ahora integrado directamente en la estructura de recursos de Spring Boot con **Thymeleaf**, lo que permite una recarga dinámica en desarrollo y un despliegue unificado.

Este módulo permite a los administradores y gerentes consultar el estado del negocio, inventarios y ventas en tiempo real sin necesidad de instalar software adicional.

### Funcionalidades Clave:
- **Acceso Universal**: Compatible con Chrome, Firefox, Safari y navegadores móviles.
- **Consumo Centralizado**: Cliente API optimizado (`src/main/resources/static/js/api.js`) que maneja automáticamente los tokens JWT.
- **Dashboard de Control**: Panel visual para la supervisión de operaciones segmentado por roles (Administrador vs. Auxiliar).
- **Arquitectura Modular**: Uso de fragmentos de Thymeleaf para evitar código duplicado y estructurar las interfaces de forma limpia.

## Tecnologías
- **HTML5**: Estructura semántica para SEO y accesibilidad.
- **Thymeleaf**: Motor de plantillas de Spring para renderizar vistas dinámicas del servidor.
- **CSS3 Moderno**: Diseño basado en variables y Flexbox/Grid para responsividad.
- **JavaScript (ES6+)**: Lógica asíncrona mediante `async/await` y `fetch` API.
- **JWT Storage**: Manejo seguro de sesiones en `localStorage`.

## Estructura de Carpetas
La estructura de este módulo sigue el estándar de recursos de un proyecto Spring Boot:

```
acacioswork-frontend/
├── src/
│   └── main/
│       └── resources/
│           ├── static/
│           │   ├── css/
│           │   │   ├── dashboard.css
│           │   │   └── styles.css
│           │   └── js/
│           │       ├── core/
│           │       │   ├── api.js
│           │       │   ├── auth.js
│           │       │   └── utils.js
│           │       ├── modules/
│           │       │   ├── catalogos/ (clientes.js, proveedores.js, usuarios.js)
│           │       │   ├── configuracion/ (configuracion.js)
│           │       │   ├── dashboard/ (dashboard.js)
│           │       │   ├── inteligencia/ (inteligencia.js)
│           │       │   ├── inventario/ (inventario.js)
│           │       │   ├── reportes/ (reportes.js, charts/)
│           │       │   └── ventas/ (ventas.js)
│           │       └── shared/
│           │           ├── buscador.js
│           │           ├── exportador-pdf.js
│           │           ├── modal.js
│           │           └── notificacion.js
│           └── templates/
│               ├── administrador-dashboard.html
│               ├── auxiliar-dashboard.html
│               ├── login.html
│               └── fragments/ (alertas, clientes, configuracion, gráficos, modals, navbar, etc.)
├── package.json
├── package-lock.json
└── README.md
```

### Descripción de Archivos Principales

| Archivo / Carpeta | Descripción |
|---------|-------------|
| `src/main/resources/templates/login.html` | Interfaz de acceso al sistema |
| `src/main/resources/templates/administrador-dashboard.html` | Panel principal para el administrador |
| `src/main/resources/templates/auxiliar-dashboard.html` | Panel principal para el auxiliar de tienda |
| `src/main/resources/templates/fragments/` | Fragmentos Thymeleaf reutilizables (navbar, stats-cards, products-table, etc.) |
| `src/main/resources/static/css/` | Estilos globales y específicos del panel de control |
| `src/main/resources/static/js/core/` | Lógica central: comunicación con API (`api.js`), autenticación (`auth.js`) y utilidades comunes (`utils.js`) |
| `src/main/resources/static/js/modules/` | Lógica de módulos de negocio (Ventas, Inventario, Reportes, IA Inteligente) |
| `src/main/resources/static/js/shared/` | Componentes lógicos compartidos (Buscador, Exportador PDF, Modales, Notificaciones) |

---

# Estado del Proyecto (Avance)

### ✅ Finalizado y Estabilizado
- **Integración con Spring Boot / Thymeleaf**: Configuración del motor de plantillas y recursos estáticos para desarrollo dinámico sin caché.
- **Flujo de Autenticación**: Inicio de sesión completo con validación de credenciales, decodificación de roles y redirección automática (Administrador vs. Auxiliar).
- **Cliente API**: Implementación de `apiRequest` con cabeceras Bearer JWT y redirección por sesión expirada (401/403).
- **Dashboard Modularizado**: Vistas de dashboard separadas para Administrador y Auxiliar utilizando fragmentos de Thymeleaf para mejorar la mantenibilidad del código.
- **Seguridad**: Validación del token JWT a nivel cliente en los accesos a dashboards.
- **Modularización Completa de Lógica**: Extracción total de la lógica JavaScript de los archivos HTML hacia la estructura organizada bajo la carpeta `static/js/` (en subcarpetas `core`, `modules` y `shared`).
- **Preguntas Inteligentes**: Interfaz de Inteligencia Artificial para consultas operacionales y financieras completamente implementada e integrada en el cliente web.
- **Exportación e Impresión**: Componentes dinámicos para la exportación de comprobantes y reportes a PDF.
- **Hub de Reportes Consolidado**: Integración de Reportes PDF, Preguntas IA y Gráficos Estadísticos en un único módulo unificado con botones de navegación internos, simplificando significativamente el menú y navbar principal de la plataforma.

### 🔄 En Desarrollo / Estabilización
- **Diseño Responsivo**: Ajustes de layouts web móviles.
- **Optimización de Animaciones**: Transiciones de micro-interacciones.

---

# Sugerencias y Próximos Pasos (Lo que falta)

1.  **Soporte PWA**: Convertir el sitio en una **Progressive Web App** para permitir su instalación y funcionamiento básico offline.
2.  **Optimización del Stock en Tiempo Real**: Añadir alertas visuales tipo "toast" instantáneas utilizando WebSockets o Server-Sent Events si es requerido.

---

# Guía de Ejecución
El Frontend se sirve y despliega directamente a través del **Backend** (puerto predeterminado: 8081).
Para ejecutar en modo desarrollo:
1. Asegúrate de que el **Backend** esté corriendo.
2. Accede a `http://localhost:8081/login` o `http://localhost:8081/` en tu navegador.
3. Los cambios en los archivos del Frontend se reflejarán inmediatamente en el navegador gracias a que la caché de Thymeleaf está desactivada (`spring.thymeleaf.cache=false`) y la ruta apunta directamente al sistema de archivos local (`file:../acacioswork-frontend/src/main/resources/...`).

---

# Estándares de Código
- Todo bloque de JavaScript debe usar la firma: `/** Descripción. @author RADJ */`.
- Seguir la convención de nombres `camelCase` para variables y funciones.
- Utilizar fragmentos de Thymeleaf (`th:fragment`, `th:replace`, etc.) para componentes UI repetitivos o modulares.
- Mantener el aislamiento: El frontend NUNCA conoce la estructura de la base de datos, solo consume el DTO de la API.
