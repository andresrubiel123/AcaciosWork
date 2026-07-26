/*** módulo javascript del frontend de acacioswork. @author RADJ */
/*** inteligencia.js - módulo de preguntas inteligentes e informes analíticos rápidos en el frontend. @author RADJ */

/*** asegurar que exista la estructura del cache en appstate. @author RADJ */
if (!AppState.cache.iqCache) {
    AppState.cache.iqCache = { ventas: null, productos: null, proveedores: null, clientes: null };
}

/*** inicializa el módulo al entrar en la sección. @author RADJ */
window.loadPreguntasInteligentes = function() {
    const fromInput = document.getElementById('iq-date-from');
    const toInput = document.getElementById('iq-date-to');
    
    if (fromInput && toInput && (!fromInput.value || !toInput.value)) {
        const now = new Date();
        const y = now.getFullYear();
        const m = String(now.getMonth() + 1).padStart(2, '0');
        const d = String(now.getDate()).padStart(2, '0');
        
        fromInput.value = `${y}-${m}-01`;
        toInput.value = `${y}-${m}-${d}`;
        
        window.onIqFilterChange();
    }
    /*** limpiar cache para forzar datos frescos. @author RADJ */
    AppState.cache.iqCache = { ventas: null, productos: null, proveedores: null, clientes: null };
};

/*** maneja el cambio de filtro de rango de fechas y habilita/deshabilita tarjetas. @author RADJ */
window.onIqFilterChange = function() {
    const fromInput = document.getElementById('iq-date-from');
    const toInput = document.getElementById('iq-date-to');
    const status = document.getElementById('iq-filter-status');
    const cards = document.querySelectorAll('.iq-card:not(#iq-card-proximos-vencer)');

    if (fromInput && fromInput.value && toInput && toInput.value) {
        if (status) {
            status.textContent = `Analizando: ${fromInput.value} al ${toInput.value}`;
            status.style.color = '#10b981';
        }
        /*** habilitar todas las tarjetas para consultas. @author RADJ */
        cards.forEach(c => c.classList.remove('disabled'));
    } else {
        if (status) {
            status.textContent = 'Selecciona las fechas para activar el análisis';
            status.style.color = '';
        }
        cards.forEach(c => c.classList.add('disabled'));
    }
    /*** limpiar respuestas anteriores al cambiar el filtro. @author RADJ */
    document.querySelectorAll('[id^="iq-answer-"]').forEach(el => el.innerHTML = '');
    /*** invalidar cache para forzar recarga con nuevo filtro. @author RADJ */
    AppState.cache.iqCache = { ventas: null, productos: null, proveedores: null, clientes: null };
};

/*** obtiene datos de la api con cache para evitar peticiones duplicadas. @author RADJ */
async function iqFetchData() {
    if (!AppState.cache.iqCache.ventas) AppState.cache.iqCache.ventas = await apiRequest('/ventas') || [];
    if (!AppState.cache.iqCache.productos) AppState.cache.iqCache.productos = await apiRequest('/productos') || [];
    if (!AppState.cache.iqCache.proveedores) AppState.cache.iqCache.proveedores = await apiRequest('/proveedores') || [];
    if (!AppState.cache.iqCache.clientes) AppState.cache.iqCache.clientes = await apiRequest('/clientes') || [];
    return AppState.cache.iqCache;
}

/*** filtra ventas por el rango de fechas seleccionado. @author RADJ */
function iqFiltrarVentasPorRango(ventas, dateFrom, dateTo) {
    if (!dateFrom && !dateTo) return ventas;
    
    const fromDate = dateFrom ? new Date(dateFrom + 'T00:00:00') : null;
    const toDate = dateTo ? new Date(dateTo + 'T23:59:59') : null;
    
    return ventas.filter(v => {
        if (!v.fechaHora) return false;
        const fecha = new Date(v.fechaHora);
        if (fromDate && fecha < fromDate) return false;
        if (toDate && fecha > toDate) return false;
        return true;
    });
}

/*** muestra spinner de carga dentro de una tarjeta de respuesta. @author RADJ */
function iqShowLoading(answerId) {
    const el = document.getElementById(answerId);
    if (el) el.innerHTML = '<div class="iq-loading"><div class="iq-spinner"></div>Analizando datos...</div>';
}

/*** muestra la respuesta con animación palpitante en color naranja. @author RADJ */
function iqShowAnswer(answerId, text) {
    const el = document.getElementById(answerId);
    if (el) {
        el.innerHTML = `<div class="iq-answer pulsing">${text}</div>`;
    }
}

