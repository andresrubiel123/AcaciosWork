/*** módulo javascript del frontend de acacioswork. @author RADJ */
/*** configuracion.js - lógica de configuración global del sistema y datos de facturación. @author RADJ */

/*** carga de la configuración del sistema. @author RADJ */
window.loadConfiguracion = async function() {
    try {
        AppState.globalConfig = await apiRequest('/configuracion', 'GET');
        if (AppState.globalConfig) {
            const el = (id) => document.getElementById(id);
            if (el('cfg-nombre-empresa')) el('cfg-nombre-empresa').value = AppState.globalConfig.nombreEmpresa || '';
            if (el('cfg-impresora')) el('cfg-impresora').value = AppState.globalConfig.impresoraActiva || '';
            if (el('cfg-ticket-logo')) el('cfg-ticket-logo').value = AppState.globalConfig.ticketLogotipo || '';
            if (el('cfg-ticket-encabezado')) el('cfg-ticket-encabezado').value = AppState.globalConfig.ticketEncabezado || '';
            if (el('cfg-ticket-pie')) el('cfg-ticket-pie').value = AppState.globalConfig.ticketPiePagina || '';
            if (el('cfg-ticket-ancho')) el('cfg-ticket-ancho').value = AppState.globalConfig.ticketAnchoMm || 80;
            if (el('cfg-ticket-alto')) el('cfg-ticket-alto').value = AppState.globalConfig.ticketAltoMm || 297;
            if (el('cfg-ticket-margen-izq')) el('cfg-ticket-margen-izq').value = AppState.globalConfig.ticketMargenIzq || 5;
            if (el('cfg-ticket-margen-der')) el('cfg-ticket-margen-der').value = AppState.globalConfig.ticketMargenDer || 5;

            // Cargar configuración de hardware desde localStorage (Opción B)
            let hardwareConfig = {};
            const localData = localStorage.getItem('local_hardware_config');
            if (localData) {
                try {
                    hardwareConfig = JSON.parse(localData);
                } catch (e) {
                    console.error("Error al parsear configuración local de hardware, usando base de datos", e);
                }
            }

            // Mezclar con valores del servidor como fallback
            const barcodeMode = hardwareConfig.barcodeMode || AppState.globalConfig.barcodeMode || 'KEYBOARD';
            const barcodePort = hardwareConfig.barcodePort !== undefined ? hardwareConfig.barcodePort : (AppState.globalConfig.barcodePort || '');
            const scaleEnabled = hardwareConfig.scaleEnabled !== undefined ? hardwareConfig.scaleEnabled : (AppState.globalConfig.scaleEnabled ?? false);
            const scaleProtocol = hardwareConfig.scaleProtocol || AppState.globalConfig.scaleProtocol || 'CAS';
            const scalePort = hardwareConfig.scalePort !== undefined ? hardwareConfig.scalePort : (AppState.globalConfig.scalePort || '');
            const scaleBaudrate = hardwareConfig.scaleBaudrate || AppState.globalConfig.scaleBaudrate || 9600;
            const printerInterface = hardwareConfig.printerInterface || AppState.globalConfig.printerInterface || 'SYSTEM';
            const printerPort = hardwareConfig.printerPort !== undefined ? hardwareConfig.printerPort : (AppState.globalConfig.printerPort || '');
            const cajonConectadoImpresora = hardwareConfig.cajonConectadoImpresora !== undefined ? hardwareConfig.cajonConectadoImpresora : (AppState.globalConfig.cajonConectadoImpresora ?? true);
            const cajonComando = hardwareConfig.cajonComando || AppState.globalConfig.cajonComando || '27,112,0,25,250';
            const datafonoIntegracion = hardwareConfig.datafonoIntegracion !== undefined ? hardwareConfig.datafonoIntegracion : (AppState.globalConfig.datafonoIntegracion ?? false);
            const datafonoProveedor = hardwareConfig.datafonoProveedor || AppState.globalConfig.datafonoProveedor || 'REDEBAN';
            const datafonoPuerto = hardwareConfig.datafonoPuerto !== undefined ? hardwareConfig.datafonoPuerto : (AppState.globalConfig.datafonoPuerto || '');
            const datafonoTerminalId = hardwareConfig.datafonoTerminalId !== undefined ? hardwareConfig.datafonoTerminalId : (AppState.globalConfig.datafonoTerminalId || '');

            // Poblar inputs en la interfaz
            if (el('cfg-barcode-mode')) el('cfg-barcode-mode').value = barcodeMode;
            if (el('cfg-barcode-port')) el('cfg-barcode-port').value = barcodePort;
            if (el('cfg-scale-enabled')) el('cfg-scale-enabled').value = String(scaleEnabled);
            if (el('cfg-scale-protocol')) el('cfg-scale-protocol').value = scaleProtocol;
            if (el('cfg-scale-port')) el('cfg-scale-port').value = scalePort;
            if (el('cfg-scale-baudrate')) el('cfg-scale-baudrate').value = String(scaleBaudrate);
            if (el('cfg-printer-interface')) el('cfg-printer-interface').value = printerInterface;
            if (el('cfg-printer-port')) el('cfg-printer-port').value = printerPort;
            if (el('cfg-cajon-conectado')) el('cfg-cajon-conectado').value = String(cajonConectadoImpresora);
            if (el('cfg-cajon-comando')) el('cfg-cajon-comando').value = cajonComando;
            if (el('cfg-datafono-integracion')) el('cfg-datafono-integracion').value = String(datafonoIntegracion);
            if (el('cfg-datafono-proveedor')) el('cfg-datafono-proveedor').value = datafonoProveedor;
            if (el('cfg-datafono-puerto')) el('cfg-datafono-puerto').value = datafonoPuerto;
            if (el('cfg-datafono-terminal-id')) el('cfg-datafono-terminal-id').value = datafonoTerminalId;

            // Si no estaba en localStorage, guardarlo para la próxima vez
            if (!localData) {
                const initialLocalConfig = {
                    barcodeMode, barcodePort, scaleEnabled, scaleProtocol, scalePort,
                    scaleBaudrate, printerInterface, printerPort, cajonConectadoImpresora,
                    cajonComando, datafonoIntegracion, datafonoProveedor, datafonoPuerto, datafonoTerminalId
                };
                localStorage.setItem('local_hardware_config', JSON.stringify(initialLocalConfig));
            }

            // Ejecutar visualización dinámica de campos
            window.toggleHardwareFields();
        }
    } catch (e) {
        console.error("Error cargando configuración", e);
    }
};

