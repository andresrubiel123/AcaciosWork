# Chatbot AcaciosWork — Forja (Starter)

Bot de IA para AcaciosWork, instalado con Forja (plan gratis) usando DeepSeek.

## 🚀 En vivo

| Recurso | URL |
|---|---|
| **Bot** | https://forja-starter-5ab7d0.andresrubiel.workers.dev |
| **Dashboard admin** | https://forja-starter-5ab7d0.andresrubiel.workers.dev/admin/overview |
| **Usuario admin** | `admin` |
| **Password** | `acacios2026` |

## ⚙️ Configuración

| Campo | Valor |
|---|---|
| **Licencia** | Gratis (Starter) |
| **Lic. Key** | `HZN-5L24-T2LB-KBJA` |
| **Worker** | `forja-starter-5ab7d0` |
| **Nombre del negocio** | AcaciosWork |
| **Bot** | Asistente de AcaciosWork |
| **Idioma** | Español (es-MX) |
| **Tier** | free |
| **Cerebro** | DeepSeek (OpenAI-compatible) |
| **API Key** | DeepSeek (`sk-52a...`) → secreto `OPENAI_API_KEY` |
| **Base URL** | `https://api.deepseek.com/v1` |
| **D1 DB** | `horizontes_bot_starter_5ab7d0_db` (id: `a859ef5d-9445-4325-912a-766ffd32d81e`) |
| **Vectorize KB** | `horizontes_bot_starter_5ab7d0_kb` |
| **Versión** | 1.0.33 |
| **Email licencia** | andresrubiel@gmail.com |
| **Account ID** | `91aa4d779e1f6bf4e53dd2cb4ebfc...` |
| **Subdominio** | `andresrubiel.workers.dev` |

## 🔧 Secrets configurados

- `OPENAI_API_KEY` — DeepSeek
- `DASHBOARD_PASSWORD` — `acacios2026`
- `KB_REINDEX_TOKEN` — token aleatorio

## 📋 Comandos útiles

```bash
# Actualizar el bot a nueva versión
cd chatbot/forja-bot
npx forjabot update

# Ver diagnóstico
npx forjabot doctor

# Conectar canales (WhatsApp, Telegram, etc)
Abrir dashboard admin → /admin/conexiones
```

## 📁 Estructura

```
chatbot/
├── README.md
├── api key de deepseek.md
└── forja-bot/         ← el Worker de Cloudflare
    ├── wrangler.toml
    ├── src/
    ├── member/config.local.ts
    └── package.json
```