/*** muestra mensaje cuando no hay datos suficientes para responder. @author RADJ */
function iqShowEmpty(answerId, msg) {
    const el = document.getElementById(answerId);
    if (el) {
        el.innerHTML = `<div class="iq-answer" style="color: var(--text-muted); border-left-color: var(--text-muted);">${msg || 'Sin datos suficientes para este periodo.'}</div>`;
    }
}

/*** función principal que ejecuta la pregunta inteligente según su tipo. @author RADJ */
window.ejecutarPreguntaInteligente = async function(tipo) {
    const dateFrom = document.getElementById('iq-date-from')?.value;
    const dateTo = document.getElementById('iq-date-to')?.value;
    if (tipo !== 'proximos-vencer' && (!dateFrom || !dateTo)) return;

    const answerId = `iq-answer-${tipo}`;
    iqShowLoading(answerId);

    try {
        const data = await iqFetchData();
        const ventasFiltradas = iqFiltrarVentasPorRango(data.ventas, dateFrom, dateTo);

        /*** despachar según el tipo de pregunta. @author RADJ */
        switch (tipo) {
            case 'rentables':
                iqAnalizarRentables(data, ventasFiltradas, answerId);
                break;
            case 'baja-rotacion':
                iqAnalizarBajaRotacion(data, ventasFiltradas, answerId);
                break;
            case 'reabastecer':
                iqAnalizarReabastecer(data, answerId);
                break;
            case 'proveedor-caro':
                iqAnalizarProveedorCaro(data, answerId);
                break;
            case 'top-clientes':
                iqAnalizarTopClientes(data, ventasFiltradas, answerId);
                break;
            case 'mejor-mes':
                iqAnalizarMejorMes(data, answerId);
                break;
            case 'perdidas':
                iqAnalizarPerdidas(data, answerId);
                break;
            case 'sin-vender':
                iqAnalizarSinVender(data, ventasFiltradas, answerId);
                break;
            case 'proximos-vencer':
                iqAnalizarProximosVencer(data, answerId);
                break;
            default:
                iqShowEmpty(answerId, 'Pregunta no reconocida.');
        }
    } catch (e) {
        iqShowEmpty(answerId, `Error al analizar: ${e.message}`);
        console.error('Error en pregunta inteligente:', e);
    }
};

/*** p1: productos más rentables — margen × cantidad vendida en periodo. @author RADJ */
function iqAnalizarRentables(data, ventasFiltradas, answerId) {
    const prodMap = {};
    data.productos.forEach(p => { prodMap[p.id] = p; });

    const gananciaPorProducto = {};
    ventasFiltradas.forEach(v => {
        if (!v.detalles) return;
        v.detalles.forEach(d => {
            const prod = prodMap[d.idProducto];
            if (!prod) return;
            const margen = (d.precioUnitario - (prod.precioCompra || 0)) * d.cantidad;
            gananciaPorProducto[d.idProducto] = (gananciaPorProducto[d.idProducto] || 0) + margen;
        });
    });

    const ranking = Object.entries(gananciaPorProducto)
        .map(([id, ganancia]) => ({ nombre: prodMap[id]?.nombre || `Producto #${id}`, ganancia }))
        .sort((a, b) => b.ganancia - a.ganancia)
        .slice(0, 3);

    if (ranking.length === 0) {
        iqShowEmpty(answerId, 'No se registraron ventas en este periodo.');
        return;
    }

    const text = ranking.map((r, i) => `${i + 1}. ${r.nombre} → Ganancia: $${Math.round(r.ganancia).toLocaleString()}`).join('\n');
    iqShowAnswer(answerId, text);
}

