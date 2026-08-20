/*** módulo javascript del frontend de acacioswork. @author RADJ */
/*** inventario.js - lógica de negocio e interfaz de movimientos de inventario y alertas. @author RADJ */

/*** Carga los lotes globales para el catálogo de productos. @author RADJ / Antigravity */
window.loadLotesGlobal = async function() {
    try {
        const allLotes = await apiRequest('/lotes') || [];
        AppState.lotesByProducto = {};
        allLotes.forEach(lote => {
            if (!AppState.lotesByProducto[lote.idProducto]) {
                AppState.lotesByProducto[lote.idProducto] = [];
            }
            AppState.lotesByProducto[lote.idProducto].push(lote);
        });
    } catch(e) {
        console.warn("No se pudieron cargar los lotes:", e);
        AppState.lotesByProducto = {};
    }
};

/*** Renderiza la celda de fecha de vencimiento con badge de lotes si existen múltiples. @author RADJ / Antigravity */
window.renderVencimientoCell = function(p) {
    if (!p) return '—';
    const lotes = (AppState.lotesByProducto && AppState.lotesByProducto[p.id])
        ? AppState.lotesByProducto[p.id].filter(l => l.activo && l.cantidadActual > 0)
        : [];

    const fechaText = p.fechaVencimiento || '—';
    const colorStyle = p.fechaVencimiento ? '#a7f3d0' : 'var(--text-muted)';

    if (lotes.length > 1) {
        return `<span style="font-size:0.8rem; color:${colorStyle}; display:inline-flex; align-items:center; gap:6px;">
            ${fechaText}
            <span class="badge-lotes" style="background:#0284c7; color:#ffffff; padding:2px 7px; border-radius:10px; font-size:0.7rem; font-weight:700; cursor:pointer; box-shadow:0 1px 3px rgba(0,0,0,0.3); transition:all 0.2s ease;" onclick="window.toggleLotesPopover(event, ${p.id})" title="Ver ${lotes.length} lotes activos">
                ${lotes.length} lotes
            </span>
        </span>`;
    } else {
        return `<span style="font-size:0.8rem; color:${colorStyle}">${fechaText}</span>`;
    }
};

