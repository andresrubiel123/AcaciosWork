/** Gráfico de Barras Horizontales: Unidades Vendidas por Categoría de Producto. @author RADJ */

let categoryChartInstance = null;
let _catVentasCache = null;
let _catProductosCache = null;
let _catCategoriasCache = null;

/** Nombres de meses en español para el resumen. @author RADJ */
const NOMBRES_MESES_CAT = [
    '', 'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
    'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'
];

/**
 * Extrae { ano, mes } de un string de fecha sin depender de la zona horaria.
 * El campo en el modelo se llama "fechaHora" y llega como "2025-05-15T10:30:00". @author RADJ
 * @param {string} fechaStr
 * @returns {{ ano: number, mes: number } | null}
 */
function parseFechaCategoria(fechaStr) {
    if (!fechaStr) return null;
    /** Tomar solo "YYYY-MM-DD" (primeros 10 chars) y partir. @author RADJ */
    const partes = String(fechaStr).substring(0, 10).split('-');
    if (partes.length < 3) return null;
    const ano = parseInt(partes[0], 10);
    const mes = parseInt(partes[1], 10);
    if (isNaN(ano) || isNaN(mes)) return null;
    return { ano, mes };
}

/**
 * Obtiene los valores actuales del filtro (mes dropdown + año input texto). @author RADJ
 * @returns {{ mes: number, ano: number }}
 */
function getCatFiltros() {
    const selMes = document.getElementById('catFilterMes');
    const inputAno = document.getElementById('catFilterAno');
    return {
        mes: parseInt(selMes?.value || (new Date().getMonth() + 1), 10),
        ano: parseInt(inputAno?.value || new Date().getFullYear(), 10)
    };
}

/**
 * Actualiza el texto del resumen con el total de unidades y ganancia del período. @author RADJ
 * @param {number} mes
 * @param {number} ano
 * @param {number} totalUnidades
 * @param {number} totalGanancia
 */
function actualizarResumenCat(mes, ano, totalUnidades, totalGanancia) {
    const el = document.getElementById('catResumenTexto');
    if (!el) return;
    const und = totalUnidades.toLocaleString('es-CO');
    const gan = totalGanancia.toLocaleString('es-CO', { minimumFractionDigits: 0, maximumFractionDigits: 0 });
    el.innerHTML =
        `Total Unidades Vendidas en ${NOMBRES_MESES_CAT[mes]} ${ano} = <strong>${und} Unidades</strong>` +
        `&nbsp;&nbsp;|&nbsp;&nbsp;Ganancia Total: <strong style="color:#34d399;">$ ${gan}</strong>`;
}

/**
 * Filtra las ventas por mes y año seleccionados, agrupa unidades por categoría,
 * ordena de mayor a menor y redibuja el gráfico de barras horizontales. @author RADJ
 */
