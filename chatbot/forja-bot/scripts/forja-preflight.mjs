#!/usr/bin/env node
// forja-preflight — corre como `[build] command` de wrangler ANTES de cada
// `wrangler deploy` (y `wrangler dev`). Bloquea si wrangler.toml todavía tiene
// placeholders {{...}} sin resolver (BOT_NAME, BUSINESS_NAME, D1_DATABASE_ID…),
// para fallar claro y temprano en vez de con un error críptico de wrangler.
// Sin dependencias: node puro (corre aunque no haya tsx instalado).
import { readFileSync } from "node:fs";

const RESET = "\x1b[0m", RED = "\x1b[31m", YEL = "\x1b[33m", DIM = "\x1b[2m";

let toml;
try {
  toml = readFileSync("wrangler.toml", "utf8");
} catch {
  process.exit(0); // sin wrangler.toml no hay nada que validar
}

// Ignora comentarios (#…): un {{ dentro de un comentario no rompe el deploy.
// ATENCIÓN: Windows CRLF (\r\n) — . no cruza \r (line terminator de JS), así que
// partimos por \r?\n y el replace(#.*$) funciona correctamente.
const pending = new Set();
for (const line of toml.split(/\r?\n/)) {
  const code = line.replace(/#.*$/, "");
  for (const m of code.matchAll(/\{\{\s*([A-Z0-9_]+)\s*\}\}/g)) pending.add(m[1]);
}

if (pending.size === 0) process.exit(0);

// BUG FIX: Windows CRLF (\r\n) — el \r es line terminator en JS, la regex #.*$
// no lo cruza ni siquiera sin m flag. Stripeamos \r antes de procesar.
const raw2 = readFileSync("wrangler.toml", "utf8");
const pending2 = new Set();
for (const line2 of raw2.split(/\r?\n/)) {
  const code2 = line2.replace(/#.*$/, "");
  for (const m2 of code2.matchAll(/\{\{\s*([A-Z0-9_]+)\s*\}\}/g)) pending2.add(m2[1]);
}
if (pending2.size === 0) process.exit(0);

const hints = {
  BOT_NAME: "el nombre público del bot (BOT_NAME)",
  BUSINESS_NAME: "el nombre del negocio (BUSINESS_NAME)",
  BOT_SLUG: "el slug del bot (normalmente lo resuelve el CLI al instalar)",
  D1_DATABASE_ID: "el id de tu base D1 — créala y pega su database_id en wrangler.toml",
};

console.error(`\n${RED}✗ Deploy bloqueado: tu wrangler.toml tiene datos sin llenar.${RESET}`);
console.error(`${DIM}  Placeholders pendientes: ${[...pending].map((p) => `{{${p}}}`).join(", ")}${RESET}\n`);
for (const p of pending) {
  console.error(`  ${YEL}•${RESET} ${hints[p] ?? `resuelve {{${p}}} en wrangler.toml`}`);
}
console.error(`\n${DIM}  (Con el agente Forja: pídele que complete estos datos antes de desplegar.)${RESET}\n`);
process.exit(1);