/*** Despliega un popover flotante con el desglose detallado de los lotes del producto. @author RADJ / Antigravity */
window.toggleLotesPopover = function(event, productId) {
    event.stopPropagation();

    const existing = document.getElementById('lotes-popover');
    if (existing) {
        existing.remove();
        if (existing.dataset.prodId == productId) return;
    }

    const productLotes = (AppState.lotesByProducto && AppState.lotesByProducto[productId])
        ? AppState.lotesByProducto[productId].filter(l => l.activo && l.cantidadActual > 0)
        : [];

    if (productLotes.length === 0) return;

    const popover = document.createElement('div');
    popover.id = 'lotes-popover';
    popover.dataset.prodId = productId;
    popover.style.position = 'absolute';
    popover.style.zIndex = '9999';
    popover.style.background = '#0f172a';
    popover.style.color = '#f8fafc';
    popover.style.border = '1px solid #334155';
    popover.style.borderRadius = '8px';
    popover.style.padding = '0.75rem 1rem';
    popover.style.boxShadow = '0 10px 25px -5px rgba(0, 0, 0, 0.6)';
    popover.style.minWidth = '240px';
    popover.style.fontSize = '0.8rem';

    // Ordenar lotes por vencimiento ascendente (FEFO)
    productLotes.sort((a, b) => new Date(a.fechaVencimiento) - new Date(b.fechaVencimiento));

    let html = `<div style="font-weight:bold; margin-bottom:0.5rem; border-bottom:1px solid #334155; padding-bottom:0.4rem; display:flex; justify-content:space-between; align-items:center;">
        <span style="color:#38bdf8;">📦 Desglose de Lotes (${productLotes.length})</span>
        <span style="cursor:pointer; font-size:1.1rem; color:#94a3b8;" onclick="document.getElementById('lotes-popover').remove()">×</span>
    </div><ul style="list-style:none; padding:0; margin:0;">`;

    productLotes.forEach((lote, index) => {
        const isNearest = index === 0;
        const badgeColor = isNearest ? '#f87171' : '#34d399';
        html += `<li style="margin-bottom:0.4rem; padding:0.4rem 0.5rem; background:rgba(255,255,255,0.04); border:1px solid rgba(255,255,255,0.06); border-radius:6px; display:flex; justify-content:space-between; align-items:center;">
            <div>
                <strong style="color:${badgeColor}">${lote.fechaVencimiento}</strong> ${isNearest ? '<span style="font-size:0.7rem; background:#7f1d1d; color:#fca5a5; padding:1px 4px; border-radius:3px; margin-left:4px;">Próximo</span>' : ''}<br/>
                <span style="font-size:0.72rem; color:#94a3b8; font-family:monospace;">${lote.codigoLote || 'Lote #' + lote.id}</span>
            </div>
            <span style="background:#1e293b; color:#38bdf8; font-size:0.75rem; font-weight:bold; padding:2px 8px; border-radius:12px; border:1px solid #334155;">${lote.cantidadActual} u.</span>
        </li>`;
    });

    html += `</ul>`;
    popover.innerHTML = html;

    document.body.appendChild(popover);

    const rect = event.target.getBoundingClientRect();
    popover.style.left = `${Math.max(10, rect.left + window.scrollX - 80)}px`;
    popover.style.top = `${rect.bottom + window.scrollY + 6}px`;

    setTimeout(() => {
        const closeHandler = (e) => {
            if (!popover.contains(e.target) && e.target !== event.target) {
                popover.remove();
                document.removeEventListener('click', closeHandler);
            }
        };
        document.addEventListener('click', closeHandler);
    }, 10);
};

/*** carga y visualización de productos en inventario. @author RADJ */
window.loadInventario = async function() {
    /*** limpiar input de búsqueda de inventario. @author RADJ */
    const searchInput = document.getElementById('inv-search-input');
    if (searchInput) searchInput.value = '';
    try {
        /*** cargar lotes globales primero. @author RADJ */
        await window.loadLotesGlobal();

        /*** obtener listado de productos desde la api. @author RADJ */
        const products = await apiRequest('/productos') || [];
        AppState.allProducts = products;
        /*** actualizar las tarjetas de estadísticas si la función existe. @author RADJ */
        if (window.updateStatsUI) {
            window.updateStatsUI(products);
        }

        window.renderInventario(products);
    } catch (e) {
        const tbody = document.getElementById('inv-tbody');
        if (tbody) {
            tbody.innerHTML = `<tr><td colspan="11" style="text-align:center;padding:2rem;color:#ef4444">Error: ${e.message}</td></tr>`;
        }
    }
};

