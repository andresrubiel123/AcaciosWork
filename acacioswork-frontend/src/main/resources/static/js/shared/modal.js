/*** módulo javascript del frontend de acacioswork. @author RADJ */
/*** modal.js - lógica compartida para ventanas modales y formularios dinámicos. @author RADJ */

/*** componente global de calendario inteligente para selección de fecha sin cerrar modales. @author RADJ */
window.SmartCalendar = {
    activeInputId: null,
    currentYear: new Date().getFullYear(),
    currentMonth: new Date().getMonth(),

    init() {
        document.addEventListener('click', (e) => {
            if (!this.activeInputId) return;
            const popover = document.getElementById(`smart-calendar-container-${this.activeInputId}`);
            const input = document.getElementById(this.activeInputId);
            const icon = document.getElementById(`smart-calendar-icon-${this.activeInputId}`);
            
            /** Evitar cierre si el elemento clicado fue desmontado del DOM durante el re-renderizado. @author RADJ */
            if (popover && !document.body.contains(e.target)) return;
            
            if (popover && !popover.contains(e.target) && e.target !== input && (!icon || !icon.contains(e.target))) {
                this.close();
            }
        });

        /** Escuchar entradas tipeadas manualmente para auto-formatear y refrescar calendario si está abierto. @author RADJ / Antigravity */
        document.addEventListener('input', (e) => {
            if (e.target && e.target.id && (e.target.id.includes('vencimiento') || e.target.id.includes('date') || e.target.id.includes('fecha'))) {
                let val = e.target.value.replace(/[^0-9-]/g, '');
                if (/^\d{8}$/.test(val)) {
                    e.target.value = `${val.slice(0,4)}-${val.slice(4,6)}-${val.slice(6,8)}`;
                    e.target.dispatchEvent(new Event('change'));
                }
                if (this.activeInputId === e.target.id && /^\d{4}-\d{2}-\d{2}$/.test(e.target.value)) {
                    const parts = e.target.value.split('-');
                    this.currentYear = parseInt(parts[0]);
                    this.currentMonth = parseInt(parts[1]) - 1;
                    this.render(this.activeInputId);
                }
            }
        });
    },

    toggle(inputId) {
        if (this.activeInputId === inputId) {
            this.close();
        } else {
            this.open(inputId);
        }
    },

    open(inputId) {
        this.close();
        this.activeInputId = inputId;
        const input = document.getElementById(inputId);
        if (!input) return;

        if (input.value && /^\d{4}-\d{2}-\d{2}$/.test(input.value)) {
            const parts = input.value.split('-');
            this.currentYear = parseInt(parts[0]);
            this.currentMonth = parseInt(parts[1]) - 1;
        } else {
            const now = new Date();
            this.currentYear = now.getFullYear();
            this.currentMonth = now.getMonth();
        }

        let popover = document.getElementById(`smart-calendar-container-${inputId}`);
        if (!popover) {
            popover = document.createElement('div');
            popover.id = `smart-calendar-container-${inputId}`;
            popover.className = 'smart-calendar-popover';
            input.parentElement.appendChild(popover);
        }

        popover.style.display = 'block';
        this.render(inputId);
    },

    close() {
        if (this.activeInputId) {
            const popover = document.getElementById(`smart-calendar-container-${this.activeInputId}`);
            if (popover) popover.style.display = 'none';
            this.activeInputId = null;
        }
    },

    changeMonth(delta) {
        this.currentMonth += delta;
        if (this.currentMonth > 11) {
            this.currentMonth = 0;
            this.currentYear++;
        } else if (this.currentMonth < 0) {
            this.currentMonth = 11;
            this.currentYear--;
        }
        if (this.activeInputId) this.render(this.activeInputId);
    },

    selectShortcut(type) {
        const now = new Date();
        let targetDate = new Date();
        if (type === 'today') {
            targetDate = now;
        } else if (type === '1m') {
            targetDate.setMonth(now.getMonth() + 1);
        } else if (type === '6m') {
            targetDate.setMonth(now.getMonth() + 6);
        } else if (type === '1y') {
            targetDate.setFullYear(now.getFullYear() + 1);
        } else if (type === 'clear') {
            if (this.activeInputId) {
                const input = document.getElementById(this.activeInputId);
                if (input) {
                    input.value = '';
                    /** Disparar eventos nativos para sincronizar filtros u oyentes de cambio. @author RADJ */
                    input.dispatchEvent(new Event('change'));
                    input.dispatchEvent(new Event('input'));
                }
            }
            this.close();
            return;
        }

        const y = targetDate.getFullYear();
        const m = String(targetDate.getMonth() + 1).padStart(2, '0');
        const d = String(targetDate.getDate()).padStart(2, '0');
        
        if (this.activeInputId) {
            const input = document.getElementById(this.activeInputId);
            if (input) {
                input.value = `${y}-${m}-${d}`;
                /** Disparar eventos nativos para sincronizar filtros u oyentes de cambio. @author RADJ */
                input.dispatchEvent(new Event('change'));
                input.dispatchEvent(new Event('input'));
            }
        }
        this.close();
    },

    selectDay(day) {
        const y = this.currentYear;
        const m = String(this.currentMonth + 1).padStart(2, '0');
        const d = String(day).padStart(2, '0');
        if (this.activeInputId) {
            const input = document.getElementById(this.activeInputId);
            if (input) {
                input.value = `${y}-${m}-${d}`;
                /** Disparar eventos nativos para sincronizar filtros u oyentes de cambio. @author RADJ */
                input.dispatchEvent(new Event('change'));
                input.dispatchEvent(new Event('input'));
            }
        }
        this.close();
    },

    render(inputId) {
        const popover = document.getElementById(`smart-calendar-container-${inputId}`);
        if (!popover) return;

        const monthNames = ["Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"];
        const dayHeaders = ["Lu", "Ma", "Mi", "Ju", "Vi", "Sá", "Do"];

        const input = document.getElementById(inputId);
        const selectedValue = input ? input.value : '';

        const now = new Date();
        const todayStr = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`;

        const firstDayIndex = (new Date(this.currentYear, this.currentMonth, 1).getDay() + 6) % 7;
        const totalDays = new Date(this.currentYear, this.currentMonth + 1, 0).getDate();

        let daysHtml = '';
        for (let i = 0; i < firstDayIndex; i++) {
            daysHtml += `<div class="smart-calendar-day-cell empty"></div>`;
        }

        for (let day = 1; day <= totalDays; day++) {
            const dateStr = `${this.currentYear}-${String(this.currentMonth + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
            let classes = 'smart-calendar-day-cell';
            if (dateStr === todayStr) classes += ' today';
            if (dateStr === selectedValue) classes += ' selected';

            daysHtml += `<div class="${classes}" onclick="SmartCalendar.selectDay(${day})">${day}</div>`;
        }

        popover.innerHTML = `
            <div class="smart-calendar-header">
                <button type="button" class="smart-calendar-nav-btn" onclick="SmartCalendar.changeMonth(-1)">◀</button>
                <span class="smart-calendar-title">${monthNames[this.currentMonth]} ${this.currentYear}</span>
                <button type="button" class="smart-calendar-nav-btn" onclick="SmartCalendar.changeMonth(1)">▶</button>
            </div>
            <div class="smart-calendar-grid">
                ${dayHeaders.map(h => `<div class="smart-calendar-day-header">${h}</div>`).join('')}
                ${daysHtml}
            </div>
        `;
    }
};

document.addEventListener('DOMContentLoaded', () => {
    SmartCalendar.init();
});

/*** obtiene los campos del formulario dinámico según el módulo. @author RADJ */
window.getModalFields = function(type) {
    if (type === 'inventario') {
        const catOpts = AppState.cache.categorias.map(c => `<option value="${c.id}">${c.nombre}</option>`).join('');
        const provOpts = AppState.cache.proveedores.map(p => `<option value="${p.id}">${p.nombre}</option>`).join('');
        return `
            <label>Código de Barras</label>
            <input id="prod-codigoBarras" placeholder="Ej: 7701234" required>
            
            <label>Nombre del Producto</label>
            <input id="prod-nombre" placeholder="Nombre del producto" required>
            
            <label>Unidad de Medida</label>
            <input id="prod-unidadMedida" placeholder="Ej: Unidad, Kilo, Litro" required>
            
            <div style="display:grid;grid-template-columns:1fr 1fr 1fr;gap:0.75rem">
                <div>
                    <label>Stock / Actual</label>
                    <input id="prod-stockActual" type="number" value="0" min="0" required>
                </div>
                <div>
                    <label>Stock Mínimo</label>
                    <input id="prod-stockMinimo" type="number" value="5" min="0" required>
                </div>
                <div>
                    <label>Stock Óptimo</label>
                    <input id="prod-stockOptimo" type="number" value="200" min="0" required>
                </div>
            </div>
            
            <div style="display:grid;grid-template-columns:1fr 1fr;gap:0.75rem">
                <div>
                    <label>Precio Compra</label>
                    <input id="prod-precioCompra" type="number" step="0.01" value="0" required>
                </div>
                <div>
                    <label>Precio Venta</label>
                    <input id="prod-precioVenta" type="number" step="0.01" value="0" required>
                </div>
            </div>

            <div style="position:relative;">
                <label>Fecha de Vencimiento</label>
                <div style="position:relative; display:flex; align-items:center;">
                    <input id="prod-fechaVencimiento" type="text" placeholder="AAAA-MM-DD (Opcional)" style="cursor:text; padding-right:2.5rem;">
                    <span id="smart-calendar-icon-prod-fechaVencimiento" onclick="SmartCalendar.toggle('prod-fechaVencimiento')" style="position:absolute; right:0.75rem; cursor:pointer; font-size:1.1rem; user-select:none;">📅</span>
                </div>
            </div>
            
            <label>Categoría</label>
            <div class="categoria-combo-wrapper">
                <select id="prod-idCategoria" required onchange="window._onCategoriaSelectChange(this)">
                    <option value="">Seleccione una categoría</option>
                    <option value="__nueva__" style="color:#6366f1; font-weight:600;">➕ Crear nueva categoría...</option>
                    ${catOpts}
                </select>
                <div id="nueva-categoria-inline" class="nueva-categoria-inline" style="display:none;">
                    <input id="nueva-categoria-nombre" type="text" placeholder="Nombre de la categoría" maxlength="100" autocomplete="off">
                    <button type="button" class="btn-inline-confirm" onclick="window._crearCategoriaInline()" title="Crear categoría">✔</button>
                    <button type="button" class="btn-inline-cancel" onclick="window._cancelarCategoriaInline()" title="Cancelar">✕</button>
                </div>
            </div>
            
            <label>Proveedor</label>
            <select id="prod-idProveedor" required>
                <option value="">Seleccione un proveedor</option>
                ${provOpts}
            </select>
            
            <label>IVA (%)</label>
            <input id="prod-iva" type="number" step="0.01" value="19" required>
            
            <label>Estado</label>
            <select id="prod-estado">
                <option value="1">Activo</option>
                <option value="0">Inactivo</option>
            </select>
        `;
    }

    if (type === 'proveedor') {
        const tdOpts = AppState.cache.tiposDocumento.map(t => `<option value="${t.id}">${t.nombre}</option>`).join('');
        return `
            <label>Nombre / Razón Social</label>
            <input id="prov-nombre" placeholder="Nombre de la empresa" required>
            
            <div style="display:grid;grid-template-columns:1fr 2fr;gap:0.75rem">
                <div>
                    <label>Tipo Doc.</label>
                    <select id="prov-idTipoDocumento" required>
                        ${tdOpts}
                    </select>
                </div>
                <div>
                    <label>Número de Documento</label>
                    <input id="prov-numeroDocumento" placeholder="NIT / Cédula" required>
                </div>
            </div>
            
            <label>Teléfono</label>
            <input id="prov-telefono" placeholder="Teléfono de contacto">
            
            <label>Email</label>
            <input id="prov-email" type="email" placeholder="correo@empresa.com">
            
            <label>Dirección (Ciudad/Dirección)</label>
            <input id="prov-direccion" placeholder="Dirección física">
            
            <label>Cuenta Bancaria</label>
            <input id="prov-cuentaBancaria" placeholder="Ej: Ahorros Bancolombia No. 123...">
            
            <label>Estado</label>
            <select id="prov-activo">
                <option value="1">Activo</option>
                <option value="0">Inactivo</option>
            </select>
        `;
    }

    if (type === 'cliente') {
        const tdOpts = AppState.cache.tiposDocumento.map(t => `<option value="${t.id}">${t.nombre}</option>`).join('');
        return `
            <label>Nombre Completo</label>
            <input id="cli-nombre" placeholder="Nombre del cliente" required>
            
            <div style="display:grid;grid-template-columns:1fr 2fr;gap:0.75rem">
                <div>
                    <label>Tipo Doc.</label>
                    <select id="cli-idTipoDocumento" required>
                        ${tdOpts}
                    </select>
                </div>
                <div>
                    <label>Número de Documento</label>
                    <input id="cli-numeroDocumento" placeholder="Cédula / NIT" required>
                </div>
            </div>
            
            <label>Teléfono</label>
            <input id="cli-telefono" placeholder="Teléfono">
            
            <label>Email</label>
            <input id="cli-email" type="email" placeholder="correo@ejemplo.com">
            
            <label>Dirección</label>
            <input id="cli-direccion" placeholder="Dirección física">
            
            <label>Cliente Frecuente</label>
            <select id="cli-frecuente">
                <option value="false">No</option>
                <option value="true">Sí</option>
            </select>
            
            <label>Estado</label>
            <select id="cli-activo">
                <option value="1">Activo</option>
                <option value="0">Inactivo</option>
            </select>
        `;
    }

    if (type === 'usuario') {
        const tdOpts = AppState.cache.tiposDocumento.map(t => `<option value="${t.id}">${t.nombre}</option>`).join('');
        const rolOpts = AppState.cache.roles.map(r => `<option value="${r.id}">${r.nombre}</option>`).join('');
        return `
            <div style="display:grid;grid-template-columns:1fr 1fr;gap:0.75rem">
                <div>
                    <label>Nombre</label>
                    <input id="usr-nombre" placeholder="Nombre" required>
                </div>
                <div>
                    <label>Apellido</label>
                    <input id="usr-apellido" placeholder="Apellido" required>
                </div>
            </div>
            
            <div style="display:grid;grid-template-columns:1fr 2fr;gap:0.75rem">
                <div>
                    <label>Tipo Doc.</label>
                    <select id="usr-idTipoDocumento" required>
                        ${tdOpts}
                    </select>
                </div>
                <div>
                    <label>Número de Documento</label>
                    <input id="usr-numeroDocumento" placeholder="Número de Documento" required>
                </div>
            </div>
            
            <label>Teléfono</label>
            <input id="usr-telefono" placeholder="Teléfono">
            
            <label>Email</label>
            <input id="usr-email" type="email" placeholder="correo@ejemplo.com" required>
            
            <label>Nombre de Usuario</label>
            <input id="usr-usuario" placeholder="Nombre de usuario" required>
            
            <label>Contraseña</label>
            <input id="usr-clave" type="password" placeholder="Contraseña" required>
            
            <div style="display:grid;grid-template-columns:1fr 1fr;gap:0.75rem">
                <div>
                    <label>Rol</label>
                    <select id="usr-idRol" required>
                        ${rolOpts}
                    </select>
                </div>
                <div>
                    <label>Estado</label>
                    <select id="usr-activo" required>
                        <option value="1">Activo</option>
                        <option value="0">Inactivo</option>
                    </select>
                </div>
            </div>
        `;
    }
    return '';
};

/*** abre el modal de creación o edición con los datos correspondientes. @author RADJ */
window.openModal = async function(type, id = null) {
    AppState.currentModalType = type;
    AppState.editId = id;

    if (!AppState.cache.categorias.length || !AppState.cache.proveedores.length) {
        if (typeof window.loadReferences === 'function') {
            await window.loadReferences();
        }
    }

    document.getElementById('modal-title').textContent = (AppState.editId ? 'Editar ' : 'Nuevo ') + {
        inventario: 'Producto',
        proveedor: 'Proveedor',
        cliente: 'Cliente',
        usuario: 'Usuario'
    }[type];

    const fieldsContainer = document.getElementById('modal-fields');
    fieldsContainer.innerHTML = getModalFields(type);

    if (AppState.editId) {
        try {
            let data = null;
            if (type === 'inventario') {
                data = await apiRequest(`/productos/${AppState.editId}`);
            } else if (type === 'proveedor') {
                data = await apiRequest(`/proveedores/${AppState.editId}`);
            } else if (type === 'cliente') {
                data = await apiRequest(`/clientes/${AppState.editId}`);
            } else if (type === 'usuario') {
                const usuarios = await apiRequest('/usuarios') || [];
                data = usuarios.find(u => u.numeroDocumento === AppState.editId);
            }

            if (data) {
                populateForm(type, data);
            }
        } catch (e) {
            console.error("Error al cargar datos en modal para edición:", e);
            alert("No se pudieron obtener los datos para editar.");
            closeModal();
            return;
        }
    }

    document.getElementById('mainModal').style.display = 'flex';
};

/*** cerrar el modal principal de formularios. @author RADJ */
window.closeModal = function() {
    document.getElementById('mainModal').style.display = 'none';
};

/*** rellena los campos del modal al editar un elemento existente. @author RADJ */
window.populateForm = function(type, data) {
    if (type === 'inventario') {
        document.getElementById('prod-codigoBarras').value = data.codigoBarras || '';
        document.getElementById('prod-nombre').value = data.nombre || '';
        document.getElementById('prod-stockActual').value = data.stockActual !== undefined ? data.stockActual : 0;
        document.getElementById('prod-stockMinimo').value = data.stockMinimo !== undefined ? data.stockMinimo : 5;
        document.getElementById('prod-stockOptimo').value = data.stockOptimo !== undefined ? data.stockOptimo : 200;
        document.getElementById('prod-precioCompra').value = data.precioCompra || 0;
        document.getElementById('prod-precioVenta').value = data.precioVenta || 0;
        document.getElementById('prod-idCategoria').value = data.idCategoria || '';
        document.getElementById('prod-idProveedor').value = data.idProveedor || '';
        document.getElementById('prod-estado').value = data.estado !== undefined ? data.estado : 1;
        document.getElementById('prod-iva').value = data.iva !== undefined ? data.iva : 19;
        document.getElementById('prod-unidadMedida').value = data.unidadMedida || '';
        if (document.getElementById('prod-fechaVencimiento')) {
            document.getElementById('prod-fechaVencimiento').value = data.fechaVencimiento || '';
        }
    }
    else if (type === 'proveedor') {
        document.getElementById('prov-nombre').value = data.nombre || '';
        document.getElementById('prov-idTipoDocumento').value = data.idTipoDocumento || '';
        document.getElementById('prov-numeroDocumento').value = data.numeroDocumento || '';
        document.getElementById('prov-telefono').value = data.telefono || '';
        document.getElementById('prov-email').value = data.email || '';
        document.getElementById('prov-direccion').value = data.direccion || '';
        document.getElementById('prov-cuentaBancaria').value = data.cuentaBancaria || '';
        document.getElementById('prov-activo').value = data.activo !== undefined ? data.activo : 1;
    }
    else if (type === 'cliente') {
        document.getElementById('cli-nombre').value = data.nombre || '';
        document.getElementById('cli-idTipoDocumento').value = data.idTipoDocumento || '';
        document.getElementById('cli-numeroDocumento').value = data.numeroDocumento || '';
        document.getElementById('cli-telefono').value = data.telefono || '';
        document.getElementById('cli-email').value = data.email || '';
        document.getElementById('cli-direccion').value = data.direccion || '';
        document.getElementById('cli-frecuente').value = data.frecuente ? 'true' : 'false';
        document.getElementById('cli-activo').value = data.activo !== undefined ? data.activo : 1;
    }
    else if (type === 'usuario') {
        document.getElementById('usr-nombre').value = data.nombre || '';
        document.getElementById('usr-apellido').value = data.apellido || '';
        document.getElementById('usr-idTipoDocumento').value = data.idTipoDocumento || '';

        const docInput = document.getElementById('usr-numeroDocumento');
        docInput.value = data.numeroDocumento || '';
        docInput.disabled = true;

        document.getElementById('usr-telefono').value = data.telefono || '';
        document.getElementById('usr-email').value = data.email || '';
        document.getElementById('usr-usuario').value = data.usuario || '';

        const claveInput = document.getElementById('usr-clave');
        claveInput.required = false;
        claveInput.placeholder = "Dejar en blanco para conservar actual";

        document.getElementById('usr-idRol').value = data.idRol || '';
        document.getElementById('usr-activo').value = data.activo !== undefined ? data.activo : 1;
    }
};

/*** construye un objeto json a partir de los datos ingresados en el formulario dinámico. @author RADJ */
window.getFormData = function(type) {
    if (type === 'inventario') {
        return {
            codigoBarras: document.getElementById('prod-codigoBarras').value,
            nombre: document.getElementById('prod-nombre').value,
            stockActual: parseInt(document.getElementById('prod-stockActual').value) || 0,
            stockMinimo: parseInt(document.getElementById('prod-stockMinimo').value) || 5,
            stockOptimo: parseInt(document.getElementById('prod-stockOptimo').value) || 200,
            precioCompra: parseFloat(document.getElementById('prod-precioCompra').value) || 0,
            precioVenta: parseFloat(document.getElementById('prod-precioVenta').value) || 0,
            idCategoria: parseInt(document.getElementById('prod-idCategoria').value) || null,
            idProveedor: parseInt(document.getElementById('prod-idProveedor').value) || null,
            estado: parseInt(document.getElementById('prod-estado').value),
            iva: parseFloat(document.getElementById('prod-iva').value) || 19,
            unidadMedida: document.getElementById('prod-unidadMedida').value || 'Unidad',
            fechaVencimiento: document.getElementById('prod-fechaVencimiento') ? document.getElementById('prod-fechaVencimiento').value : null
        };
    }

    if (type === 'proveedor') {
        return {
            nombre: document.getElementById('prov-nombre').value,
            idTipoDocumento: parseInt(document.getElementById('prov-idTipoDocumento').value) || null,
            numeroDocumento: document.getElementById('prov-numeroDocumento').value,
            telefono: document.getElementById('prov-telefono').value,
            email: document.getElementById('prov-email').value,
            direccion: document.getElementById('prov-direccion').value,
            cuentaBancaria: document.getElementById('prov-cuentaBancaria').value,
            activo: parseInt(document.getElementById('prov-activo').value)
        };
    }

    if (type === 'cliente') {
        return {
            nombre: document.getElementById('cli-nombre').value,
            idTipoDocumento: parseInt(document.getElementById('cli-idTipoDocumento').value) || null,
            numeroDocumento: document.getElementById('cli-numeroDocumento').value,
            telefono: document.getElementById('cli-telefono').value,
            email: document.getElementById('cli-email').value,
            direccion: document.getElementById('cli-direccion').value,
            frecuente: document.getElementById('cli-frecuente').value === 'true',
            activo: parseInt(document.getElementById('cli-activo').value)
        };
    }

    if (type === 'usuario') {
        const u = {
            nombre: document.getElementById('usr-nombre').value,
            apellido: document.getElementById('usr-apellido').value,
            numeroDocumento: document.getElementById('usr-numeroDocumento').value,
            idTipoDocumento: parseInt(document.getElementById('usr-idTipoDocumento').value) || null,
            telefono: document.getElementById('usr-telefono').value,
            email: document.getElementById('usr-email').value,
            usuario: document.getElementById('usr-usuario').value,
            idRol: parseInt(document.getElementById('usr-idRol').value) || null,
            activo: parseInt(document.getElementById('usr-activo').value)
        };
        const clave = document.getElementById('usr-clave').value;
        if (clave) {
            u.clave = clave;
        }
        return u;
    }

    return null;
};

/*** cargar y mostrar modal con detalles completos de un proveedor. @author RADJ */
window.verProveedor = async function(id) {
    try {
        if (!AppState.cache.proveedores.length) {
            if (typeof window.loadReferences === 'function') {
                await window.loadReferences();
            }
        }
        const prov = AppState.cache.proveedores.find(p => p.id === id) || await apiRequest(`/proveedores/${id}`);
        if (!prov) {
            alert("No se encontró la información del proveedor.");
            return;
        }

        const content = document.getElementById('prov-details-content');
        content.innerHTML = `
            <div><strong>Nombre / Razón Social:</strong> <span style="color:white; margin-left: 0.5rem;">${prov.nombre}</span></div>
            <div><strong>NIT / Identificación:</strong> <span style="color:white; margin-left: 0.5rem;">${prov.numeroDocumento || '—'}</span></div>
            <div><strong>Teléfono:</strong> <span style="color:white; margin-left: 0.5rem;">${prov.telefono || '—'}</span></div>
            <div><strong>Email:</strong> <span style="color:white; margin-left: 0.5rem;">${prov.email || '—'}</span></div>
            <div><strong>Dirección:</strong> <span style="color:white; margin-left: 0.5rem;">${prov.direccion || '—'}</span></div>
            <div><strong>Cuenta Bancaria:</strong> <span style="color:white; margin-left: 0.5rem;">${prov.cuentaBancaria || '—'}</span></div>
            <div><strong>Estado:</strong> <span class="badge ${prov.activo === 1 ? 'badge-success' : 'badge-danger'}" style="margin-left: 0.5rem;">${prov.activo === 1 ? 'Activo' : 'Inactivo'}</span></div>
        `;
        document.getElementById('proveedorModal').style.display = 'flex';
    } catch (e) {
        console.error(e);
        alert("Error al cargar detalles del proveedor: " + e.message);
    }
};

/*** cerrar el modal de detalles del proveedor. @author RADJ */
window.closeProveedorModal = function() {
    document.getElementById('proveedorModal').style.display = 'none';
};

/*** registrar el callback global para el envío de formularios modales. @author RADJ */
document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('modal-form');
    if (form) {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            const type = AppState.currentModalType;
            const data = getFormData(type);

            if (!data) return;

            try {
                let endpoint = '';
                let method = AppState.editId ? 'PUT' : 'POST';

                if (type === 'inventario') endpoint = AppState.editId ? `/productos/${AppState.editId}` : '/productos';
                else if (type === 'proveedor') endpoint = AppState.editId ? `/proveedores/${AppState.editId}` : '/proveedores';
                else if (type === 'cliente') endpoint = AppState.editId ? `/clientes/${AppState.editId}` : '/clientes';
                else if (type === 'usuario') endpoint = AppState.editId ? `/usuarios/${AppState.editId}` : '/usuarios';

                await apiRequest(endpoint, method, data);
                closeModal();

                if (typeof window.loadReferences === 'function') {
                    await window.loadReferences();
                }

                if (type === 'inventario' && typeof window.loadInventario === 'function') window.loadInventario();
                else if (type === 'proveedor' && typeof window.loadProveedores === 'function') window.loadProveedores();
                else if (type === 'cliente' && typeof window.loadClientes === 'function') window.loadClientes();
                else if (type === 'usuario' && typeof window.loadUsuarios === 'function') window.loadUsuarios();

                if (typeof window.showToast === 'function') {
                    window.showToast('✅ ' + (AppState.editId ? 'Registro actualizado' : 'Registro creado') + ' exitosamente.', 'success');
                } else {
                    alert((AppState.editId ? 'Registro actualizado' : 'Registro creado') + ' exitosamente.');
                }
            } catch (e) {
                console.error("Error al persistir registro:", e);
                alert("Error al guardar: " + e.message);
            }
        });
    }
});

