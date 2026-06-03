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
├── .github/
│   └── java-upgrade/
│       ├── .gitignore
│       └── 20260501074435/
│           └── logs/
│               └── 0.log
├── src/
│   └── main/
│       └── resources/
│           ├── static/
│           │   ├── css/
│           │   │   ├── dashboard.css
│           │   │   └── styles.css
│           │   └── js/
│           │       ├── api.js
│           │       ├── dashboard.js
│           │       └── grafico-categorias.js
│           └── templates/
│               ├── administrador-dashboard.html
│               ├── auxiliar-dashboard.html
│               ├── login.html
│               └── fragments/
│                   ├── alertas.html
│                   ├── clientes.html
│                   ├── configuracion.html
│                   ├── grafico-categorias.html
│                   ├── grafico-ventas.html
│                   ├── home-products-table.html
│                   ├── modals.html
│                   ├── navbar.html
│                   ├── preguntas-inteligentes.html
│                   ├── products-table.html
│                   ├── proveedores.html
│                   ├── reportes.html
│                   ├── stats-cards.html
│                   ├── usuarios.html
│                   └── vender.html
├── package.json
├── package-lock.json
└── README.md
```

### Descripción de Archivos Principales

| Archivo | Descripción |
|---------|-------------|
| `src/main/resources/templates/login.html` | Interfaz de acceso al sistema |
| `src/main/resources/templates/administrador-dashboard.html` | Panel principal para el administrador |
| `src/main/resources/templates/auxiliar-dashboard.html` | Panel principal para el auxiliar de tienda |
| `src/main/resources/templates/fragments/` | Fragmentos Thymeleaf reutilizables (navbar, stats-cards, products-table, etc.) |
| `src/main/resources/static/css/styles.css` | Estilos globales y variables de diseño |
| `src/main/resources/static/css/dashboard.css` | Estilos específicos para los paneles de control |
| `src/main/resources/static/js/api.js` | Núcleo de comunicación API con el backend |
| `src/main/resources/static/js/dashboard.js` | Lógica interactiva para la carga dinámica de fragmentos |
| `src/main/resources/static/js/grafico-categorias.js` | Lógica para gráficos de categorías |
| `.github/java-upgrade/logs/` | Logs de sesiones de actualización de Java |

---

# Estado del Proyecto (Avance)

### ✅ Finalizado y Estabilizado
- **Integración con Spring Boot / Thymeleaf**: Configuración del motor de plantillas y recursos estáticos para desarrollo dinámico sin caché.
- **Flujo de Autenticación**: Inicio de sesión completo con validación de credenciales, decodificación de roles y redirección automática (Administrador vs. Auxiliar).
- **Cliente API**: Implementación de `apiRequest` con cabeceras Bearer JWT y redirección por sesión expirada (401/403).
- **Dashboard Modularizado**: Vistas de dashboard separadas para Administrador y Auxiliar utilizando fragmentos de Thymeleaf para mejorar la mantenibilidad del código.
- **Seguridad**: Validación del token JWT a nivel cliente en los accesos a dashboards.

### 🔄 En Desarrollo / Estabilización
- **Modularización Completa de Lógica**: Extracción de la lógica JS restante incrustada en los HTMLs a scripts en la carpeta `static/js`.
- **Diseño Responsivo**: Ajustes de layouts para dispositivos móviles.

---

# Sugerencias y Próximos Pasos (Lo que falta)

1.  **Integración de Datos Dinámicos**: Terminar de conectar los fragmentos de Thymeleaf (`usuarios.html`, `clientes.html`, `proveedores.html`) para que hagan llamadas API dinámicas mediante `api.js`.
2.  **Gráficos Interactivos**: Implementar una librería ligera como **Chart.js** en el fragmento de reportes.
3.  **Soporte PWA**: Convertir el sitio en una **Progressive Web App** para permitir su instalación y funcionamiento básico offline.
4.  **Sistema de Notificaciones**: Añadir alertas visuales tipo "toast" para avisar sobre stock crítico en tiempo real.

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