/*** renderiza la tabla de productos del inventario usando paginación infinita. @author RADJ */
window.renderInventario = function(products) {
    const tbody = document.getElementById('inv-tbody');
    if (tbody) {
        window.setupTablePagination({
            tbodyId: 'inv-tbody',
            allItems: products,
            renderRowFn: (p) => {
                const stockActual = p.stockActual !== undefined ? p.stockActual : 0;
                const stockMinimo = p.stockMinimo !== undefined ? p.stockMinimo : 5;
                const stockOptimo = p.stockOptimo ? p.stockOptimo : 200;

                /*** calcular el porcentaje de stock respecto al nivel óptimo. @author RADJ */
                const pct = stockOptimo > 0 ? Math.round((stockActual / stockOptimo) * 100) : 0;
                let colorClass = 'green';
                if (pct <= 30) {
                    colorClass = 'red';
                } else if (pct <= 69) {
                    colorClass = 'orange';
                }

                /*** retornar la estructura html de la fila del producto. @author RADJ */
                return `
                <tr>
                    <td class="col-codigo" style="font-family:monospace;font-size:0.8rem">${p.codigoBarras || 'N/A'}</td>
                    <td class="col-nombre" style="font-weight:500" title="${p.nombre}">${p.nombre}</td>
                    <td class="col-unidad">${p.unidadMedida || 'Unidad'}</td>
                    <td class="col-stock">
                        <span class="stock-qty ${colorClass}">${stockActual}</span>
                    </td>
                    <td class="col-precio">$${p.precioCompra !== undefined ? Math.round(p.precioCompra) : '0'}</td>
                    <td class="col-precio">$${p.precioVenta !== undefined ? Math.round(p.precioVenta) : '0'}</td>
                    <td class="col-vencimiento">${window.renderVencimientoCell(p)}</td>
                    <td class="col-iva">${p.iva !== undefined ? Number(p.iva).toFixed(1) : '0.0'}%</td>
                    <td class="col-estado"><span class="badge ${p.estado === 1 ? 'badge-success' : 'badge-danger'}">${p.estado === 1 ? 'Activo' : 'Inactivo'}</span></td>
                    <td class="col-movimientos" style="display:flex;gap:0.4rem;align-items:center;">
                        <button class="btn-sm" style="background:#10b981;color:#fff;border:none;border-radius:0.25rem;cursor:pointer;padding:0.25rem 0.5rem;font-weight:600;font-size:0.8rem" onclick="openMovimientoModal(${p.id}, 'ENTRADA')">Entrada</button>
                        <button class="btn-sm" style="background:#ef4444;color:#fff;border:none;border-radius:0.25rem;cursor:pointer;padding:0.25rem 0.5rem;font-weight:600;font-size:0.8rem" onclick="openMovimientoModal(${p.id}, 'SALIDA')">Salida</button>
                    </td>
                    <td class="col-acciones" style="display:flex;gap:0.4rem">
                        <button class="btn-sm" onclick="editProducto(${p.id})">Editar</button>
                        <button class="btn-sm btn-del" onclick="deleteProducto(${p.id})">Borrar</button>
                    </td>
                </tr>`;
            }
        });
    }
};

/*** abrir el modal de edición para un producto específico. @author RADJ */
window.editProducto = function(id) {
    /*** invocar modal con el contexto de inventario y el id seleccionado. @author RADJ */
    if (window.openModal) {
        window.openModal('inventario', id);
    }
};

/*** solicitar confirmación y eliminar un producto de la base de datos. @author RADJ */
window.deleteProducto = async function(id) {
    /*** mostrar confirmación nativa al usuario antes de proceder. @author RADJ */
    if (!confirm('¿Eliminar este producto?')) return;
    try {
        /*** realizar petición delete al endpoint de productos. @author RADJ */
        await apiRequest(`/productos/${id}`, 'DELETE');
        /*** recargar la tabla de inventario tras la eliminación. @author RADJ */
        loadInventario();
        alert('Producto eliminado con éxito.');
    } catch (e) {
        /*** informar error al usuario si falla la eliminación. @author RADJ */
        alert('Error al eliminar producto: ' + e.message);
    }
};

