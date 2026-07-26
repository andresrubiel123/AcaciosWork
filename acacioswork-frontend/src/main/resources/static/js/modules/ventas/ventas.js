/*** módulo javascript del frontend de acacioswork. @author RADJ */
/*** ventas.js - lógica de punto de venta (pos) y registro de transacciones. @author RADJ */

/*** carga e inicializa la sección de ventas. @author RADJ */
window.loadVenderSection = async function() {
    try {
        AppState.allProducts = await apiRequest('/productos') || [];
    } catch (e) {
        console.error('Error al precargar productos:', e);
    }

    try {
        AppState.allClientes = await apiRequest('/clientes') || [];
        loadClienteSelect();
    } catch (e) {
        console.error('Error al cargar clientes:', e);
    }

    limpiarCarrito();
};

/*** carga los clientes en el selector. @author RADJ */
window.loadClienteSelect = function() {
    const select = document.getElementById('client-select');
    if (!select) return;
    select.innerHTML = '<option value="">— Venta sin cliente registrado —</option>';
    AppState.allClientes.forEach(c => {
        if (c.activo === 1) {
            select.innerHTML += `<option value="${c.id}">${c.nombre} (${c.numeroDocumento || 'Sin doc'})</option>`;
        }
    });
};

/*** búsqueda de productos en tiempo real. @author RADJ */
window.searchProducts = function(query) {
    clearTimeout(AppState.searchTimeout);
    const dropdown = document.getElementById('product-dropdown');
    if (!dropdown) return;
    if (!query || query.length < 1) { dropdown.style.display = 'none'; return; }

    AppState.searchTimeout = setTimeout(() => {
        const q = query.toLowerCase();
        const results = AppState.allProducts.filter(p =>
            (p.nombre && p.nombre.toLowerCase().includes(q)) ||
            (p.codigoBarras && p.codigoBarras.toLowerCase().includes(q))
        ).slice(0, 12);

        if (!results.length) {
            dropdown.innerHTML = '<div class="product-dropdown-item" style="color:var(--text-muted); cursor:default;">Sin resultados</div>';
        } else {
            dropdown.innerHTML = results.map(p => {
                const sinStock = (p.stockActual || 0) <= 0;
                return `<div class="product-dropdown-item ${sinStock ? 'no-stock' : ''}"
                    onclick="${sinStock ? '' : `addToCart(${p.id})`}">
                    <div>
                        <div class="p-name">${p.nombre}</div>
                        <div class="p-meta">Stock: ${p.stockActual || 0} uds${sinStock ? ' — Sin stock' : ''}</div>
                    </div>
                    <div class="p-price">${formatCurrency(p.precioVenta)}</div>
                </div>`;
            }).join('');
        }
        dropdown.style.display = 'block';
    }, 180);
};

/*** agregar un producto al carrito. @author RADJ */
window.addToCart = function(productId) {
    const producto = AppState.allProducts.find(p => p.id === productId);
    if (!producto) return;

    const searchInput = document.getElementById('product-search');
    if (searchInput) searchInput.value = '';
    const dropdown = document.getElementById('product-dropdown');
    if (dropdown) dropdown.style.display = 'none';

    const existing = AppState.cart.find(item => item.producto.id === productId);
    if (existing) {
        if (existing.cantidad >= (producto.stockActual || 0)) {
            if (window.showToast) {
                showToast('No hay más stock disponible para este producto.', 'error');
            } else {
                alert('No hay más stock disponible para este producto.');
            }
            return;
        }
        existing.cantidad++;
    } else {
        AppState.cart.push({ producto, cantidad: 1 });
    }
    renderCart();
};

/*** quitar producto del carrito. @author RADJ */
window.removeFromCart = function(productId) {
    AppState.cart = AppState.cart.filter(item => item.producto.id !== productId);
    renderCart();
};

/*** cambiar cantidad del producto en el carrito. @author RADJ */
window.updateQuantity = function(productId, newQty) {
    const item = AppState.cart.find(i => i.producto.id === productId);
    if (!item) return;
    const qty = parseInt(newQty) || 1;
    const maxStock = item.producto.stockActual || 0;
    item.cantidad = Math.max(1, Math.min(qty, maxStock));
    renderCart();
};

/*** limpiar todos los elementos del carrito. @author RADJ */
window.limpiarCarrito = function() {
    AppState.cart = [];
    renderCart();
};

