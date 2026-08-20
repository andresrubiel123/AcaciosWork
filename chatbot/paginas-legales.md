# Páginas Legales — Worker Independiente

Worker de Cloudflare que sirve las páginas de Política de Privacidad y Términos del Servicio para publicar la app de Meta.

## URLs

| Página | URL |
|---|---|
| **Política de Privacidad** | https://acacios-legal.andresrubiel.workers.dev/privacy |
| **Términos del Servicio** | https://acacios-legal.andresrubiel.workers.dev/terms |

## Ubicación del código

`chatbot/legal-worker/index.js`

## Despliegue

```bash
cd chatbot/legal-worker
npx wrangler deploy
```

## Costo

**$0** — Cloudflare Workers Free Plan (100k solicitudes/día, sin fecha de vencimiento). No requiere dominio propio ni tarjeta de crédito.

## Editar contenido

Para cambiar el texto de las páginas, editar `chatbot/legal-worker/index.js` y redesplegar.