/*** controla la visibilidad de los campos según la conectividad del hardware. @author RADJ */
window.toggleHardwareFields = function() {
    const el = (id) => document.getElementById(id);
    if (!el('cfg-barcode-mode')) return;

    // Visibilidad del puerto del lector
    const isBarcodeSerial = el('cfg-barcode-mode').value === 'SERIAL';
    const groupBarcodePort = el('group-barcode-port');
    if (groupBarcodePort) groupBarcodePort.style.display = isBarcodeSerial ? 'block' : 'none';

    // Visibilidad de los campos de la báscula
    const isScaleEnabled = el('cfg-scale-enabled').value === 'true';
    const scaleFields = ['group-scale-protocol', 'group-scale-port', 'group-scale-baudrate'];
    scaleFields.forEach(fieldId => {
        const field = el(fieldId);
        if (field) field.style.display = isScaleEnabled ? 'block' : 'none';
    });

    // Visibilidad del puerto de la impresora
    const printerInterface = el('cfg-printer-interface').value;
    const groupPrinterPort = el('group-printer-port');
    if (groupPrinterPort) {
        groupPrinterPort.style.display = (printerInterface === 'ESC_POS_RAW' || printerInterface === 'NETWORK') ? 'block' : 'none';
    }

    // Visibilidad de los campos de integración del datáfono
    const isDatafonoEnabled = el('cfg-datafono-integracion').value === 'true';
    const datafonoFields = ['group-datafono-proveedor', 'group-datafono-puerto', 'group-datafono-terminal'];
    datafonoFields.forEach(fieldId => {
        const field = el(fieldId);
        if (field) field.style.display = isDatafonoEnabled ? 'block' : 'none';
    });
};