/*** renderizar la tabla del carrito. @author RADJ */
window.renderCart = function() {
    const tbody = document.getElementById('cart-tbody');
    if (!tbody) return;

    if (!AppState.cart.length) {
        tbody.innerHTML = `<tr><td colspan="5"><div class="cart-empty-msg">🛒 El carrito está vacío.<br><span style="font-size:0.78rem;">Busca y agrega productos arriba.</span></div></td></tr>`;
        updateSummary(0, 0);
        const btn = document.getElementById('btn-registrar');
        if (btn) btn.disabled = true;
        return;
    }

    let totalItems = 0;
    let totalCost = 0;
    tbody.innerHTML = AppState.cart.map(item => {
        const subtotal = item.cantidad * item.producto.precioVenta;
        totalItems += item.cantidad;
        totalCost += subtotal;
        return `<tr>
            <td style="font-weight:500; max-width:180px; overflow:hidden; text-overflow:ellipsis; white-space:nowrap;" title="${item.producto.nombre}">${item.producto.nombre}</td>
            <td style="text-align:center;">
                <input type="number" class="qty-input" value="${item.cantidad}" min="1" max="${item.producto.stockActual || 1}"
                    onchange="updateQuantity(${item.producto.id}, this.value)" oninput="updateQuantity(${item.producto.id}, this.value)">
            </td>
            <td style="text-align:right; color:var(--text-muted);">${formatCurrency(item.producto.precioVenta)}</td>
            <td style="text-align:right; font-weight:600; color:#10b981;">${formatCurrency(subtotal)}</td>
            <td style="text-align:center;">
                <button class="remove-item-btn" onclick="removeFromCart(${item.producto.id})" title="Quitar">✕</button>
            </td>
        </tr>`;
    }).join('');

    updateSummary(totalItems, totalCost);
    const btn = document.getElementById('btn-registrar');
    if (btn) btn.disabled = false;
};

/*** actualizar resumen financiero. @author RADJ */
window.updateSummary = function(items, total) {
    const summaryItems = document.getElementById('summary-items');
    const summarySubtotal = document.getElementById('summary-subtotal');
    const summaryTotal = document.getElementById('summary-total');

    if (summaryItems) summaryItems.textContent = items;
    if (summarySubtotal) summarySubtotal.textContent = formatCurrency(total);
    if (summaryTotal) summaryTotal.textContent = formatCurrency(total);
};

/*** registrar la venta en la base de datos. @author RADJ */
window.registrarVenta = async function() {
    if (!AppState.cart.length) return;
    const btn = document.getElementById('btn-registrar');
    if (!btn) return;
    btn.disabled = true;
    btn.textContent = '⏳ Registrando...';

   
/*** obtener id de usuario administrador logueado. @author RADJ */
    let idUsuario = null;
    const usuarioRaw = localStorage.getItem('usuario');
    if (usuarioRaw) {
        try {
            idUsuario = JSON.parse(usuarioRaw).id;
        } catch (e) {
            console.error('Error al parsear usuario de localStorage:', e);
        }
    }

    const clienteIdRaw = document.getElementById('client-select').value;
    const idCliente = clienteIdRaw ? parseInt(clienteIdRaw) : null;

    const detalles = AppState.cart.map(item => ({
        idProducto: item.producto.id,
        cantidad: item.cantidad,
        precioUnitario: item.producto.precioVenta
    }));

    try {
        await apiRequest('/ventas', 'POST', { idUsuario, idCliente, detalles });
        if (window.showToast) {
            showToast('✅ Venta registrada con éxito', 'success');
        } else {
            alert('Venta registrada con éxito.');
        }
        limpiarCarrito();
       
/*** recargar productos para reflejar los nuevos stocks. @author RADJ */
        AppState.allProducts = await apiRequest('/productos') || [];
        const clientSelect = document.getElementById('client-select');
        if (clientSelect) clientSelect.value = '';
    } catch (e) {
        if (window.showToast) {
            showToast('❌ Error al registrar: ' + e.message, 'error');
        } else {
            alert('Error al registrar: ' + e.message);
        }
    } finally {
        btn.textContent = '✅ Registrar Venta';
        btn.disabled = AppState.cart.length === 0;
    }
};

/*** cerrar dropdown al hacer click fuera del buscador. @author RADJ */
document.addEventListener('click', e => {
    const searchWrapper = e.target.closest('.pos-search-wrapper');
    if (!searchWrapper) {
        const dropdown = document.getElementById('product-dropdown');
        if (dropdown) dropdown.style.display = 'none';
    }
});