/*** carga y visualización de alertas críticas de stock y vencimiento. @author RADJ */
window.loadAlertas = async function() {
    /*** limpiar campo de búsqueda en alertas. @author RADJ */
    const searchInput = document.getElementById('alertas-search-input');
    if (searchInput) searchInput.value = '';
    /*** obtener el contenedor principal de alertas de stock. @author RADJ */
    const container = document.getElementById('alertas-container');
    try {
        /*** cargar referencias de base de datos si no existen en memoria. @author RADJ */
        if (!AppState.cache.proveedores || !AppState.cache.proveedores.length) {
            if (window.loadReferences) {
                await window.loadReferences();
            }
        }
        /*** obtener catálogo de productos para analizar stock y vencimientos. @author RADJ */
        const products = await apiRequest('/productos') || [];
        const bajos = products.filter(p => p.stockActual <= (p.stockMinimo || 5));

        const today = new Date();
        today.setHours(0, 0, 0, 0);

        const porVencer = products.filter(p => {
            if (!p.fechaVencimiento) return false;
            const expDate = new Date(p.fechaVencimiento);
            if (isNaN(expDate.getTime())) return false;
            const diffTime = expDate - today;
            const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
            return diffDays <= 5;
        });

        // Ordenar por fecha de vencimiento ascendente (más cercanos o vencidos primero)
        porVencer.sort((a, b) => new Date(a.fechaVencimiento) - new Date(b.fechaVencimiento));

        /*** activar efecto visual en botón si hay alertas. @author RADJ */
        const btnAlertas = document.getElementById('btn-alertas');
        if (btnAlertas) {
            if (bajos.length > 0 || porVencer.length > 0) {
                btnAlertas.classList.add('pulsing');
            } else {
                btnAlertas.classList.remove('pulsing');
            }
        }

        /*** siempre mostrar contenedor de búsqueda para consistencia. @author RADJ */
        const searchCont = document.getElementById('alertas-search-container');
        if (searchCont) searchCont.style.display = 'block';
        let html = '';

        /*** 1. Tabla de Fecha de Vencimientos @author RADJ */
        html += `
            <h3 style="margin: 1.5rem 0 0.75rem 0; display: flex; align-items: center; gap: 0.5rem; color: #ef4444;">📅 Productos Próximos a Vencer o Vencidos (Límite: 5 días)</h3>
            <button type="button" class="btn btn-pdf" onclick="generarReporte('vencimientos')" style="background-color: #d97706; margin-bottom: 1rem; padding: 0.5rem 1rem; font-size: 0.82rem;">Vencimientos PDF</button>
            <div class="card" style="margin-bottom: 2rem;">
                <table>
                    <thead>
                        <tr>
                            <th>Producto</th>
                            <th style="text-align:center">Fecha Vencimiento</th>
                            <th>Estado / Días Restantes</th>
                            <th>Proveedor</th>
                            <th>Acción</th>
                        </tr>
                    </thead>
                    <tbody id="alertas-vence-tbody">
        `;

        if (porVencer.length > 0) {
            porVencer.forEach(p => {
                const prov = AppState.cache.proveedores.find(pr => pr.id === p.idProveedor);
                const provNombre = prov ? prov.nombre : 'Sin asignar';

                const expDate = new Date(p.fechaVencimiento);
                const diffTime = expDate - today;
                const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

                let badgeHtml = '';
                if (diffDays < 0) {
                    badgeHtml = `<span style="font-weight:600; color:#ef4444;">Vencido hace ${Math.abs(diffDays)} días</span>`;
                } else if (diffDays === 0) {
                    badgeHtml = `<span style="font-weight:600; color:#ef4444; animation: pulsing 1.5s infinite ease-in-out;">Vence HOY</span>`;
                } else if (diffDays === 1) {
                    badgeHtml = `<span style="font-weight:600; color:#f59e0b;">Vence Mañana</span>`;
                } else {
                    badgeHtml = `<span style="font-weight:600; color:#f59e0b;">Vence en ${diffDays} días</span>`;
                }

                const actionHtml = p.idProveedor
                    ? `<a href="javascript:void(0)" onclick="verProveedor(${p.idProveedor})" style="color: #a5b4fc; text-decoration: none; font-weight: 600; display: inline-flex; align-items: center; gap: 0.35rem; transition: all 0.2s;" onmouseover="this.style.color='#c7d2fe'" onmouseout="this.style.color='#a5b4fc'">🔍 Ver Proveedor</a>`
                    : `<span style="color: var(--text-muted);">—</span>`;

                html += `
                            <tr>
                                <td style="font-weight:500">${p.nombre}</td>
                                <td style="text-align:center; font-weight:600; font-family:monospace;">${p.fechaVencimiento}</td>
                                <td>${badgeHtml}</td>
                                <td>${provNombre}</td>
                                <td>${actionHtml}</td>
                            </tr>
                `;
            });
        } else {
            html += `
                        <tr>
                            <td colspan="5" style="text-align:center; padding: 2rem; color: #10b981; font-weight: 500;">✓ No hay productos próximos a vencer o vencidos (límite 5 días).</td>
                        </tr>
            `;
        }

        html += `
                    </tbody>
                </table>
            </div>
        `;

        /*** 2. Tabla de Stock Bajo @author RADJ */
        html += `
            <h3 style="margin: 2rem 0 0.75rem 0; display: flex; align-items: center; gap: 0.5rem; color: #ef4444;">⚠ Productos con Stock Bajo</h3>
            <button type="button" class="btn btn-pdf" onclick="generarReporte('stock-bajo')" style="margin-bottom: 1rem; padding: 0.5rem 1rem; font-size: 0.82rem;">Stock Bajo PDF</button>
            <div class="card">
                <table>
                    <thead>
                        <tr>
                            <th>Producto</th>
                            <th style="text-align:center">Stock Actual</th>
                            <th style="text-align:center">Mínimo</th>
                            <th>Proveedor</th>
                            <th>Acción</th>
                        </tr>
                    </thead>
                    <tbody id="alertas-tbody">
        `;

        if (bajos.length > 0) {
            bajos.forEach(p => {
                const prov = AppState.cache.proveedores.find(pr => pr.id === p.idProveedor);
                const provNombre = prov ? prov.nombre : 'Sin asignar';
                const isAgotado = p.stockActual === 0;

                const actionHtml = p.idProveedor
                    ? `<a href="javascript:void(0)" onclick="verProveedor(${p.idProveedor})" style="color: #a5b4fc; text-decoration: none; font-weight: 600; display: inline-flex; align-items: center; gap: 0.35rem; transition: all 0.2s;" onmouseover="this.style.color='#c7d2fe'" onmouseout="this.style.color='#a5b4fc'">🔍 Ver Proveedor</a>`
                    : `<span style="color: var(--text-muted);">—</span>`;

                html += `
                            <tr>
                                <td style="font-weight:500">${p.nombre}</td>
                                <td style="text-align:center; font-weight:600; ${isAgotado ? 'color:#ef4444; background:rgba(239, 68, 68, 0.1);' : 'color:#f59e0b; background:rgba(245, 158, 11, 0.1);'}">${p.stockActual} uds</td>
                                <td style="text-align:center; color:var(--text-muted)">${p.stockMinimo || 5} uds</td>
                                <td>${provNombre}</td>
                                <td>${actionHtml}</td>
                            </tr>
                `;
            });
        } else {
            html += `
                        <tr>
                            <td colspan="5" style="text-align:center; padding: 2rem; color: #10b981; font-weight: 500;">✓ No hay productos con stock bajo.</td>
                        </tr>
            `;
        }

        html += `
                    </tbody>
                </table>
            </div>
        `;

        if (container) {
            container.innerHTML = html;
        }
    } catch (e) {
        /*** presentar error en el contenedor de alertas. @author RADJ */
        if (container) {
            container.innerHTML = `<p style="color:#ef4444">Error al cargar alertas: ${e.message}</p>`;
        }
        const searchCont = document.getElementById('alertas-search-container');
        if (searchCont) searchCont.style.display = 'none';
    }
};

