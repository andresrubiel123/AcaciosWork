/*** módulo javascript del frontend de acacioswork. @author RADJ */
/*** dashboard.js - controlador principal de la interfaz y flujo de navegación. @author RADJ */

/*** inicializar caché de buscadores activos en appstate. @author RADJ */
if (!AppState.cache.buscadoresActivos) {
    AppState.cache.buscadoresActivos = {};
}

/*** función de filtrado en tiempo real para las tablas delegada en el buscador común. @author RADJ */
window.filterTable = function(inputElement, tbodyId) {
    if (!inputElement) return;
    const inputId = inputElement.id;
    if (!inputId) return;

    const key = `${inputId}_${tbodyId}`;
    if (!AppState.cache.buscadoresActivos[key]) {
        /*** determinar tipo de entidad basado en el id del tbody. @author RADJ */
        let entidad = 'productos';
        if (tbodyId.includes('cli')) {
            entidad = 'clientes';
        } else if (tbodyId.includes('prov')) {
            entidad = 'proveedores';
        } else if (tbodyId.includes('usr')) {
            entidad = 'usuarios';
        } else if (tbodyId.includes('alertas')) {
            entidad = 'productos';
        }

        AppState.cache.buscadoresActivos[key] = new Buscador({
            entidad: entidad,
            inputId: inputId,
            tbodyId: tbodyId,
            debounceMs: 0,
            onSearchResult: (results) => {
                const handler = window._paginationScrollHandlers && window._paginationScrollHandlers[tbodyId];
                if (handler) {
                    handler.updateSearch(results);
                } else {
                    if (AppState.cache.buscadoresActivos[key]) {
                        AppState.cache.buscadoresActivos[key].filtrarTablaHTML(inputElement.value.toLowerCase().trim());
                    }
                }
            }
        });
    }

    /*** ejecutar búsqueda unificada. @author RADJ */
    AppState.cache.buscadoresActivos[key].realizarBusqueda(inputElement.value);
};

/*** carga las referencias de la base de datos necesarias para poblar selects. @author RADJ */
window.loadReferences = async function() {
    try {
        /*** obtener categorías, proveedores, documentos y roles desde la api. @author RADJ */
        AppState.cache.categorias = await apiRequest('/categorias') || [];
        AppState.cache.proveedores = await apiRequest('/proveedores') || [];
        AppState.cache.tiposDocumento = await apiRequest('/tipos-documentos') || [];
        AppState.cache.roles = await apiRequest('/roles') || [];
    } catch (e) {
        console.error("Error al cargar referencias de base de datos:", e);
    }
};

/*** control de navegación entre secciones del dashboard. @author RADJ */
window.showSection = function(name, btn) {
    /*** limpiar inputs de búsqueda al cambiar de sección. @author RADJ */
    const searchInputs = ['inv-search-input', 'home-inv-search-input', 'prov-search-input', 'cli-search-input', 'usr-search-input', 'alertas-search-input', 'product-search'];
    searchInputs.forEach(id => {
        const el = document.getElementById(id);
        if (el) el.value = '';
    });

    /*** ocultar todas las secciones del panel. @author RADJ */
    document.querySelectorAll('.section').forEach(s => s.style.display = 'none');
    /*** remover la clase activa de todos los botones de la barra. @author RADJ */
    document.querySelectorAll('.toolbar-btn').forEach(b => b.classList.remove('active'));
    /*** mostrar la sección seleccionada y activar su botón correspondiente. @author RADJ */
    const targetSection = document.getElementById('sec-' + name);
    if (targetSection) targetSection.style.display = 'block';
    if (btn) btn.classList.add('active');

    /*** invocar carga de datos específica según la sección de destino. @author RADJ */
    if (name === 'welcome') loadStats();
    if (name === 'inventario') {
        if (window.loadInventario) window.loadInventario();
    }
    if (name === 'proveedores') {
        if (window.loadProveedores) window.loadProveedores();
    }
    if (name === 'clientes') {
        if (window.loadClientes) window.loadClientes();
    }
    if (name === 'usuarios') {
        if (window.loadUsuarios) window.loadUsuarios();
    }
    if (name === 'alertas') {
        if (window.loadAlertas) window.loadAlertas();
    }
    if (name === 'vender') {
        if (window.loadVenderSection) window.loadVenderSection();
    }
    if (name === 'reportes') {
        if (window.initReportes) window.initReportes();
    }
    if (name === 'graficos') {
        /** Cargar gráficos estadísticos en la nueva sección dedicada. @author RADJ */
        if (window.loadReportesChart) window.loadReportesChart();
        if (window.loadCategoriasChart) window.loadCategoriasChart();
    }
    if (name === 'historial') {
        if (window.loadHistorial) window.loadHistorial();
    }
    if (name === 'preguntas-inteligentes') {
        if (window.loadPreguntasInteligentes) window.loadPreguntasInteligentes();
    }
};

