# Bot AcaciosWork — Resumen del despliegue

✅ **Bot AcaciosWork desplegado exitosamente**

## 📍 En vivo

| Recurso | URL |
|---|---|
| **URL base** | https://forja-starter-5ab7d0.andresrubiel.workers.dev |
| **Dashboard admin** | https://forja-starter-5ab7d0.andresrubiel.workers.dev/admin/overview |
| **Usuario** | `admin` |
| **Password** | `acacios2026` |

## ✅ Lo que se hizo

| Paso | Estado |
|---|---|
| `npx forjabot init` (Starter gratis) | ✅ |
| Instalar dependencias (pnpm) | ✅ |
| Auth Cloudflare (wrangler login) | ✅ |
| Crear D1 DB `horizontes_bot_starter_5ab7d0_db` | ✅ |
| Crear Vectorize KB `horizontes_bot_starter_5ab7d0_kb` | ✅ |
| Configurar DeepSeek como proveedor de IA | ✅ |
| Secrets: OPENAI_API_KEY, DASHBOARD_PASSWORD, KB_REINDEX_TOKEN | ✅ |
| Schema DB (45 queries, 22 tablas) | ✅ |
| Deploy a Cloudflare | ✅ |

## 🔧 Modificaciones al código Forja

- Arreglé un bug con **saltos de línea Windows** (`\r\n`) en el preflight script — sin esto el deploy fallaba por no detectar placeholders en comentarios.
- Añadí soporte para `OPENAI_BASE_URL` para poder usar **DeepSeek** como proveedor.

## 📡 Para conectar canales

Entra al dashboard: https://forja-starter-5ab7d0.andresrubiel.workers.dev/admin/conexiones
https://forja-starter-5ab7d0.andresrubiel.workers.dev/admin/agente

Nombre de usuario: "admin"
Contraseña: "acacios2026"
Ahí puedes conectar WhatsApp, Telegram, Instagram o Messenger.
