export default {
  async fetch(request) {
    const url = new URL(request.url);

    if (url.pathname === "/privacy" || url.pathname === "/privacy/") {
      return new Response(PRIVACY, { headers: { "content-type": "text/html;charset=utf-8" } });
    }
    if (url.pathname === "/terms" || url.pathname === "/terms/") {
      return new Response(TERMS, { headers: { "content-type": "text/html;charset=utf-8" } });
    }
    return new Response("not found", { status: 404 });
  },
};

const PRIVACY = `<!DOCTYPE html>
<html lang="es">
<head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>Politica de Privacidad</title>
<style>body{font-family:system-ui,sans-serif;max-width:720px;margin:40px auto;padding:0 20px;color:#333;line-height:1.7}h1{color:#1a1a2e}</style>
</head>
<body>
<h1>Politica de Privacidad</h1>
<p>Ultima actualizacion: 26 julio 2026</p>
<p>Este bot de WhatsApp es parte del sistema AcaciosWork.</p>
<p>Datos: mensajes y numero de telefono para responder consultas.</p>
<p>Almacenamiento: servidores Cloudflare. No compartimos con terceros.</p>
<p>Contacto: andresrubiel@gmail.com</p>
</body></html>`;

const TERMS = `<!DOCTYPE html>
<html lang="es">
<head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>Terminos del Servicio</title>
<style>body{font-family:system-ui,sans-serif;max-width:720px;margin:40px auto;padding:0 20px;color:#333;line-height:1.7}h1{color:#1a1a2e}</style>
</head>
<body>
<h1>Terminos del Servicio</h1>
<p>Ultima actualizacion: 26 julio 2026</p>
<p>Al usar este bot aceptas los terminos.</p>
<p>El bot da respuestas automaticas. No garantiza precision absoluta.</p>
<p>Nos reservamos modificar estos terminos.</p>
</body></html>`;
