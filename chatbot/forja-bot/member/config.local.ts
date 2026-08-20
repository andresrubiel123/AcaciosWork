// member/config.local.ts — generado por `forja init`. Edítalo cuando quieras.
// NUNCA se sobrescribe al actualizar el bot.

export const memberConfig = {
  businessName: "AcaciosWork",
  botName: "Asistente de AcaciosWork",
  language: "es" as "es" | "en",
  tier: "free" as "free" | "pro",
  timezone: "America/Mexico_City",
  contactEmail: "andresrubiel@gmail.com",
};
export type MemberConfig = typeof memberConfig;

export const businessConfig = {
  hours: "Lunes a viernes 8am a 6pm",
  services: [] as { name: string; price: number }[],
  location: "Colombia",
  paymentMethods: ["transferencia", "efectivo", "tarjeta"] as string[],
  contactPhone: "+57 3144655271",
  customFields: {
    queHacemos: "plataforma de gestión empresarial SaaS",
    ofrecemos: "inventarios, ventas, clientes, punto de venta POS, inteligencia de negocio",
    tono: "formal y profesional, claro y respetuoso",
    sitioWebYRedes: "",
    preguntasFrecuentes: "¿Cómo registro una venta? ¿Cómo veo el inventario? ¿Cómo agrego un producto?",
    reglasYEscalacion: "No compartir datos confidenciales del negocio, escalar a humano si el cliente lo solicita",
  } as Record<string, string>,
};

import type { CommentFunnel } from "../src/channels/comment-funnel";
export const commentFunnels: CommentFunnel[] = [];

export const catalog: { name: string; price: number; description?: string; sku?: string }[] = [];