/*** p2: productos con baja rotación — menor cantidad vendida en el periodo. @author RADJ */
function iqAnalizarBajaRotacion(data, ventasFiltradas, answerId) {
    const cantidadPorProducto = {};
    data.productos.filter(p => p.estado === 1).forEach(p => { cantidadPorProducto[p.id] = 0; });

    ventasFiltradas.forEach(v => {
        if (!v.detalles) return;
        v.detalles.forEach(d => {
            if (cantidadPorProducto[d.idProducto] !== undefined) {
                cantidadPorProducto[d.idProducto] += d.cantidad;
            }
        });
    });

    const prodMap = {};
    data.productos.forEach(p => { prodMap[p.id] = p; });

    const ranking = Object.entries(cantidadPorProducto)
        .filter(([id, qty]) => qty > 0)
        .map(([id, qty]) => ({ nombre: prodMap[id]?.nombre || `Producto #${id}`, cantidad: qty }))
        .sort((a, b) => a.cantidad - b.cantidad)
        .slice(0, 3);

    if (ranking.length === 0) {
        iqShowEmpty(answerId, 'No hay productos con ventas en este periodo.');
        return;
    }

    const text = ranking.map((r, i) => `${i + 1}. ${r.nombre} → Solo ${r.cantidad} uds vendidas`).join('\n');
    iqShowAnswer(answerId, text);
}

/*** p3: productos a reabastecer — stock actual ≤ stock mínimo. @author RADJ */
function iqAnalizarReabastecer(data, answerId) {
    const ranking = data.productos
        .filter(p => p.estado === 1 && p.stockActual <= (p.stockMinimo || 5))
        .map(p => ({ nombre: p.nombre, stockActual: p.stockActual, stockMinimo: p.stockMinimo || 5 }))
        .sort((a, b) => (a.stockActual - a.stockMinimo) - (b.stockActual - b.stockMinimo))
        .slice(0, 3);

    if (ranking.length === 0) {
        iqShowEmpty(answerId, '¡Excelente! Todos los productos tienen stock suficiente.');
        return;
    }

    const text = ranking.map((r, i) => `${i + 1}. ${r.nombre} → Stock: ${r.stockActual} uds (mín: ${r.stockMinimo})`).join('\n');
    iqShowAnswer(answerId, text);
}

/*** p4: proveedor que vende más caro — promedio de preciocompra por proveedor. @author RADJ */
function iqAnalizarProveedorCaro(data, answerId) {
    const provStats = {};
    data.productos.filter(p => p.idProveedor).forEach(p => {
        if (!provStats[p.idProveedor]) provStats[p.idProveedor] = { total: 0, count: 0 };
        provStats[p.idProveedor].total += p.precioCompra || 0;
        provStats[p.idProveedor].count++;
    });

    const provMap = {};
    data.proveedores.forEach(p => { provMap[p.id] = p; });

    const ranking = Object.entries(provStats)
        .map(([id, stats]) => ({
            nombre: provMap[id]?.nombre || `Proveedor #${id}`,
            promedio: stats.total / stats.count
        }))
        .sort((a, b) => b.promedio - a.promedio)
        .slice(0, 3);

    if (ranking.length === 0) {
        iqShowEmpty(answerId, 'No hay proveedores con productos asignados.');
        return;
    }

    const text = ranking.map((r, i) => `${i + 1}. ${r.nombre} → Promedio: $${Math.round(r.promedio).toLocaleString()}`).join('\n');
    iqShowAnswer(answerId, text);
}

/*** p5: clientes que más compran — total comprado en el periodo. @author RADJ */
function iqAnalizarTopClientes(data, ventasFiltradas, answerId) {
    const compraPorCliente = {};
    ventasFiltradas.forEach(v => {
        if (!v.idCliente) return;
        compraPorCliente[v.idCliente] = (compraPorCliente[v.idCliente] || 0) + (v.valorTotal || 0);
    });

    const cliMap = {};
    data.clientes.forEach(c => { cliMap[c.id] = c; });

    const ranking = Object.entries(compraPorCliente)
        .map(([id, total]) => ({ nombre: cliMap[id]?.nombre || `Cliente #${id}`, total }))
        .sort((a, b) => b.total - a.total)
        .slice(0, 3);

    if (ranking.length === 0) {
        iqShowEmpty(answerId, 'No hay ventas con cliente asignado en este periodo.');
        return;
    }

    const text = ranking.map((r, i) => `${i + 1}. ${r.nombre} → Total: $${Math.round(r.total).toLocaleString()}`).join('\n');
    iqShowAnswer(answerId, text);
}

