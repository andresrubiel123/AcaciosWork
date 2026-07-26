/*** módulo javascript del frontend de acacioswork. @author RADJ */
/*** exportador-pdf.js - lógica para la generación y maquetación de reportes pdf. @author RADJ */

/*** generación de reportes pdf optimizados para impresión física. @author RADJ */
window.generarReporte = async function(tipo) {
    try {
        let titulo = '';
        let headers = [];
        let rows = [];
        let resumenHtml = '';
        const nowStr = new Date().toLocaleString('es-CO');

        /*** generar reporte específico según la sección seleccionada. @author RADJ */
        if (tipo === 'inventario') {
            titulo = 'Inventario General de Productos';
            headers = ['Código de Barras', 'Nombre del Producto', 'Stock', 'P. Compra', 'P. Venta', 'Estado'];
            const data = await apiRequest('/productos') || [];

            let totalStock = 0;
            let totalValor = 0;
            let totalCosto = 0;

            /*** procesar cada producto del inventario para totales y filas. @author RADJ */
            rows = data.map(p => {
                totalStock += p.stockActual || 0;
                totalValor += (p.stockActual || 0) * (p.precioVenta || 0);
                totalCosto += (p.stockActual || 0) * (p.precioCompra || 0);
                return [
                    p.codigoBarras || 'N/A',
                    p.nombre,
                    `${p.stockActual} uds`,
                    `$${p.precioCompra?.toLocaleString('es-CO')}`,
                    `$${p.precioVenta?.toLocaleString('es-CO')}`,
                    p.estado === 1 ? 'Activo' : 'Inactivo'
                ];
            });

            /*** crear caja de resumen financiero de inventario. @author RADJ */
            resumenHtml = `
                <div class="summary-box">
                    <p><strong>Total Productos:</strong> ${data.length}</p>
                    <p><strong>Stock Total en Almacén:</strong> ${totalStock} unidades</p>
                    <p><strong>Valoración Comercial (a P. Venta):</strong> $${totalValor.toLocaleString('es-CO')}</p>
                    <p><strong>Valor Costo Total:</strong> $${totalCosto.toLocaleString('es-CO')}</p>
                    <p><strong>Utilidad Neta Estimada:</strong> $${(totalValor - totalCosto).toLocaleString('es-CO')}</p>
                </div>
            `;
        } else if (tipo === 'stock-bajo') {
            titulo = 'Reporte de Productos con Stock Bajo';
            headers = ['Código de Barras', 'Nombre', 'Stock Actual', 'Stock Mínimo', 'P. Venta', 'Proveedor'];
            const data = await apiRequest('/productos') || [];
            const stockBajoData = data.filter(p => p.stockActual <= (p.stockMinimo || 5));

            /*** generar filas para productos con niveles de stock críticos. @author RADJ */
            rows = stockBajoData.map(p => {
                const prov = AppState.cache.proveedores.find(pr => pr.id === p.idProveedor);
                return [
                    p.codigoBarras || 'N/A',
                    p.nombre,
                    `<span style="color:#ef4444; font-weight:bold">${p.stockActual} uds</span>`,
                    `${p.stockMinimo || 5} uds`,
                    `$${p.precioVenta?.toLocaleString('es-CO')}`,
                    prov ? prov.nombre : 'Sin asignar'
                ];
            });

            /*** crear caja de resumen de stock crítico. @author RADJ */
            resumenHtml = `
                <div class="summary-box">
                    <p><strong>Total en Stock Crítico:</strong> ${stockBajoData.length} productos</p>
                </div>
            `;
        } else if (tipo === 'vencimientos') {
            titulo = 'Reporte de Productos Próximos a Vencer o Vencidos';
            headers = ['Código de Barras', 'Nombre del Producto', 'Fecha Vencimiento', 'Estado / Días', 'Proveedor'];
            const data = await apiRequest('/productos') || [];

            const today = new Date();
            today.setHours(0, 0, 0, 0);

            const porVencerData = data.filter(p => {
                if (!p.fechaVencimiento) return false;
                const expDate = new Date(p.fechaVencimiento);
                if (isNaN(expDate.getTime())) return false;
                const diffTime = expDate - today;
                const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
                return diffDays <= 5;
            });

            porVencerData.sort((a, b) => new Date(a.fechaVencimiento) - new Date(b.fechaVencimiento));

            rows = porVencerData.map(p => {
                const prov = AppState.cache.proveedores.find(pr => pr.id === p.idProveedor);
                const expDate = new Date(p.fechaVencimiento);
                const diffTime = expDate - today;
                const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

                let statusText = '';
                if (diffDays < 0) {
                    statusText = `<span style="color:#ef4444; font-weight:bold">Vencido (${Math.abs(diffDays)}d)</span>`;
                } else if (diffDays === 0) {
                    statusText = `<span style="color:#ef4444; font-weight:bold">Vence HOY</span>`;
                } else if (diffDays === 1) {
                    statusText = `<span style="color:#f97316; font-weight:bold">Vence Mañana</span>`;
                } else {
                    statusText = `<span style="color:#d97706; font-weight:bold">Vence en ${diffDays} días</span>`;
                }

                return [
                    p.codigoBarras || 'N/A',
                    p.nombre,
                    p.fechaVencimiento,
                    statusText,
                    prov ? prov.nombre : 'Sin asignar'
                ];
            });

            resumenHtml = `
                <div class="summary-box">
                    <p><strong>Total por Vencer o Vencidos (Límite 5 días):</strong> ${porVencerData.length} productos</p>
                </div>
            `;
        } else if (tipo === 'vencimientos-15') {
            /** Generar reporte para productos próximos a vencer a 15 días. @author RADJ */
            titulo = 'Reporte de Productos Próximos a Vencer o Vencidos (15 días)';
            headers = ['Código de Barras', 'Nombre del Producto', 'Fecha Vencimiento', 'Estado / Días', 'Proveedor'];
            const data = await apiRequest('/productos') || [];

            const today = new Date();
            today.setHours(0, 0, 0, 0);

            const porVencerData = data.filter(p => {
                if (!p.fechaVencimiento) return false;
                const expDate = new Date(p.fechaVencimiento);
                if (isNaN(expDate.getTime())) return false;
                const diffTime = expDate - today;
                const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
                return diffDays <= 15;
            });

            porVencerData.sort((a, b) => new Date(a.fechaVencimiento) - new Date(b.fechaVencimiento));

            rows = porVencerData.map(p => {
                const prov = AppState.cache.proveedores.find(pr => pr.id === p.idProveedor);
                const expDate = new Date(p.fechaVencimiento);
                const diffTime = expDate - today;
                const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

                let statusText = '';
                if (diffDays < 0) {
                    statusText = `<span style="color:#ef4444; font-weight:bold">Vencido (${Math.abs(diffDays)}d)</span>`;
                } else if (diffDays === 0) {
                    statusText = `<span style="color:#ef4444; font-weight:bold">Vence HOY</span>`;
                } else if (diffDays === 1) {
                    statusText = `<span style="color:#f97316; font-weight:bold">Vence Mañana</span>`;
                } else if (diffDays <= 5) {
                    statusText = `<span style="color:#d97706; font-weight:bold">Vence en ${diffDays} días</span>`;
                } else {
                    statusText = `<span style="color:#a16207; font-weight:bold">Vence en ${diffDays} días</span>`;
                }

                return [
                    p.codigoBarras || 'N/A',
                    p.nombre,
                    p.fechaVencimiento,
                    statusText,
                    prov ? prov.nombre : 'Sin asignar'
                ];
            });

            resumenHtml = `
                <div class="summary-box">
                    <p><strong>Total por Vencer o Vencidos (Límite 15 días):</strong> ${porVencerData.length} productos</p>
                </div>
            `;
        } else if (tipo === 'clientes') {
            titulo = 'Reporte General de Clientes';
            headers = ['Nombre Completo', 'Identificación', 'Teléfono', 'Email', 'Dirección', 'Frecuente', 'Estado'];
            const data = await apiRequest('/clientes') || [];

            /*** construir filas con información detallada de cada cliente. @author RADJ */
            rows = data.map(c => {
                return [
                    c.nombre,
                    c.numeroDocumento || '—',
                    c.telefono || '—',
                    c.email || '—',
                    c.direccion || '—',
                    c.frecuente ? 'Sí' : 'No',
                    c.activo === 1 ? 'Activo' : 'Inactivo'
                ];
            });

            /*** crear caja resumen del reporte de clientes. @author RADJ */
            resumenHtml = `
                <div class="summary-box">
                    <p><strong>Total Clientes Registrados:</strong> ${data.length}</p>
                </div>
            `;
        } else if (tipo === 'proveedores') {
            titulo = 'Directorio General de Proveedores';
            headers = ['Nombre / Empresa', 'NIT / Identificación', 'Teléfono', 'Email', 'Dirección', 'Cuenta Bancaria', 'Estado'];
            const data = await apiRequest('/proveedores') || [];

            /*** construir filas con información de cada proveedor. @author RADJ */
            rows = data.map(p => {
                return [
                    p.nombre,
                    p.numeroDocumento || '—',
                    p.telefono || '—',
                    p.email || '—',
                    p.direccion || '—',
                    p.cuentaBancaria || '—',
                    p.activo === 1 ? 'Activo' : 'Inactivo'
                ];
            });

            /*** crear caja resumen del reporte de proveedores. @author RADJ */
            resumenHtml = `
                <div class="summary-box">
                    <p><strong>Total Proveedores Registrados:</strong> ${data.length}</p>
                </div>
            `;
        } else if (tipo === 'usuarios') {
            titulo = 'Reporte de Usuarios del Sistema';
            headers = ['Nombre Completo', 'Identificación', 'Usuario', 'Email', 'Rol', 'Estado'];
            const data = await apiRequest('/usuarios') || [];

            /*** crear filas de usuarios y sus roles asignados. @author RADJ */
            rows = data.map(u => {
                return [
                    `${u.nombre} ${u.apellido || ''}`,
                    u.numeroDocumento || '—',
                    u.usuario || '—',
                    u.email || '—',
                    u.idRol === 1 ? 'Administrador' : 'Auxiliar',
                    u.activo === 1 ? 'Activo' : 'Inactivo'
                ];
            });

            /*** crear caja de resumen de usuarios. @author RADJ */
            resumenHtml = `
                <div class="summary-box">
                    <p><strong>Total Usuarios Registrados:</strong> ${data.length}</p>
                </div>
            `;
        } else if (tipo === 'resumen') {
            titulo = 'Resumen Ejecutivo de la Empresa';
            headers = ['Indicador', 'Valor / Métrica', 'Estado / Detalle'];

            /*** obtener información de todos los módulos para el resumen de alto nivel. @author RADJ */
            const productos = await apiRequest('/productos') || [];
            const clientes = await apiRequest('/clientes') || [];
            const proveedores = await apiRequest('/proveedores') || [];
            const usuarios = await apiRequest('/usuarios') || [];

            let totalProd = productos.length;
            let totalStock = 0;
            let stockBajo = 0;
            let valorInventario = 0;

            productos.forEach(p => {
                let qty = p.stockActual || 0;
                let min = p.stockMinimo || 5;
                valorInventario += qty * (p.precioVenta || 0);
                totalStock += qty;
                if (qty <= min) stockBajo++;
            });

            /*** mapear métricas clave en la tabla resumen. @author RADJ */
            rows = [
                ['Total de Productos en Catálogo', `${totalProd}`, 'Productos registrados'],
                ['Unidades de Stock Físico', `${totalStock} uds`, 'Total unidades en inventario'],
                ['Productos con Stock Bajo', `<span style="color:#ef4444; font-weight:bold">${stockBajo}</span>`, 'Requieren reabastecimiento urgente'],
                ['Valoración de Inventario', `$${valorInventario.toLocaleString('es-CO', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`, 'En base a precios de venta comerciales'],
                ['Clientes Registrados', `${clientes.length}`, 'Base de datos de clientes'],
                ['Proveedores Registrados', `${proveedores.length}`, 'Suministradores comerciales'],
                ['Usuarios en el Sistema', `${usuarios.length}`, 'Cuentas con acceso administrativo']
            ];

            /*** crear caja de resumen ejecutivo con marca temporal. @author RADJ */
            resumenHtml = `
                <div class="summary-box">
                    <p><strong>Fecha del Resumen:</strong> ${nowStr}</p>
                    <p><strong>Estado de Operación:</strong> Operando normalmente</p>
                </div>
            `;
        } else if (tipo === 'ventas') {
            titulo = 'Reporte Histórico de Ventas';
            headers = ['ID Venta', 'Fecha / Hora', 'Cliente', 'Procesado por', 'Productos', 'Total'];
            const dataVentas = await apiRequest('/ventas') || [];
            const dataClientes = await apiRequest('/clientes') || [];
            const dataUsuarios = await apiRequest('/usuarios') || [];

           
/*** crear mapas para cruzar ids con nombres. @author RADJ */
            const clientesMap = {};
            dataClientes.forEach(c => {
                clientesMap[c.id] = c.nombre;
            });

            const usuariosMap = {};
            dataUsuarios.forEach(u => {
                usuariosMap[u.id] = `${u.nombre} ${u.apellido || ''}`;
            });

            let totalVentasMonto = 0;

           
/*** ordenar por fecha descendente. @author RADJ */
            dataVentas.sort((a, b) => new Date(b.fechaHora) - new Date(a.fechaHora));

            rows = dataVentas.map(v => {
                totalVentasMonto += v.valorTotal || 0;
                const fecha = v.fechaHora ? new Date(v.fechaHora).toLocaleString('es-CO') : '—';
                const cliente = v.idCliente ? (clientesMap[v.idCliente] || `Cliente #${v.idCliente}`) : 'Sin cliente';
                const usuario = v.idUsuario ? (usuariosMap[v.idUsuario] || `Usuario #${v.idUsuario}`) : 'Sistema';
                const nProductos = v.detalles ? v.detalles.length : 0;

                return [
                    `#${v.id}`,
                    fecha,
                    cliente,
                    usuario,
                    `${nProductos} producto(s)`,
                    `$${v.valorTotal?.toLocaleString('es-CO')}`
                ];
            });

            resumenHtml = `
                <div class="summary-box">
                    <p><strong>Total de Ventas Realizadas:</strong> ${dataVentas.length}</p>
                    <p><strong>Monto Total Recaudado:</strong> $${totalVentasMonto.toLocaleString('es-CO')}</p>
                </div>
            `;
        } else if (tipo === 'ganancias') {
            titulo = 'Reporte de Ganancias y Rentabilidad';
            headers = ['ID Venta', 'Fecha / Hora', 'Ingreso (Venta)', 'Costo total', 'Ganancia Neta', 'Margen %'];
            const dataVentas = await apiRequest('/ventas') || [];
            const dataProductos = await apiRequest('/productos') || [];

           
/*** map productos por id para obtener precios de compra. @author RADJ */
            const prodMap = {};
            dataProductos.forEach(p => {
                prodMap[p.id] = p;
            });

            let globalIngresos = 0;
            let globalCostos = 0;

           
/*** ordenar por fecha descendente. @author RADJ */
            dataVentas.sort((a, b) => new Date(b.fechaHora) - new Date(a.fechaHora));

            rows = dataVentas.map(v => {
                let ingreso = v.valorTotal || 0;
                let costoTotal = 0;

                if (v.detalles) {
                    v.detalles.forEach(d => {
                        const prod = prodMap[d.idProducto];
                        const costoCompra = prod ? (prod.precioCompra || 0) : 0;
                        costoTotal += (d.cantidad || 0) * costoCompra;
                    });
                }

                const ganancia = ingreso - costoTotal;
                const margen = ingreso > 0 ? ((ganancia / ingreso) * 100).toFixed(1) : '0.0';

                globalIngresos += ingreso;
                globalCostos += costoTotal;

                return [
                    `#${v.id}`,
                    v.fechaHora ? new Date(v.fechaHora).toLocaleString('es-CO') : '—',
                    `$${ingreso.toLocaleString('es-CO')}`,
                    `$${costoTotal.toLocaleString('es-CO')}`,
                    `<span style="${ganancia >= 0 ? 'color:#10b981' : 'color:#ef4444'}">$${ganancia.toLocaleString('es-CO')}</span>`,
                    `${margen}%`
                ];
            });

            const globalGanancia = globalIngresos - globalCostos;
            const globalMargen = globalIngresos > 0 ? ((globalGanancia / globalIngresos) * 100).toFixed(1) : '0.0';

            resumenHtml = `
                <div class="summary-box">
                    <p><strong>Monto Total de Ventas (Ingresos):</strong> $${globalIngresos.toLocaleString('es-CO')}</p>
                    <p><strong>Costo de Mercancía Vendida (Costos):</strong> $${globalCostos.toLocaleString('es-CO')}</p>
                    <p><strong>Utilidad Operativa Neta:</strong> $${globalGanancia.toLocaleString('es-CO')}</p>
                    <p><strong>Margen de Rentabilidad Promedio:</strong> ${globalMargen}%</p>
                </div>
            `;
        }

        /*** crear el documento html de impresión. @author RADJ */
        const html = `
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <title>${titulo}</title>
                <style>
                    body {
                        font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
                        color: #1e293b;
                        padding: 1.5rem;
                        background: #ffffff;
                        line-height: 1.5;
                    }
                    .header {
                        border-bottom: 2px solid #6366f1;
                        padding-bottom: 1rem;
                        margin-bottom: 1.5rem;
                    }
                    .header h1 {
                        margin: 0;
                        font-size: 1.8rem;
                        color: #4f46e5;
                    }
                    .header p {
                        margin: 0.25rem 0 0 0;
                        font-size: 0.85rem;
                        color: #64748b;
                    }
                    .summary-box {
                        background: #f8fafc;
                        border: 1px solid #e2e8f0;
                        border-radius: 8px;
                        padding: 1rem 1.25rem;
                        margin-bottom: 1.5rem;
                    }
                    .summary-box p {
                        margin: 0.35rem 0;
                        font-size: 0.9rem;
                    }
                    .summary-box p strong {
                        color: #0f172a;
                    }
                    table {
                        width: 100%;
                        border-collapse: collapse;
                        font-size: 0.85rem;
                        margin-top: 1rem;
                    }
                    th {
                        background: #4f46e5;
                        color: #ffffff;
                        text-align: left;
                        padding: 0.6rem 0.8rem;
                        font-weight: 600;
                        text-transform: uppercase;
                        font-size: 0.75rem;
                        letter-spacing: 0.05em;
                    }
                    td {
                        padding: 0.6rem 0.8rem;
                        border-bottom: 1px solid #e2e8f0;
                    }
                    tr:nth-child(even) td {
                        background: #f8fafc;
                    }
                    .footer {
                        margin-top: 2rem;
                        border-top: 1px solid #e2e8f0;
                        padding-top: 0.75rem;
                        font-size: 0.75rem;
                        color: #94a3b8;
                        text-align: center;
                    }
                    @page {
                        size: letter;
                        margin: 18mm 15mm 18mm 15mm;
                    }
                    @media print {
                        .no-print { display: none; }
                        body { 
                            padding: 0; 
                            margin: 0;
                            background: #ffffff;
                        }
                        tr {
                            page-break-inside: avoid;
                            break-inside: avoid;
                        }
                        thead {
                            display: table-header-group;
                        }
                    }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>${titulo}</h1>
                    <p>Reporte Oficial generado el ${nowStr}</p>
                </div>
                
                ${resumenHtml}

                <table>
                    <thead>
                        <tr>
                            ${headers.map(h => `<th>${h}</th>`).join('')}
                        </tr>
                    </thead>
                    <tbody>
                        ${rows.map(row => `
                            <tr>
                                ${row.map(cell => `<td>${cell}</td>`).join('')}
                            </tr>
                        `).join('')}
                    </tbody>
                </table>

                <div class="footer">
                    Copyright © 2026 Rubiel Andrés Díaz | Contacto: andresrubiel@gmail.com
                </div>
                <script>
                    window.onload = function() {
                        window.print();
                    }
                </script>
            </body>
            </html>
        `;

        /*** abrir ventana de impresión e inyectar documento. @author RADJ */
        const win = window.open('', '_blank');
        if (!win) {
            alert("Por favor habilite las ventanas emergentes en su navegador.");
            return;
        }

        win.document.open();
        win.document.write(html);
        win.document.close();
    } catch (e) {
        console.error("Error al generar reporte:", e);
        alert("Error al generar el reporte: " + e.message);
    }
};