/*** maneja el cambio del select de categorías: muestra el input inline si se selecciona "crear nueva". @author RADJ */
window._onCategoriaSelectChange = function(selectEl) {
    const inlineDiv = document.getElementById('nueva-categoria-inline');
    if (selectEl.value === '__nueva__') {
        inlineDiv.style.display = 'flex';
        const input = document.getElementById('nueva-categoria-nombre');
        input.value = '';
        input.focus();
       
/*** temporariamente desactivar el required del select para que el form no valide. @author RADJ */
        selectEl.removeAttribute('required');
    } else {
        inlineDiv.style.display = 'none';
        selectEl.setAttribute('required', 'required');
    }
};

/*** crea una categoría nueva en el backend y la selecciona automáticamente en el dropdown. @author RADJ */
window._crearCategoriaInline = async function() {
    const input = document.getElementById('nueva-categoria-nombre');
    const nombre = input.value.trim();

    if (!nombre) {
        input.focus();
        input.style.borderColor = '#ef4444';
        setTimeout(() => input.style.borderColor = '', 1500);
        return;
    }

    try {
        const nuevaCat = await apiRequest('/categorias', 'POST', { nombre });

       
/*** refrescar el cache global de categorías. @author RADJ */
        AppState.cache.categorias = await apiRequest('/categorias') || [];

       
/*** reconstruir las opciones del select. @author RADJ */
        const select = document.getElementById('prod-idCategoria');
        const catOpts = AppState.cache.categorias.map(c => `<option value="${c.id}">${c.nombre}</option>`).join('');
        select.innerHTML = `
            <option value="">Seleccione una categoría</option>
            <option value="__nueva__" style="color:#6366f1; font-weight:600;">➕ Crear nueva categoría...</option>
            ${catOpts}
        `;

       
/*** auto-seleccionar la categoría recién creada. @author RADJ */
        select.value = String(nuevaCat.id);
        select.setAttribute('required', 'required');

       
/*** ocultar el input inline. @author RADJ */
        document.getElementById('nueva-categoria-inline').style.display = 'none';

       
/*** notificación visual. @author RADJ */
        if (typeof showToast === 'function') {
            showToast(`✅ Categoría "${nombre}" creada`, 'success');
        }
    } catch (e) {
        console.error("Error al crear categoría:", e);
        alert("Error al crear la categoría: " + e.message);
    }
};

/*** cancela la creación inline de categoría y restablece el select. @author RADJ */
window._cancelarCategoriaInline = function() {
    const select = document.getElementById('prod-idCategoria');
    select.value = '';
    select.setAttribute('required', 'required');
    document.getElementById('nueva-categoria-inline').style.display = 'none';
    document.getElementById('nueva-categoria-nombre').value = '';
};