function renderCatChart() {
    const canvas = document.getElementById('categoryChart');
    const wrapper = document.getElementById('categoryChartWrapper');
    if (!canvas || !wrapper) return;

    const { mes, ano } = getCatFiltros();
    if (isNaN(mes) || isNaN(ano) || ano < 2000 || ano > 2100) return;

    const ventas = _catVentasCache || [];
    const prodMap = _catProductosCache || {};

    /** Inicializar acumulador por categoría. @author RADJ */
    const catData = {};
    (_catCategoriasCache || []).forEach(c => {
        catData[c.id] = { nombre: c.nombre, unidades: 0, ganancia: 0 };
    });

    /**
     * Filtrar ventas del período seleccionado.
     * El campo de fecha en el JSON es "fechaHora" (LocalDateTime → "2025-05-15T10:30:00"). @author RADJ
     */
    ventas.forEach(v => {
        const parsed = parseFechaCategoria(v.fechaHora);
        if (!parsed) return;
        if (parsed.mes !== mes || parsed.ano !== ano) return;

        (v.detalles || []).forEach(d => {
            const prod = prodMap[d.idProducto];
            if (!prod) return;
            const cid = prod.idCategoria;
            if (!cid) return;
            if (!catData[cid]) {
                catData[cid] = { nombre: 'Categoría #' + cid, unidades: 0, ganancia: 0 };
            }
            const cantidad    = d.cantidad      || 0;
            const precioVenta = d.precioUnitario || 0;
            const precioCompra = prod.precioCompra || 0;
            catData[cid].unidades += cantidad;
            catData[cid].ganancia += (precioVenta - precioCompra) * cantidad;
        });
    });

    /** Filtrar categorías con ventas y ordenar de mayor a menor. @author RADJ */
    const sorted = Object.values(catData)
        .filter(c => c.unidades > 0)
        .sort((a, b) => b.unidades - a.unidades);

    /** Calcular totales para el resumen. @author RADJ */
    const totalUnidades = sorted.reduce((sum, c) => sum + c.unidades, 0);
    const totalGanancia = sorted.reduce((sum, c) => sum + c.ganancia, 0);
    actualizarResumenCat(mes, ano, totalUnidades, totalGanancia);

    /** Si no hay datos: mostrar mensaje y destruir gráfico anterior. @author RADJ */
    if (sorted.length === 0) {
        if (categoryChartInstance) {
            categoryChartInstance.destroy();
            categoryChartInstance = null;
        }
        wrapper.style.height = 'auto';
        canvas.style.display = 'none';
        let msg = wrapper.querySelector('.cat-no-data');
        if (!msg) {
            msg = document.createElement('p');
            msg.className = 'cat-no-data';
            msg.style.cssText = 'color:var(--text-muted);text-align:center;padding:2rem;font-family:Inter,sans-serif;margin:0;';
            wrapper.appendChild(msg);
        }
        msg.textContent = `Sin datos de ventas para ${NOMBRES_MESES_CAT[mes]} ${ano}.`;
        return;
    }

    /** Limpiar mensaje "sin datos" y restaurar canvas. @author RADJ */
    const oldMsg = wrapper.querySelector('.cat-no-data');
    if (oldMsg) oldMsg.remove();
    canvas.style.display = '';

    const labels    = sorted.map(c => c.nombre);
    const values    = sorted.map(c => c.unidades);
    const ganancias = sorted.map(c => c.ganancia);

    /** Ajustar altura del canvas al número de categorías. @author RADJ */
    const barHeight = 44;
    const chartHeight = Math.max(220, labels.length * barHeight + 60);
    wrapper.style.height = chartHeight + 'px';

    /** Paleta de colores para las barras. @author RADJ */
    const palette = [
        '#6366f1', '#8b5cf6', '#a78bfa',
        '#3b82f6', '#0ea5e9', '#06b6d4',
        '#10b981', '#34d399', '#f59e0b',
        '#f97316', '#ef4444', '#ec4899'
    ];
    const backgroundColors = labels.map((_, i) => palette[i % palette.length] + 'cc');
    const borderColors    = labels.map((_, i) => palette[i % palette.length]);

    /** Destruir instancia previa si existe. @author RADJ */
    if (categoryChartInstance) {
        categoryChartInstance.destroy();
    }

    categoryChartInstance = new Chart(canvas, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Unidades Vendidas',
                data: values,
                backgroundColor: backgroundColors,
                borderColor: borderColors,
                borderWidth: 1.5,
                borderRadius: 6,
                borderSkipped: false
            }]
        },
        options: {
            indexAxis: 'y',
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false },
                tooltip: {
                    backgroundColor: '#1e293b',
                    titleColor: '#f8fafc',
                    bodyColor: '#94a3b8',
                    borderColor: 'rgba(255,255,255,0.1)',
                    borderWidth: 1,
                    padding: 10,
                    callbacks: {
                        label: ctx => '  ' + ctx.parsed.x.toLocaleString('es-CO') + ' und.'
                    }
                }
            },
            layout: { padding: { right: 200 } },
            scales: {
                x: {
                    beginAtZero: true,
                    grid: { color: 'rgba(255,255,255,0.05)' },
                    ticks: {
                        color: '#94a3b8',
                        font: { family: 'Inter', size: 11 },
                        padding: 6,
                        callback: v => v.toLocaleString('es-CO')
                    },
                    title: {
                        display: true,
                        text: 'Unidades vendidas',
                        color: '#64748b',
                        font: { family: 'Inter', size: 11 }
                    }
                },
                y: {
                    grid: { display: false },
                    ticks: {
                        color: '#f8fafc',
                        font: { family: 'Inter', size: 12, weight: '600' },
                        padding: 10
                    }
                }
            }
        },
        plugins: [{
            /**
             * Plugin inline: dibuja etiquetas al final de cada barra con unidades y ganancia. @author RADJ
             * Formato: "200 und.  $ 200.000 Ganancia"
             */
            id: 'categoryBarLabels',
            afterDatasetsDraw(chart) {
                const { ctx } = chart;
                ctx.save();
                chart.getDatasetMeta(0).data.forEach((bar, i) => {
                    const und = values[i].toLocaleString('es-CO');
                    const gan = ganancias[i].toLocaleString('es-CO', {
                        minimumFractionDigits: 0, maximumFractionDigits: 0
                    });
                    const x = bar.x + 8;
                    const y = bar.y;
                    /** Unidades en gris claro. @author RADJ */
                    ctx.font = 'bold 11px Inter, sans-serif';
                    ctx.fillStyle = '#94a3b8';
                    ctx.textAlign = 'left';
                    ctx.textBaseline = 'middle';
                    const undLabel = und + ' und.  ';
                    ctx.fillText(undLabel, x, y);
                    /** Ganancia en verde. @author RADJ */
                    const undWidth = ctx.measureText(undLabel).width;
                    ctx.fillStyle = '#34d399';
                    ctx.fillText('$ ' + gan + ' Ganancia', x + undWidth, y);
                });
                ctx.restore();
            }
        }]
    });
}