/*** p6: mes con mayores ganancias — análisis histórico sin filtro de mes. @author RADJ */
function iqAnalizarMejorMes(data, answerId) {
    const prodMap = {};
    data.productos.forEach(p => { prodMap[p.id] = p; });

    const gananciasPorMes = {};
    const monthNames = ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
        'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'];

    data.ventas.forEach(v => {
        if (!v.fechaHora || !v.detalles) return;
        const fecha = new Date(v.fechaHora);
        const key = `${fecha.getFullYear()}-${String(fecha.getMonth() + 1).padStart(2, '0')}`;
        const label = `${monthNames[fecha.getMonth()]} ${fecha.getFullYear()}`;

        if (!gananciasPorMes[key]) gananciasPorMes[key] = { label, ganancia: 0 };

        v.detalles.forEach(d => {
            const prod = prodMap[d.idProducto];
            const costo = prod ? (prod.precioCompra || 0) : 0;
            gananciasPorMes[key].ganancia += (d.precioUnitario - costo) * d.cantidad;
        });
    });

    const ranking = Object.values(gananciasPorMes)
        .sort((a, b) => b.ganancia - a.ganancia)
        .slice(0, 3);

    if (ranking.length === 0) {
        iqShowEmpty(answerId, 'No hay historial de ventas para analizar.');
        return;
    }

    const text = ranking.map((r, i) => `${i + 1}. ${r.label} → Ganancia: $${Math.round(r.ganancia).toLocaleString()}`).join('\n');
    iqShowAnswer(answerId, text);
}

/*** p7: productos generando pérdidas — precioventa < preciocompra. @author RADJ */
function iqAnalizarPerdidas(data, answerId) {
    const ranking = data.productos
        .filter(p => p.estado === 1 && p.precioCompra > 0 && p.precioVenta < p.precioCompra)
        .map(p => ({
            nombre: p.nombre,
            perdida: p.precioVenta - p.precioCompra
        }))
        .sort((a, b) => a.perdida - b.perdida)
        .slice(0, 3);

    if (ranking.length === 0) {
        iqShowEmpty(answerId, '¡Bien! Ningún producto tiene precio de venta inferior al costo.');
        return;
    }

    const text = ranking.map((r, i) => `${i + 1}. ${r.nombre} → Pérdida: $${Math.round(Math.abs(r.perdida)).toLocaleString()} por unidad`).join('\n');
    iqShowAnswer(answerId, text);
}

/*** p8: productos que llevan más tiempo sin venderse — activos sin presencia en ventas del periodo. @author RADJ */
function iqAnalizarSinVender(data, ventasFiltradas, answerId) {
    const vendidos = new Set();
    ventasFiltradas.forEach(v => {
        if (!v.detalles) return;
        v.detalles.forEach(d => vendidos.add(d.idProducto));
    });

    const sinVender = data.productos
        .filter(p => p.estado === 1 && !vendidos.has(p.id))
        .slice(0, 3);

    if (sinVender.length === 0) {
        iqShowEmpty(answerId, '¡Excelente! Todos los productos activos tuvieron ventas en este periodo.');
        return;
    }

    const text = sinVender.map((p, i) => `${i + 1}. ${p.nombre} → Stock: ${p.stockActual} uds sin movimiento`).join('\n');
    iqShowAnswer(answerId, text);
}

/*** p9: productos próximos a vencerse — 4 productos con menor tiempo para vencerse, ignorando los que no tienen fecha. @author RADJ */
function iqAnalizarProximosVencer(data, answerId) {
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const conFecha = data.productos.filter(p => {
        if (!p.fechaVencimiento) return false;
        const expDate = new Date(p.fechaVencimiento);
        return !isNaN(expDate.getTime());
    });

    if (conFecha.length === 0) {
        iqShowEmpty(answerId, 'No hay productos con fecha de vencimiento registrada.');
        return;
    }

    // Calcular diferencia en días y mapear
    const calculados = conFecha.map(p => {
        const expDate = new Date(p.fechaVencimiento);
        const diffTime = expDate - today;
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
        return { nombre: p.nombre, fecha: p.fechaVencimiento, dias: diffDays };
    });

    // Ordenar de menor a mayor días restantes
    calculados.sort((a, b) => a.dias - b.dias);

    // Obtener los 4 primeros productos
    const ranking = calculados.slice(0, 4);

    const text = ranking.map((r, i) => {
        let label = '';
        if (r.dias < 0) {
            label = `(Vencido hace ${Math.abs(r.dias)}d)`;
        } else if (r.dias === 0) {
            label = `(Vence HOY)`;
        } else if (r.dias === 1) {
            label = `(Vence Mañana)`;
        } else {
            label = `(Vence en ${r.dias}d)`;
        }
        return `${i + 1}. ${r.nombre} → ${r.fecha} ${label}`;
    }).join('\n');

    iqShowAnswer(answerId, text);
}