/*** guarda la configuración del sistema y recarga la página para aplicar cambios. @author RADJ */
window.guardarConfiguracion = async function() {
    const el = (id) => document.getElementById(id);

    // 1. Guardar configuración de hardware en localStorage (local de la máquina)
    const localHardwareConfig = {
        barcodeMode: el('cfg-barcode-mode').value,
        barcodePort: el('cfg-barcode-port').value,
        scaleEnabled: el('cfg-scale-enabled').value === 'true',
        scaleProtocol: el('cfg-scale-protocol').value,
        scalePort: el('cfg-scale-port').value,
        scaleBaudrate: parseInt(el('cfg-scale-baudrate').value) || 9600,
        printerInterface: el('cfg-printer-interface').value,
        printerPort: el('cfg-printer-port').value,
        cajonConectadoImpresora: el('cfg-cajon-conectado').value === 'true',
        cajonComando: el('cfg-cajon-comando').value,
        datafonoIntegracion: el('cfg-datafono-integracion').value === 'true',
        datafonoProveedor: el('cfg-datafono-proveedor').value,
        datafonoPuerto: el('cfg-datafono-puerto').value,
        datafonoTerminalId: el('cfg-datafono-terminal-id').value
    };
    localStorage.setItem('local_hardware_config', JSON.stringify(localHardwareConfig));

    // 2. Enviar payload de configuración general y hardware al backend
    const payload = {
        nombreEmpresa: el('cfg-nombre-empresa').value,
        idioma: AppState.globalConfig?.idioma || 'es',
        moneda: AppState.globalConfig?.moneda || 'COP',
        lectorCodigoBarras: localHardwareConfig.barcodeMode === 'KEYBOARD' ? 'USB HID' : localHardwareConfig.barcodePort,
        impresoraActiva: el('cfg-impresora').value,
        ticketLogotipo: el('cfg-ticket-logo').value,
        ticketEncabezado: el('cfg-ticket-encabezado').value,
        ticketPiePagina: el('cfg-ticket-pie').value,
        ticketAnchoMm: parseInt(el('cfg-ticket-ancho').value) || 80,
        ticketAltoMm: parseInt(el('cfg-ticket-alto').value) || 297,
        ticketMargenIzq: parseInt(el('cfg-ticket-margen-izq').value) || 5,
        ticketMargenDer: parseInt(el('cfg-ticket-margen-der').value) || 5,

        // Campos de hardware como backup/default
        barcodeMode: localHardwareConfig.barcodeMode,
        barcodePort: localHardwareConfig.barcodePort,
        scaleEnabled: localHardwareConfig.scaleEnabled,
        scaleProtocol: localHardwareConfig.scaleProtocol,
        scalePort: localHardwareConfig.scalePort,
        scaleBaudrate: localHardwareConfig.scaleBaudrate,
        printerInterface: localHardwareConfig.printerInterface,
        printerPort: localHardwareConfig.printerPort,
        cajonConectadoImpresora: localHardwareConfig.cajonConectadoImpresora,
        cajonComando: localHardwareConfig.cajonComando,
        datafonoIntegracion: localHardwareConfig.datafonoIntegracion,
        datafonoProveedor: localHardwareConfig.datafonoProveedor,
        datafonoPuerto: localHardwareConfig.datafonoPuerto,
        datafonoTerminalId: localHardwareConfig.datafonoTerminalId
    };

    try {
        AppState.globalConfig = await apiRequest('/configuracion', 'PUT', payload);
        alert("Configuración guardada exitosamente");
        
/*** recargar para aplicar cambios de moneda/estilo. @author RADJ */
        location.reload();
    } catch (e) {
        alert("Error al guardar configuración: " + e.message);
    }
};

/*** alterna entre las pestañas internas de configuración. @author RADJ */
window.switchConfigTab = function(tabName) {
    document.querySelectorAll('.config-tab-btn').forEach(b => b.classList.remove('active'));
    document.querySelectorAll('.config-tab-content').forEach(c => c.style.display = 'none');
    
    if (event && event.target) {
        event.target.classList.add('active');
    }
    
    const targetTab = document.getElementById('config-tab-' + tabName);
    if (targetTab) {
        targetTab.style.display = 'block';
    }
};
