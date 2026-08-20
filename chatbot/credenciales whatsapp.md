# WhatsApp Cloud API — AcaciosWork Bot

## Configuración

| Campo | Valor |
|---|---|
| **Número de prueba** | `+1 (555) 151-8709` |
| **Phone Number ID** | `1219572274578929` |
| **WABA ID** | `1914766132536884` |
| **Verify Token** | `acacios2026verify` |
| **Webhook URL** | `https://forja-starter-5ab7d0.andresrubiel.workers.dev/webhooks/whatsapp` |

## Meta App

| Campo | Valor |
|---|---|
| **App ID** | `1085649117451683` |
| **App Secret** | `e4ae32b15c04bb97330000373ae0dc85` |

## Secrets en Cloudflare

- `WHATSAPP_PHONE_NUMBER_ID` — ✅ configurado
- `WHATSAPP_ACCESS_TOKEN` — ✅ configurado
- `WHATSAPP_VERIFY_TOKEN` — ✅ configurado
- `WHATSAPP_APP_SECRET` — ✅ configurado

## Prueba

Para enviar un mensaje de prueba desde el bot a tu WhatsApp real:
1. Abre: https://forja-starter-5ab7d0.andresrubiel.workers.dev/admin/conexiones
2. Copia el webhook URL
3. En el dashboard de Meta Developers, ve a WhatsApp → Paso 1 → "Enviar mensaje"
4. Pon tu número real (con código de país) y envía un mensaje de prueba