/*** Funciones auxiliares para el Modal de Movimientos (Entrada / Salida). @author RADJ / Antigravity */
window.setVencimientoShortcut = function(months) {
    const dateInput = document.getElementById('mov-fecha-vencimiento');
    if (!dateInput) return;
    const d = new Date();
    d.setMonth(d.getMonth() + months);
    const yyyy = d.getFullYear();
    const mm = String(d.getMonth() + 1).padStart(2, '0');
    const dd = String(d.getDate()).padStart(2, '0');
    dateInput.value = `${yyyy}-${mm}-${dd}`;
    window.checkVencimientoWarning();
};

window.autoGenerarLote = function() {
    const input = document.getElementById('mov-codigo-lote');
    if (!input) return;
    const now = new Date();
    const dateStr = now.toISOString().slice(2, 10).replace(/-/g, '');
    const rand = Math.floor(100 + Math.random() * 900);
    input.value = `LOT-${dateStr}-${rand}`;
};

window.checkVencimientoWarning = function() {
    const dateInput = document.getElementById('mov-fecha-vencimiento');
    const warningEl = document.getElementById('mov-venc-warning');
    if (!dateInput || !warningEl) return;

    if (!dateInput.value) {
        warningEl.style.display = 'none';
        return;
    }

    const selected = new Date(dateInput.value);
    const today = new Date();
    const diffTime = selected - today;
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    if (diffDays <= 30) {
        warningEl.style.display = 'inline';
    } else {
        warningEl.style.display = 'none';
    }
};

