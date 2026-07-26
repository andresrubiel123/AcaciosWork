package com.acacioswork.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** controlador mvc para servir vistas de thymeleaf. @author RADJ */
@Controller
public class ViewController {

    /** mapea la ruta raíz y la página de login. @author RADJ */
    @GetMapping({"/", "/login", "/login.html"})
    public String login() {
        return "login";
    }

    /** mapea el dashboard de administrador. @author RADJ */
    @GetMapping({"/administrador-dashboard", "/administrador-dashboard.html"})
    public String administradorDashboard() {
        return "administrador-dashboard";
    }

    /** mapea el dashboard de auxiliar. @author RADJ */
    @GetMapping({"/auxiliar-dashboard", "/auxiliar-dashboard.html"})
    public String auxiliarDashboard() {
        return "auxiliar-dashboard";
    }
}
