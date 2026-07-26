/*** módulo javascript del frontend de acacioswork. @author RADJ */
/*** inventario.js - lógica de negocio e interfaz de movimientos de inventario y alertas. @author RADJ */

/*** carga y visualización de productos en inventario. @author RADJ */
window.loadInventario = async function() {
    /*** limpiar input de búsqueda de inventario. @author RADJ */
    const searchInput = document.getElementById('inv-search-input');
    if (searchInput) searchInput.value = '';
    /*** obtener el cuerpo de la tabla de inventario. @author RADJ */
    const tbody = document.getElementById('inv-tbody');
    try {
        /*** obtener listado de productos desde la api. @author RADJ */
        const products = await apiRequest('/productos') || [];
        AppState.allProducts = products;
        /*** actualizar las tarjetas de estadísticas si la función existe. @author RADJ */
        if (window.updateStatsUI) {
            window.updateStatsUI(products);
        }

        /*** generar el html de las filas de la tabla de productos. @author RADJ */
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
                        <td class="col-vencimiento" style="font-size:0.8rem;color:${p.fechaVencimiento ? '#a7f3d0' : 'var(--text-muted)'}">${p.fechaVencimiento || '—'}</td>
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
    } catch (e) {
        /*** renderizar mensaje de error en la tabla si falla la petición. @author RADJ */
        if (tbody) {
            tbody.innerHTML = `<tr><td colspan="11" style="text-align:center;padding:2rem;color:#ef4444">Error: ${e.message}</td></tr>`;
        }
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

/*** abre el modal para registrar un movimiento de inventario (entrada o salida). @author RADJ */
window.openMovimientoModal = function(idProducto, tipo) {
    const prod = AppState.allProducts.find(p => p.id === idProducto);
    const nombreProducto = prod ? prod.nombre : 'Producto #' + idProducto;

    document.getElementById('mov-id-producto').value = idProducto;
    document.getElementById('mov-tipo').value = tipo;
    document.getElementById('mov-cantidad').value = '';
    document.getElementById('mov-referencia').value = '';
    document.getElementById('mov-observacion').value = '';

    const isEntrada = tipo === 'ENTRADA';
    const titleText = isEntrada ? '📥 Registrar Entrada' : '📤 Registrar Salida';
    const subtitleText = `Producto: <strong>${nombreProducto}</strong>`;

    document.getElementById('mov-modal-title').textContent = titleText;
    document.getElementById('mov-modal-subtitle').innerHTML = subtitleText;

    const submitBtn = document.getElementById('mov-submit-btn');
    if (submitBtn) {
        submitBtn.textContent = isEntrada ? 'Agregar Stock' : 'Retirar Stock';
        submitBtn.style.background = isEntrada ? '#10b981' : '#ef4444';
    }

    document.getElementById('movimientoModal').style.display = 'flex';
};

/*** cierra el modal de movimientos de inventario. @author RADJ */
window.closeMovimientoModal = function() {
    document.getElementById('movimientoModal').style.display = 'none';
};

/*** adjuntar controlador para el formulario de movimientos. @author RADJ */
document.addEventListener('DOMContentLoaded', () => {
    const movForm = document.getElementById('mov-modal-form');
    if (movForm) {
        movForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            const idProducto = parseInt(document.getElementById('mov-id-producto').value);
            const tipo = document.getElementById('mov-tipo').value;
            const cantidad = parseInt(document.getElementById('mov-cantidad').value);
            const referencia = document.getElementById('mov-referencia').value.trim();
            const observacion = document.getElementById('mov-observacion').value.trim();

            if (!idProducto || isNaN(cantidad) || cantidad <= 0) {
                alert("La cantidad debe ser mayor a cero.");
                return;
            }

            try {
                const userStr = localStorage.getItem('usuario');
                let idUsuario = 1;
/*** default. @author RADJ */
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
});