window.updateMovPreview = function() {
    const idProducto = parseInt(document.getElementById('mov-id-producto').value);
    const tipo = document.getElementById('mov-tipo').value;
    const cantVal = parseInt(document.getElementById('mov-cantidad').value) || 0;

    const prod = (AppState.allProducts || []).find(p => p.id === idProducto);
    const stockActual = prod ? (prod.stockActual || 0) : 0;

    let nuevoStock = stockActual;
    if (tipo === 'ENTRADA') {
        nuevoStock = stockActual + cantVal;
    } else if (tipo === 'SALIDA') {
        nuevoStock = Math.max(0, stockActual - cantVal);
    }

    const actualEl = document.getElementById('mov-preview-actual');
    const nuevoEl = document.getElementById('mov-preview-nuevo');

    if (actualEl) actualEl.textContent = `${stockActual} u.`;
    if (nuevoEl) {
        nuevoEl.textContent = `${nuevoStock} u.`;
        nuevoEl.style.color = tipo === 'ENTRADA' ? '#38bdf8' : (cantVal > stockActual ? '#f87171' : '#f59e0b');
    }
};

/*** abre el modal para registrar un movimiento de inventario (entrada o salida). @author RADJ / Antigravity */
window.openMovimientoModal = function(idProducto, tipo) {
    const prod = (AppState.allProducts || []).find(p => p.id === idProducto);
    const nombreProducto = prod ? prod.nombre : 'Producto #' + idProducto;

    document.getElementById('mov-id-producto').value = idProducto;
    document.getElementById('mov-tipo').value = tipo;
    document.getElementById('mov-cantidad').value = '';
    document.getElementById('mov-referencia').value = '';
    document.getElementById('mov-observacion').value = '';

    const isEntrada = tipo === 'ENTRADA';
    const titleText = isEntrada ? '📥 Registrar Entrada de Stock' : '📤 Registrar Salida de Stock';
    const subtitleText = `Producto: <strong>${nombreProducto}</strong> (Stock actual: ${prod ? (prod.stockActual || 0) : 0} u.)`;

    document.getElementById('mov-modal-title').textContent = titleText;
    document.getElementById('mov-modal-subtitle').innerHTML = subtitleText;

    const vencContainer = document.getElementById('mov-vencimiento-container');
    const loteContainer = document.getElementById('mov-lote-container');
    const dateInput = document.getElementById('mov-fecha-vencimiento');
    const loteInput = document.getElementById('mov-codigo-lote');

    if (isEntrada) {
        if (vencContainer) vencContainer.style.display = 'block';
        if (loteContainer) loteContainer.style.display = 'block';
        if (dateInput) {
            dateInput.required = true;
            // Pre-llenar por defecto con +1 año si está vacío
            const defaultDate = new Date();
            defaultDate.setFullYear(defaultDate.getFullYear() + 1);
            dateInput.value = defaultDate.toISOString().split('T')[0];
        }
        if (loteInput) loteInput.value = '';
    } else {
        if (vencContainer) vencContainer.style.display = 'none';
        if (loteContainer) loteContainer.style.display = 'none';
        if (dateInput) {
            dateInput.required = false;
            dateInput.value = '';
        }
        if (loteInput) loteInput.value = '';
    }

    const submitBtn = document.getElementById('mov-submit-btn');
    if (submitBtn) {
        submitBtn.textContent = isEntrada ? 'Agregar Stock' : 'Retirar Stock';
        submitBtn.style.background = isEntrada ? '#10b981' : '#ef4444';
    }

    // Configurar event listeners para actualización en tiempo real
    const cantInput = document.getElementById('mov-cantidad');
    if (cantInput) {
        cantInput.oninput = window.updateMovPreview;
    }
    if (dateInput) {
        dateInput.onchange = window.checkVencimientoWarning;
    }

    window.checkVencimientoWarning();
    window.updateMovPreview();

    document.getElementById('movimientoModal').style.display = 'flex';
};

