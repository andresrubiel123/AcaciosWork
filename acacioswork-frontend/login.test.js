/** Pruebas de integración para login.html. @author RADJ */

const fs = require('fs');
const path = require('path');

// Leer el archivo login.html
const htmlPath = path.resolve(__dirname, './login.html');
const htmlContent = fs.readFileSync(htmlPath, 'utf8');

describe('Pruebas de la interfaz de Login (login.html)', () => {
    let originalLocation;

    beforeEach(() => {
        // Mock de window.location
        originalLocation = window.location;
        delete window.location;
        window.location = {
            href: "http://localhost/login.html"
        };

        // Mock de apiRequest global
        window.apiRequest = jest.fn();
        localStorage.clear();

        // Cargar el HTML en JSDOM
        document.documentElement.innerHTML = htmlContent;

        // Extraer y ejecutar el script inline de login.html
        const scripts = document.querySelectorAll('script');
        // El segundo tag de script (índice 1) contiene la lógica del formulario de login
        const loginScriptContent = scripts[1].textContent;
        
        // Ejecutar la función en el ámbito global (donde document y window están definidos)
        const executeScript = new Function(loginScriptContent);
        executeScript();
    });

    afterEach(() => {
        window.location = originalLocation;
        jest.restoreAllMocks();
    });

    test('debe inicializar el formulario con los campos vacíos', () => {
        expect(document.getElementById('username').value).toBe('');
        expect(document.getElementById('password').value).toBe('');
        expect(document.getElementById('errorMsg').style.display).toBe('none');
    });

    test('debe iniciar sesión exitosamente para un Administrador (idRol = 1)', async () => {
        const mockUserResponse = {
            token: "jwt_token_admin_mock",
            usuario: {
                id: 1,
                nombre: "Andres",
                apellido: "Rubiel",
                usuario: "admin",
                idRol: 1
            }
        };
        window.apiRequest.mockResolvedValue(mockUserResponse);

        // Llenar campos
        document.getElementById('username').value = 'admin';
        document.getElementById('password').value = 'clave123';

        // Disparar submit
        const form = document.getElementById('loginForm');
        form.dispatchEvent(new Event('submit'));

        // Esperar a que se resuelvan las promesas
        await new Promise(process.nextTick);

        // Verificaciones de API
        expect(window.apiRequest).toHaveBeenCalledWith("/usuarios/login", "POST", {
            usuario: "admin",
            clave: "clave123"
        });

        // Verificaciones de almacenamiento en localStorage
        expect(localStorage.getItem("jwt_token")).toBe("jwt_token_admin_mock");
        expect(localStorage.getItem("user_name")).toBe("Andres Rubiel");
        expect(localStorage.getItem("user_role")).toBe("1");
        expect(JSON.parse(localStorage.getItem("usuario"))).toEqual(mockUserResponse.usuario);

        // Verificación de redirección
        expect(window.location.href).toBe("dashboard.html");
    });

    test('debe iniciar sesión exitosamente para un Auxiliar (idRol = 2)', async () => {
        const mockUserResponse = {
            token: "jwt_token_aux_mock",
            usuario: {
                id: 2,
                nombre: "Carlos",
                apellido: "Gomez",
                usuario: "auxiliar",
                idRol: 2
            }
        };
        window.apiRequest.mockResolvedValue(mockUserResponse);

        document.getElementById('username').value = 'auxiliar';
        document.getElementById('password').value = 'clave456';

        const form = document.getElementById('loginForm');
        form.dispatchEvent(new Event('submit'));

        await new Promise(process.nextTick);

        // Verificación de almacenamiento en localStorage
        expect(localStorage.getItem("jwt_token")).toBe("jwt_token_aux_mock");
        expect(localStorage.getItem("user_role")).toBe("2");

        // Verificación de redirección según rol auxiliar
        expect(window.location.href).toBe("auxiliar-dashboard.html");
    });

    test('debe mostrar mensaje de error en credenciales inválidas', async () => {
        window.apiRequest.mockRejectedValue(new Error("Credenciales incorrectas"));

        document.getElementById('username').value = 'admin';
        document.getElementById('password').value = 'clave_incorrecta';

        const form = document.getElementById('loginForm');
        form.dispatchEvent(new Event('submit'));

        // Verificar feedback visual temporal de cargando
        const loginBtn = document.getElementById('loginBtn');
        expect(loginBtn.innerText).toBe("Verificando...");
        expect(loginBtn.disabled).toBe(true);

        await new Promise(process.nextTick);

        // Mensaje de error visible
        const errorMsg = document.getElementById('errorMsg');
        expect(errorMsg.style.display).toBe('block');
        expect(errorMsg.innerText).toBe("Credenciales incorrectas o error de conexión.");

        // Botón habilitado nuevamente
        expect(loginBtn.innerText).toBe("Iniciar Sesión");
        expect(loginBtn.disabled).toBe(false);
    });

    test('debe limpiar el mensaje de error al escribir en los inputs', () => {
        const errorMsg = document.getElementById('errorMsg');
        errorMsg.style.display = 'block';

        const usernameInput = document.getElementById('username');
        usernameInput.value = 'a';
        usernameInput.dispatchEvent(new Event('input'));

        expect(errorMsg.style.display).toBe('none');
    });
});