/*** actualiza la interfaz gráfica de las tarjetas de estadísticas con los productos proporcionados. @author RADJ */
window.updateStatsUI = function(products) {
    const total = products.length;
    const bajo = products.filter(p => p.stockActual <= (p.stockMinimo || 5)).length;

    // Calcular productos próximos a vencer o ya vencidos (<= 5 días)
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const vencimiento = products.filter(p => {
        if (!p.fechaVencimiento) return false;
        const expDate = new Date(p.fechaVencimiento);
        if (isNaN(expDate.getTime())) return false;
        const diffTime = expDate - today;
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
        return diffDays <= 5;
    }).length;

    const valor = products.reduce((a, p) => a + (p.stockActual * p.precioVenta), 0);
    const valorCosto = products.reduce((a, p) => a + (p.stockActual * (p.precioCompra || 0)), 0);
    const ganancia = valor - valorCosto;

    const totalEl = document.getElementById('inv-total');
    if (totalEl) totalEl.textContent = total;

    const bajoEl = document.getElementById('inv-bajo');
    if (bajoEl) bajoEl.textContent = bajo;

    const vencimientoEl = document.getElementById('inv-vencimiento');
    if (vencimientoEl) vencimientoEl.textContent = vencimiento;

    const btnAlertas = document.getElementById('btn-alertas');
    if (btnAlertas) {
        if (bajo > 0 || vencimiento > 0) {
            btnAlertas.classList.add('pulsing');
        } else {
            btnAlertas.classList.remove('pulsing');
        }
    }

    const valorEl = document.getElementById('inv-valor');
    if (valorEl) valorEl.textContent = '$' + valor.toLocaleString();

    const costoEl = document.getElementById('inv-costo');
    if (costoEl) costoEl.textContent = '$' + valorCosto.toLocaleString();

    const gananciaEl = document.getElementById('inv-ganancia');
    if (gananciaEl) {
        gananciaEl.textContent = '$' + ganancia.toLocaleString();
        if (ganancia >= 0) {
            gananciaEl.style.color = '#10b981';
        } else {
            gananciaEl.style.color = '#ef4444';
        }
    }
};

/*** carga y visualización de estadísticas globales en la ventana de inicio. @author RADJ */
window.loadStats = async function() {
    try {
        const products = await apiRequest('/productos') || [];
        window.updateStatsUI(products);
        renderHomeProductsTable(products);
    } catch (e) {
        console.error("Error al cargar estadísticas en inicio:", e);
    }
};

/*** renderiza la tabla simplificada de productos en la sección de inicio (resumen de 10 productos ordenados por porcentaje de stock de menor a mayor). @author RADJ */
function renderHomeProductsTable(products) {
    const tbody = document.getElementById('home-inv-tbody');
    if (!tbody) return;
    try {
        if (!products || !products.length) {
            tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;padding:2rem;color:var(--text-muted)">Sin productos registrados.</td></tr>';
            return;
        }

        /*** calcular porcentaje real de stock relativo al nivel óptimo @author RADJ */
        const getPct = (p) => {
            const actual = p.stockActual !== undefined ? p.stockActual : 0;
            const optimo = p.stockOptimo ? p.stockOptimo : 200;
            return optimo > 0 ? (actual / optimo) * 100 : 0;
        };

        /*** 1. 5 productos con menor porcentaje de stock (los más bajos / críticos) @author RADJ */
        const sortedAsc = [...products].sort((a, b) => getPct(a) - getPct(b));
        const lowest5 = sortedAsc.slice(0, 5);

        /*** 2. 5 productos con mayor porcentaje de stock (los más altos, evitando duplicados) @author RADJ */
        const sortedDesc = [...products].sort((a, b) => getPct(b) - getPct(a));
        const highest5 = sortedDesc.filter(p => !lowest5.some(l => l.id === p.id)).slice(0, 5);

        /*** 3. lista combinada ordenada estrictamente de menor a mayor porcentaje (%) @author RADJ */
        const summaryProducts = [...lowest5, ...highest5].sort((a, b) => getPct(a) - getPct(b));

        tbody.innerHTML = summaryProducts.map(p => {
            const stockActual = p.stockActual !== undefined ? p.stockActual : 0;
            const stockMinimo = p.stockMinimo !== undefined ? p.stockMinimo : 5;
            const stockOptimo = p.stockOptimo ? p.stockOptimo : 200;

            const pct = stockOptimo > 0 ? Math.round((stockActual / stockOptimo) * 100) : 0;
            let colorClass = 'green';
            if (pct <= 30) {
                colorClass = 'red';
            } else if (pct <= 69) {
                colorClass = 'orange';
            }
            const barWidth = Math.min(pct, 100);

            return `
            <tr>
                <td class="col-codigo" style="font-family:monospace;font-size:0.8rem">${p.codigoBarras || 'N/A'}</td>
                <td class="col-nombre" style="font-weight:500" title="${p.nombre}">${p.nombre}</td>
                <td class="col-unidad">${p.unidadMedida || 'Unidad'}</td>
                <td class="col-stock">
                    <div class="stock-bar-wrapper">
                        <div class="stock-bar-info">
                            <span class="stock-bar-qty">${stockActual} / ${stockOptimo} uds</span>
                            <span class="stock-bar-pct ${colorClass}">${pct}%</span>
                        </div>
                        <div class="stock-bar-container">
                            <div class="stock-bar-fill ${colorClass}" style="width: ${barWidth}%"></div>
                        </div>
                    </div>
                </td>
                <td class="col-estado" style="text-align: center;"><span class="badge ${p.estado === 1 ? 'badge-success' : 'badge-danger'}">${p.estado === 1 ? 'Activo' : 'Inactivo'}</span></td>
            </tr>`;
        }).join('');
    } catch (e) {
        tbody.innerHTML = `<tr><td colspan="5" style="text-align:center;padding:2rem;color:#ef4444">Error: ${e.message}</td></tr>`;
    }
}

