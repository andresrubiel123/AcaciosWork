/*** módulo javascript del frontend de acacioswork. @author RADJ */
/*** grafico-categorias.js - renderizado del gráfico de unidades vendidas por categoría de producto. @author RADJ */

/*** nombres de meses en español para el resumen. @author RADJ */
const NOMBRES_MESES_CAT = [
    '', 'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
    'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'
];

/*** extrae { ano, mes } de un string de fecha sin depender de la zona horaria. @param {string} fechastr @returns {{ ano: number, mes: number } | null}. @author RADJ */
function parseFechaCategoria(fechaStr) {
    if (!fechaStr) return null;
    /*** tomar solo "yyyy-mm-dd" (primeros 10 chars) y partir. @author RADJ */
    const partes = String(fechaStr).substring(0, 10).split('-');
    if (partes.length < 3) return null;
    const ano = parseInt(partes[0], 10);
    const mes = parseInt(partes[1], 10);
    if (isNaN(ano) || isNaN(mes)) return null;
    return { ano, mes };
}

/*** obtiene los valores actuales del filtro (mes dropdown + año input texto). @author RADJ @returns {{ mes: number, ano: number }} */
function getCatFiltros() {
    const selMes = document.getElementById('catFilterMes');
    const inputAno = document.getElementById('catFilterAno');
    return {
        mes: parseInt(selMes?.value || (new Date().getMonth() + 1), 10),
        ano: parseInt(inputAno?.value || new Date().getFullYear(), 10)
    };
}

/*** actualiza el texto del resumen con el total de unidades y ganancia del período. @author RADJ @param {number} mes @param {number} ano @param {number} totalunidades @param {number} totalganancia */
function actualizarResumenCat(mes, ano, totalUnidades, totalGanancia) {
    const el = document.getElementById('catResumenTexto');
    if (!el) return;
    const und = totalUnidades.toLocaleString('es-CO');
    const gan = totalGanancia.toLocaleString('es-CO', { minimumFractionDigits: 0, maximumFractionDigits: 0 });
    el.innerHTML =
        `Total Unidades Vendidas en ${NOMBRES_MESES_CAT[mes]} ${ano} = <strong>${und} Unidades</strong>` +
        `&nbsp;&nbsp;|&nbsp;&nbsp;Ganancia Total: <strong style="color:#34d399;">$ ${gan}</strong>`;
}

/*** filtra las ventas por mes y año seleccionados, agrupa unidades por categoría, ordena de mayor a menor y redibuja el gráfico de barras horizontales. @author RADJ */
window.renderCatChart = function() {
    const canvas = document.getElementById('categoryChart');
    const wrapper = document.getElementById('categoryChartWrapper');
    if (!canvas || !wrapper) return;

    const { mes, ano } = getCatFiltros();
    if (isNaN(mes) || isNaN(ano) || ano < 2000 || ano > 2100) return;

    const ventas = AppState.cache.catVentasCache || [];
    const prodMap = AppState.cache.catProductosCache || {};

    /*** inicializar acumulador por categoría. @author RADJ */
    const catData = {};
    (AppState.cache.categorias || []).forEach(c => {
        catData[c.id] = { nombre: c.nombre, unidades: 0, ganancia: 0 };
    });

    /*** filtrar ventas del período seleccionado. @author RADJ */
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

    /*** filtrar categorías con ventas y ordenar de mayor a menor. @author RADJ */
    const sorted = Object.values(catData)
        .filter(c => c.unidades > 0)
        .sort((a, b) => b.unidades - a.unidades);

    /*** calcular totales para el resumen. @author RADJ */
    const totalUnidades = sorted.reduce((sum, c) => sum + c.unidades, 0);
    const totalGanancia = sorted.reduce((sum, c) => sum + c.ganancia, 0);
    actualizarResumenCat(mes, ano, totalUnidades, totalGanancia);

    /*** si no hay datos: mostrar mensaje y destruir gráfico anterior. @author RADJ */
    if (sorted.length === 0) {
        if (AppState.categoryChartInstance) {
            AppState.categoryChartInstance.destroy();
            AppState.categoryChartInstance = null;
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

    /*** limpiar mensaje "sin datos" y restaurar canvas. @author RADJ */
    const oldMsg = wrapper.querySelector('.cat-no-data');
    if (oldMsg) oldMsg.remove();
    canvas.style.display = '';

    const labels    = sorted.map(c => c.nombre);
    const values    = sorted.map(c => c.unidades);
    const ganancias = sorted.map(c => c.ganancia);

    /*** ajustar altura del canvas al número de categorías. @author RADJ */
    const barHeight = 44;
    const chartHeight = Math.max(220, labels.length * barHeight + 60);
    wrapper.style.height = chartHeight + 'px';

    /*** paleta de colores para las barras. @author RADJ */
    const palette = [
        '#6366f1', '#8b5cf6', '#a78bfa',
        '#3b82f6', '#0ea5e9', '#06b6d4',
        '#10b981', '#34d399', '#f59e0b',
        '#f97316', '#ef4444', '#ec4899'
    ];
    const backgroundColors = labels.map((_, i) => palette[i % palette.length] + 'cc');
    const borderColors    = labels.map((_, i) => palette[i % palette.length]);

    /*** destruir la instancia previa si existe. @author RADJ */
    if (AppState.categoryChartInstance) {
        AppState.categoryChartInstance.destroy();
    }

    AppState.categoryChartInstance = new Chart(canvas, {
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
                    ctx.font = 'bold 11px Inter, sans-serif';
                    ctx.fillStyle = '#94a3b8';
                    ctx.textAlign = 'left';
                    ctx.textBaseline = 'middle';
                    const undLabel = und + ' und.  ';
                    ctx.fillText(undLabel, x, y);
                    const undWidth = ctx.measureText(undLabel).width;
                    ctx.fillStyle = '#34d399';
                    ctx.fillText('$ ' + gan + ' Ganancia', x + undWidth, y);
                });
                ctx.restore();
            }
        }]
    });
};

