import type { Context } from "hono";

export const PRIVACY_HTML = `<!doctype html>
<html lang="es">
<head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>Política de Privacidad - AcaciosWork Bot</title>
<style>body{font-family:system-ui,sans-serif;max-width:720px;margin:40px auto;padding:0 20px;color:#333;line-height:1.7}h1{color:#1a1a2e}footer{margin-top:60px;font-size:13px;color:#888}</style>
</head>
<body>
<h1>Política de Privacidad</h1>
<p>Última actualización: 26 de julio de 2026</p>
<p>Este bot de WhatsApp es operado como parte del sistema AcaciosWork.</p>
<h2>Datos que recopilamos</h2><p>El Bot recopila los mensajes que los usuarios envian a traves de WhatsApp, incluyendo el numero de telefono y el contenido.</p>
<h2>Uso de los datos</h2><p>Se usan exclusivamente para responder consultas y mejorar el servicio.</p>
<h2>Almacenamiento</h2><p>Los datos se almacenan en servidores de Cloudflare. No se comparten con terceros.</p>
<h2>Contacto</h2><p>andresrubiel@gmail.com</p>
<footer><p>AcaciosWork</p></footer>
</body></html>`;

export const TERMS_HTML = `<!doctype html>
<html lang="es">
<head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>Terminos del Servicio - AcaciosWork Bot</title>
<style>body{font-family:system-ui,sans-serif;max-width:720px;margin:40px auto;padding:0 20px;color:#333;line-height:1.7}h1{color:#1a1a2e}footer{margin-top:60px;font-size:13px;color:#888}</style>
</head>
<body>
<h1>Terminos del Servicio</h1>
<p>Ultima actualizacion: 26 de julio de 2026</p>
<p>Al usar este Bot de WhatsApp, aceptas los terminos.</p>
<h2>Uso del servicio</h2><p>El Bot proporciona respuestas automaticas. No garantiza precision absoluta.</p>
<h2>Limitacion de responsabilidad</h2><p>El operador no se hace responsable por decisiones basadas en la informacion del Bot.</p>
<h2>Modificaciones</h2><p>Nos reservamos el derecho de modificar estos terminos.</p>
<footer><p>AcaciosWork</p></footer>
</body></html>`;

export function legalPages() {
  return {
    privacy: (c: Context) => c.html(PRIVACY_HTML),
    terms: (c: Context) => c.html(TERMS_HTML),
  };
}