/**
 * Carga datos de ventas, productos y categorías desde la API,
 * inicializa los filtros con el período actual y renderiza el gráfico. @author RADJ
 */
async function loadCategoriasChart() {
    const canvas = document.getElementById('categoryChart');
    if (!canvas) return;

    try {
        const [ventas, productos, categorias] = await Promise.all([
            apiRequest('/ventas')    || [],
            apiRequest('/productos') || [],
            apiRequest('/categorias')|| []
        ]);

        /** Construir mapa de productos por ID para acceso O(1). @author RADJ */
        const prodMap = {};
        productos.forEach(p => { prodMap[p.id] = p; });

        /** Guardar en caché para reutilizar sin re-fetch al cambiar filtros. @author RADJ */
        _catVentasCache     = ventas;
        _catProductosCache  = prodMap;
        _catCategoriasCache = categorias;

        /** Ajustar valor por defecto: mes y año actuales. @author RADJ */
        const now = new Date();
        const selMes   = document.getElementById('catFilterMes');
        const inputAno = document.getElementById('catFilterAno');
        if (selMes)   selMes.value   = String(now.getMonth() + 1);
        if (inputAno) inputAno.value = String(now.getFullYear());

        /** Escuchar cambios en los filtros para redibujar. @author RADJ */
        if (selMes && !selMes.dataset.listenerAttached) {
            selMes.addEventListener('change', renderCatChart);
            selMes.dataset.listenerAttached = '1';
        }
        if (inputAno && !inputAno.dataset.listenerAttached) {
            /** Redibujar al presionar Enter o al perder el foco. @author RADJ */
            inputAno.addEventListener('change', renderCatChart);
            inputAno.addEventListener('keydown', e => { if (e.key === 'Enter') renderCatChart(); });
            inputAno.dataset.listenerAttached = '1';
        }

        /** Primer render con el período por defecto. @author RADJ */
        renderCatChart();

    } catch (err) {
        console.error('Error al cargar gráfico de categorías:', err);
    }
}