/*** carga datos de ventas, productos y categorías desde la api, inicializa los filtros con el período actual y renderiza el gráfico. @author RADJ */
window.loadCategoriasChart = async function() {
    const canvas = document.getElementById('categoryChart');
    if (!canvas) return;

    try {
        const [ventas, productos, categorias] = await Promise.all([
            apiRequest('/ventas')    || [],
            apiRequest('/productos') || [],
            apiRequest('/categorias')|| []
        ]);

        const prodMap = {};
        productos.forEach(p => { prodMap[p.id] = p; });

        /*** guardar en caché para reutilizar sin re-fetch al cambiar filtros. @author RADJ */
        AppState.cache.catVentasCache     = ventas;
        AppState.cache.catProductosCache  = prodMap;
        AppState.cache.categorias         = categorias;

        /*** ajustar valor por defecto: mes y año actuales. @author RADJ */
        const now = new Date();
        const selMes   = document.getElementById('catFilterMes');
        const inputAno = document.getElementById('catFilterAno');
        if (selMes)   selMes.value   = String(now.getMonth() + 1);
        if (inputAno && !inputAno.value) {
            inputAno.value = String(now.getFullYear());
        }

        /*** escuchar cambios en los filtros para redibujar. @author RADJ */
        if (selMes && !selMes.dataset.listenerAttached) {
            selMes.addEventListener('change', window.renderCatChart);
            selMes.dataset.listenerAttached = '1';
        }
        if (inputAno && !inputAno.dataset.listenerAttached) {
            inputAno.addEventListener('change', window.renderCatChart);
            inputAno.dataset.listenerAttached = '1';
        }

        /*** primer render con el período por defecto. @author RADJ */
        window.renderCatChart();

    } catch (err) {
        console.error('Error al cargar gráfico de categorías:', err);
    }
};

/*** Activar modo edición manual al hacer doble clic. @author RADJ */
window.enableManualCatYearInput = function() {
    const select = document.getElementById('catFilterAno');
    const input = document.getElementById('catFilterAno-input');
    if (select && input) {
        input.value = ''; // Queda totalmente limpio para escribir inmediatamente. @author RADJ
        select.style.display = 'none';
        input.style.display = 'inline-block';
        input.focus();
    }
};

/*** Desactivar modo edición manual y aplicar el año seleccionado. @author RADJ */
window.disableManualCatYearInput = function(val) {
    const select = document.getElementById('catFilterAno');
    const input = document.getElementById('catFilterAno-input');
    if (select && input) {
        const yearNum = parseInt(val);
        if (!isNaN(yearNum) && yearNum >= 2000 && yearNum <= 2100) {
            // Buscar si ya existe la opción en el select
            let exists = false;
            for (let i = 0; i < select.options.length; i++) {
                if (parseInt(select.options[i].value) === yearNum) {
                    exists = true;
                    select.selectedIndex = i;
                    break;
                }
            }
            // Si no existe, agregarla dinámicamente
            if (!exists) {
                const newOpt = document.createElement('option');
                newOpt.value = yearNum.toString();
                newOpt.textContent = yearNum.toString();
                select.appendChild(newOpt);
                select.value = yearNum.toString();
            }
            
            // Recargar gráfico
            window.onCatYearChange(yearNum);
        }
        input.style.display = 'none';
        select.style.display = 'inline-block';
    }
};

/*** Escucha el cambio de año y actualiza el gráfico. @author RADJ */
window.onCatYearChange = function(year) {
    window.renderCatChart();
};
