/*** módulo javascript del frontend de acacioswork. @author RADJ */
/*** ganancias-mensuales.js - renderizado del gráfico de tendencia de ganancias utilizando chart.js. @author RADJ */

/*** carga e inicializa el gráfico de tendencia de ganancias mensuales. @author RADJ */
window.loadReportesChart = async function(targetYear) {
    const ctx = document.getElementById('salesChart');
    if (!ctx) return;

    let year = targetYear;
    const yearSelect = document.getElementById('sales-year-select');
    if (yearSelect && !targetYear) {
        if (!yearSelect.value) {
            yearSelect.value = new Date().getFullYear().toString();
        }
        year = parseInt(yearSelect.value);
    }
    if (!year) {
        year = new Date().getFullYear();
    }

    try {
        /*** obtener listado de ventas y productos desde la api para el cálculo. @author RADJ */
        const [ventas, productos] = await Promise.all([
            apiRequest('/ventas') || [],
            apiRequest('/productos') || []
        ]);

        const prodMap = {};
        productos.forEach(p => {
            prodMap[p.id] = p;
        });

        const monthlyData = Array(12).fill(0);

        /*** agrupar la suma de ganancias de cada venta por su mes correspondiente. @author RADJ */
        ventas.forEach(v => {
            if (v.fechaHora) {
                const fecha = new Date(v.fechaHora);
                if (fecha.getFullYear() === year) {
                    const mesIndex = fecha.getMonth();
/*** 0-11. @author RADJ */

                    let totalVenta = v.valorTotal || 0;
                    if (!totalVenta && v.detalles && v.detalles.length > 0) {
                        totalVenta = v.detalles.reduce((sum, d) => sum + (d.subtotal || 0), 0);
                    }

                    let costoVenta = 0;
                    if (v.detalles) {
                        v.detalles.forEach(d => {
                            const prod = prodMap[d.idProducto];
                            const precioCompra = prod ? (prod.precioCompra || 0) : 0;
                            costoVenta += (d.cantidad || 0) * precioCompra;
                        });
                    }

                    const gananciaVenta = totalVenta - costoVenta;
                    monthlyData[mesIndex] += gananciaVenta;
                }
            }
        });

        /*** destruir la instancia previa si existe para evitar solapamientos. @author RADJ */
        if (AppState.salesChartInstance) {
            AppState.salesChartInstance.destroy();
        }

        /*** inicializar chart.js con la tendencia mensual. @author RADJ */
        AppState.salesChartInstance = new Chart(ctx, {
            type: 'line',
            data: {
                labels: ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'],
                datasets: [{
                    label: 'Ganancias Mensuales (' + year + ')',
                    data: monthlyData,
                    borderColor: '#6366f1',
                    backgroundColor: 'rgba(99, 102, 241, 0.1)',
                    borderWidth: 3,
                    tension: 0.4,
                    fill: true,
                    pointBackgroundColor: '#f97316',
                    pointBorderColor: '#ffffff',
                    pointRadius: 6,
                    pointHoverRadius: 8
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        labels: {
                            color: '#f8fafc',
                            font: {
                                family: 'Inter',
                                size: 12,
                                weight: '600'
                            }
                        }
                    },
                    tooltip: {
                        backgroundColor: '#1e293b',
                        titleColor: '#f8fafc',
                        bodyColor: '#f8fafc',
                        borderColor: 'rgba(255,255,255,0.1)',
                        borderWidth: 1,
                        callbacks: {
                            label: function (context) {
                                let label = context.dataset.label || '';
                                if (label) {
                                    label += ': ';
                                }
                                if (context.parsed.y !== null) {
                                    label += new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }).format(context.parsed.y);
                                }
                                return label;
                            }
                        }
                    }
                },
                scales: {
                    x: {
                        grid: {
                            color: 'rgba(255, 255, 255, 0.05)'
                        },
                        ticks: {
                            color: '#94a3b8',
                            font: {
                                family: 'Inter'
                            }
                        }
                    },
                    y: {
                        title: {
                            display: true,
                            text: 'COP',
                            color: '#94a3b8',
                            align: 'end',
                            font: {
                                family: 'Inter',
                                size: 11,
                                weight: '600'
                            }
                        },
                        grid: {
                            color: 'rgba(255, 255, 255, 0.05)'
                        },
                        ticks: {
                            color: '#94a3b8',
                            font: {
                                family: 'Inter'
                            },
                            padding: 10,
                            callback: function (value) {
                                return value.toLocaleString('es-CO');
                            }
                        }
                    }
                }
            }
        });
    } catch (error) {
        console.error("Error al cargar gráfico de ventas:", error);
    }
};

/*** Activar modo edición manual al hacer doble clic. @author RADJ */
window.enableManualYearInput = function() {
    const select = document.getElementById('sales-year-select');
    const input = document.getElementById('sales-year-input');
    if (select && input) {
        input.value = ''; // Queda totalmente limpio para escribir inmediatamente. @author RADJ
        select.style.display = 'none';
        input.style.display = 'inline-block';
        input.focus();
    }
};

/*** Desactivar modo edición manual y aplicar el año seleccionado. @author RADJ */
window.disableManualYearInput = function(val) {
    const select = document.getElementById('sales-year-select');
    const input = document.getElementById('sales-year-input');
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
            window.onSalesYearChange(yearNum);
        }
        input.style.display = 'none';
        select.style.display = 'inline-block';
    }
};

/*** Escucha el cambio de año y actualiza el gráfico. @author RADJ */
window.onSalesYearChange = function(year) {
    window.loadReportesChart(parseInt(year));
};
