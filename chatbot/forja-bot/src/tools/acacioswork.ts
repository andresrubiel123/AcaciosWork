import { tool } from "ai";
import { z } from "zod";
import type { Env } from "../env";

const API_TIMEOUT = 8000;

async function acaciosFetch(env: Env, path: string): Promise<any> {
  const base = env.ACACIOSWORK_API_URL;
  if (!base) return { error: "AcaciosWork API no configurada (falta ACACIOSWORK_API_URL)." };
  try {
    const ctrl = new AbortController();
    const to = setTimeout(() => ctrl.abort(), API_TIMEOUT);
    const res = await fetch(`${base}${path}`, { signal: ctrl.signal });
    clearTimeout(to);
    if (!res.ok) return { error: `HTTP ${res.status} en ${path}` };
    return await res.json();
  } catch (e: any) {
    return { error: `Error conectando con AcaciosWork: ${e.message || e}` };
  }
}

export function acaciosworkTool(env: Env) {
  return tool({
    description:
      "Consulta el sistema AcaciosWork (gestión empresarial: inventarios, ventas, clientes, productos, reportes). " +
      "Usa 'accion' para elegir qué consultar. Devuelve datos reales del sistema.",
    inputSchema: z.object({
      accion: z.enum([
        "productos",
        "producto",
        "inventario",
        "inventario-alertas",
        "clientes",
        "cliente",
        "ventas",
        "ventas-diarias",
        "ganancias",
        "stock-bajo",
        "proveedores",
        "categorias",
      ]).describe("Qué consultar en AcaciosWork"),
      id: z.string().optional().describe("ID para consultar un producto o cliente específico (ej: '1')"),
    }),
    execute: async ({ accion, id }) => {
      switch (accion) {
        case "productos":
          return acaciosFetch(env, "/api/productos");
        case "producto":
          if (!id) return { error: "Se requiere 'id' para consultar un producto específico." };
          return acaciosFetch(env, `/api/productos/${id}`);
        case "inventario":
          return acaciosFetch(env, "/api/inventario");
        case "inventario-alertas":
          return acaciosFetch(env, "/api/inventario/alertas");
        case "clientes":
          return acaciosFetch(env, "/api/clientes");
        case "cliente":
          if (!id) return { error: "Se requiere 'id' para consultar un cliente específico." };
          return acaciosFetch(env, `/api/clientes/${id}`);
        case "ventas":
          return acaciosFetch(env, "/api/ventas");
        case "ventas-diarias":
          return acaciosFetch(env, "/api/reportes/ventas-diarias");
        case "ganancias":
          return acaciosFetch(env, "/api/reportes/ganancias");
        case "stock-bajo":
          return acaciosFetch(env, "/api/reportes/stock-bajo");
        case "proveedores":
          return acaciosFetch(env, "/api/proveedores");
        case "categorias":
          return acaciosFetch(env, "/api/categorias");
        default:
          return { error: `Acción desconocida: ${accion}` };
      }
    },
  });
}