/*** cierra el modal de movimientos de inventario. @author RADJ */
window.closeMovimientoModal = function() {
    document.getElementById('movimientoModal').style.display = 'none';
};

/*** adjuntar controlador para el formulario de movimientos. @author RADJ / Antigravity */
document.addEventListener('DOMContentLoaded', () => {
    const movForm = document.getElementById('mov-modal-form');
    if (movForm) {
        movForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            const idProducto = parseInt(document.getElementById('mov-id-producto').value);
            const tipo = document.getElementById('mov-tipo').value;
            const cantidad = parseInt(document.getElementById('mov-cantidad').value);
            let referencia = document.getElementById('mov-referencia').value.trim();
            let observacion = document.getElementById('mov-observacion').value.trim();
            const fechaVenc = document.getElementById('mov-fecha-vencimiento') ? document.getElementById('mov-fecha-vencimiento').value : '';
            const codigoLote = document.getElementById('mov-codigo-lote') ? document.getElementById('mov-codigo-lote').value.trim() : '';

            if (!idProducto || isNaN(cantidad) || cantidad <= 0) {
                alert("La cantidad debe ser mayor a cero.");
                return;
            }

            const isEntrada = tipo === 'ENTRADA';
            if (isEntrada && !fechaVenc) {
                alert("La fecha de vencimiento es obligatoria para registrar una entrada.");
                return;
            }

            if (isEntrada) {
                if (fechaVenc) {
                    referencia = referencia ? `${referencia} [${fechaVenc}]` : `Vencimiento: ${fechaVenc}`;
                }
                if (codigoLote) {
                    observacion = observacion ? `${observacion} [Lote: ${codigoLote}]` : `Lote: ${codigoLote}`;
                }
            }

            try {
                const userStr = localStorage.getItem('usuario');
                let idUsuario = 1;
                if (userStr) {
                    try {
                        const u = JSON.parse(userStr);
                        if (u && u.id) idUsuario = u.id;
                    } catch (err) { }
                }

                const payload = {
                    idProducto: idProducto,
                    tipoMovimiento: tipo,
                    cantidad: cantidad,
                    referencia: referencia || null,
                    observacion: observacion || null,
                    idUsuario: idUsuario
                };

                await apiRequest('/movimientos-inventario', 'POST', payload);
                closeMovimientoModal();
                await loadInventario();
                alert('Movimiento registrado y stock actualizado con éxito.');
            } catch (err) {
                console.error("Error al registrar movimiento:", err);
                alert("Error al registrar movimiento: " + err.message);
            }
        });
    }

    // Habilitar ordenamiento dinámico en la tabla de inventario del administrador. @author RADJ
    window.enableTableSorting({
        tableSelector: '#sec-inventario table.products-table',
        getDataFn: () => AppState.allProducts,
        setDataFn: (sorted) => { AppState.allProducts = sorted; },
        renderFn: (sorted) => {
            window.renderInventario(sorted);
            const searchInput = document.getElementById('inv-search-input');
            if (searchInput && searchInput.value) {
                window.filterTable(searchInput, 'inv-tbody');
            }
        }
    });
});