/*** verificación de autenticación y carga al iniciar la vista. @author RADJ */
document.addEventListener('DOMContentLoaded', async () => {
   
/*** si no estamos en la página de login y no hay token, redirigir. @author RADJ */
    if (!localStorage.getItem('jwt_token')) {
        window.location.href = 'login';
        return;
    }

    const userInfoEl = document.getElementById('userInfo');
    if (userInfoEl) {
        userInfoEl.textContent = '👤 ' + (localStorage.getItem('user_name') || 'Admin');
    }

    /*** cargar configuración global antes de renderizar nada más. @author RADJ */
    if (window.loadConfiguracion) {
        await window.loadConfiguracion();
    }

    /*** cargar listas de referencia iniciales en memoria. @author RADJ */
    await window.loadReferences();

    /*** cargar y mostrar estadísticas iniciales en la ventana de inicio. @author RADJ */
    await window.loadStats();
});

/*** carga y visualización del historial de ventas en el panel administrador. @author RADJ */
window.loadHistorial = async function() {
    const searchInput = document.getElementById('hist-search');
    if (searchInput) searchInput.value = '';
    const tbody = document.getElementById('hist-tbody');
    if (tbody) {
        tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;padding:2rem;color:var(--text-muted);">Cargando...</td></tr>';
    }

    try {
        if (!AppState.cache.clientes || !AppState.cache.clientes.length) {
            AppState.cache.clientes = await apiRequest('/clientes') || [];
        }

        const queryResult = await apiRequest('/ventas') || [];
        const ventas = Array.isArray(queryResult) ? queryResult : [];
        
        const totalVentasEl = document.getElementById('hist-total');
        if (totalVentasEl) totalVentasEl.textContent = ventas.length;

        const totalVal = ventas.reduce((acc, v) => acc + (v.valorTotal || 0), 0);
        const totalValorEl = document.getElementById('hist-valor');
        if (totalValorEl) {
            totalValorEl.textContent = typeof formatCurrency === 'function' ? formatCurrency(totalVal) : '$' + totalVal.toLocaleString();
        }

        if (!ventas.length) {
            if (tbody) {
                tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;padding:2rem;color:var(--text-muted);">Sin ventas registradas.</td></tr>';
            }
            return;
        }

        ventas.sort((a, b) => new Date(b.fechaHora) - new Date(a.fechaHora));

        if (tbody) {
            window.setupTablePagination({
                tbodyId: 'hist-tbody',
                allItems: ventas,
                renderRowFn: (v) => {
                    const fecha = v.fechaHora ? new Date(v.fechaHora).toLocaleString('es-CO') : '—';
                    const nProductos = v.detalles ? v.detalles.length : 0;
                    
                    const client = AppState.cache.clientes.find(c => c.id === v.idCliente);
                    const clienteNombre = client ? client.nombre : (v.idCliente ? `Cliente #${v.idCliente}` : '<span style="color:var(--text-muted)">Sin cliente</span>');
                    
                    const totalFormatted = typeof formatCurrency === 'function' ? formatCurrency(v.valorTotal || 0) : '$' + (v.valorTotal || 0).toLocaleString();
                    return `<tr>
                        <td style="font-family:monospace; font-size:0.8rem; color:var(--text-muted);">#${v.id}</td>
                        <td style="font-size:0.83rem;">${fecha}</td>
                        <td style="font-weight:500; font-size:0.88rem;" title="${clienteNombre}">${clienteNombre}</td>
                        <td><span class="badge" style="background: rgba(99, 102, 241, 0.15); color: #a5b4fc; padding: 0.25rem 0.5rem; border-radius: 0.35rem; font-size: 0.78rem;">📦 ${nProductos} producto${nProductos !== 1 ? 's' : ''}</span></td>
                        <td style="text-align:right; font-weight:700; color:#10b981;">${totalFormatted}</td>
                    </tr>`;
                }
            });
        }
    } catch (e) {
        if (tbody) {
            tbody.innerHTML = `<tr><td colspan="5" style="text-align:center;padding:2rem;color:#ef4444;">Error: ${e.message}</td></tr>`;
        }
    }
};
