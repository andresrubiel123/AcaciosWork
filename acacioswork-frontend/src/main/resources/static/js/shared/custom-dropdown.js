/*** módulo javascript del frontend de acacioswork. @author RADJ */
/*** custom-dropdown.js - selector de clientes personalizado con diseño adaptado, búsqueda y accesos modales. @author RADJ */

/*** Inicialización del selector de clientes personalizado con diseño adaptado y búsqueda por doble clic. @author RADJ */
window.initCustomClientDropdown = function() {
    const select = document.getElementById('client-select');
    if (!select) return;
    
    // Si ya está envuelto en nuestro componente, no volver a envolver
    if (select.parentNode.classList.contains('custom-client-dropdown')) {
        return;
    }
    
    // Crear contenedor principal
    const wrapper = document.createElement('div');
    wrapper.className = 'custom-client-dropdown';
    
    // Insertar wrapper en el DOM justo antes del select
    select.parentNode.insertBefore(wrapper, select);
    
    // Mover el select adentro del wrapper
    wrapper.appendChild(select);
    select.style.display = 'none';
    
    // Crear el elemento disparador (trigger) que contiene el input
    const trigger = document.createElement('div');
    trigger.className = 'client-select-trigger';
    trigger.id = 'client-select-trigger';
    trigger.innerHTML = `
        <input type="text" id="client-select-input" class="client-select-input" value="— Venta sin cliente registrado —" readonly autocomplete="off">
        <span class="dropdown-arrow">▼</span>
    `;
    wrapper.appendChild(trigger);
    
    const input = trigger.querySelector('#client-select-input');
    
    // Crear contenedor de la lista de opciones
    const list = document.createElement('div');
    list.className = 'client-dropdown-list';
    list.id = 'client-dropdown-list';
    wrapper.appendChild(list);
    
    // Alternar menú desplegable al hacer clic en el disparador (solo si está en readonly)
    trigger.addEventListener('click', (e) => {
        if (!input.readOnly) {
            e.stopPropagation();
            return;
        }
        
        e.stopPropagation();
        const isOpen = list.style.display === 'block';
        
        // Cerrar otros dropdowns del mismo tipo si existen
        document.querySelectorAll('.client-dropdown-list').forEach(l => l.style.display = 'none');
        document.querySelectorAll('.custom-client-dropdown').forEach(w => w.classList.remove('open'));
        
        if (isOpen) {
            list.style.display = 'none';
            wrapper.classList.remove('open');
        } else {
            list.style.display = 'block';
            wrapper.classList.add('open');
            // Hacer scroll hasta la opción actualmente seleccionada
            const selectedItem = list.querySelector('.client-dropdown-item.selected');
            if (selectedItem) {
                list.scrollTop = selectedItem.offsetTop - 10;
            }
        }
    });
    
    // Habilitar escritura/búsqueda al hacer doble clic
    trigger.addEventListener('dblclick', (e) => {
        e.stopPropagation();
        input.readOnly = false;
        input.value = ''; // Queda limpio para escribir
        input.focus();
        
        // Asegurarse de que todos los elementos sean visibles al limpiar la búsqueda
        Array.from(list.children).forEach(child => child.style.display = 'block');
        
        // Asegurarse de que el dropdown esté abierto
        list.style.display = 'block';
        wrapper.classList.add('open');
    });
    
    // Filtrar opciones en tiempo real a medida que el usuario escribe
    input.addEventListener('input', () => {
        const query = input.value.toLowerCase().trim();
        Array.from(list.children).forEach(child => {
            const text = child.textContent.toLowerCase();
            if (text.includes(query)) {
                child.style.display = 'block';
            } else {
                child.style.display = 'none';
            }
        });
    });
    
    // Navegación rápida por teclado
    input.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') {
            e.preventDefault();
            // Seleccionar primera opción visible
            const firstVisible = Array.from(list.children).find(child => child.style.display !== 'none');
            if (firstVisible) {
                firstVisible.click();
            } else {
                input.blur();
            }
        } else if (e.key === 'Escape') {
            input.blur();
        }
    });
    
    // Restaurar estado de sólo lectura al perder el foco
    input.addEventListener('blur', () => {
        setTimeout(() => {
            input.readOnly = true;
            
            // Re-sincronizar con el valor actual del select
            const selectedOption = select.options[select.selectedIndex];
            input.value = selectedOption ? selectedOption.textContent : '— Venta sin cliente registrado —';
            
            // Restablecer visibilidad de todos los elementos para la próxima vez
            Array.from(list.children).forEach(child => child.style.display = 'block');
            
            list.style.display = 'none';
            wrapper.classList.remove('open');
        }, 200);
    });
    
    // Cerrar el menú si se hace clic en cualquier otra parte del documento (sólo si no está en modo edición)
    document.addEventListener('click', () => {
        if (!input.readOnly) return;
        list.style.display = 'none';
        wrapper.classList.remove('open');
    });
    
    // Función interna para sincronizar las opciones del select real a los elementos visuales
    function syncCustomClientDropdown() {
        list.innerHTML = '';
        Array.from(select.options).forEach(opt => {
            const item = document.createElement('div');
            item.className = 'client-dropdown-item';
            item.textContent = opt.textContent;
            item.dataset.value = opt.value;
            
            if (opt.value == select.value) {
                item.classList.add('selected');
            }
            
            item.addEventListener('click', (e) => {
                e.stopPropagation();
                select.value = opt.value; // Esto llamará al descriptor modificado
                list.style.display = 'none';
                wrapper.classList.remove('open');
            });
            
            list.appendChild(item);
        });
        
        // Sincronizar texto inicial del disparador
        const selectedOption = select.options[select.selectedIndex];
        if (input) {
            input.value = selectedOption ? selectedOption.textContent : '— Venta sin cliente registrado —';
        }
    }
    
    // Observar cambios dinámicos de los elementos hijos (options) de la etiqueta select
    const observer = new MutationObserver(() => {
        syncCustomClientDropdown();
    });
    observer.observe(select, { childList: true });
    
    // Sobrescribir el descriptor de la propiedad 'value' para interceptar asignaciones de JS
    const descriptor = Object.getOwnPropertyDescriptor(HTMLSelectElement.prototype, 'value');
    Object.defineProperty(select, 'value', {
        get() {
            return descriptor.get.call(this);
        },
        set(val) {
            descriptor.set.call(this, val);
            
            // Sincronizar la UI visual
            const selectedOption = Array.from(this.options).find(opt => opt.value == val);
            if (input) {
                input.value = selectedOption ? selectedOption.textContent : '— Venta sin cliente registrado —';
            }
            
            Array.from(list.children).forEach(child => {
                if (child.dataset.value == val) {
                    child.classList.add('selected');
                } else {
                    child.classList.remove('selected');
                }
            });
        },
        configurable: true
    });
    
    // Carga inicial
    syncCustomClientDropdown();
};

// Auto inicializar al terminar la carga del DOM/Script
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => {
        setTimeout(window.initCustomClientDropdown, 100);
    });
} else {
    setTimeout(window.initCustomClientDropdown, 100);
}

/*** Mapea la apertura del modal de nuevo cliente de acuerdo al rol/interfaz cargada. @author RADJ */
window.abrirNuevoClienteModal = function() {
    if (typeof window.openModal === 'function') {
        window.openModal('cliente');
    } else if (typeof window.openClienteModal === 'function') {
        window.openClienteModal();
    } else if (typeof openClienteModal === 'function') {
        openClienteModal();
    } else {
        console.error('No se encontró la función para abrir el modal de clientes.');
        alert('No se pudo abrir la ventana de nuevo cliente en esta pantalla.');
    }
};
