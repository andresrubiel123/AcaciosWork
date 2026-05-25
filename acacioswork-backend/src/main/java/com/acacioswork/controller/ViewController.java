package com.acacioswork.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Controlador MVC para servir vistas de Thymeleaf. @author RADJ */
@Controller
public class ViewController {

    /** Mapea la ruta raíz y la página de login. @author RADJ */
    @GetMapping({"/", "/login", "/login.html"})
    public String login() {
        return "login";
    }

    /** Mapea el dashboard de administrador. @author RADJ */
    @GetMapping({"/administrador-dashboard", "/administrador-dashboard.html"})
    public String administradorDashboard() {
        return "administrador-dashboard";
    }

    /** Mapea el dashboard de auxiliar. @author RADJ */
    @GetMapping({"/auxiliar-dashboard", "/auxiliar-dashboard.html"})
    public String auxiliarDashboard() {
        return "auxiliar-dashboard";
    }
}
