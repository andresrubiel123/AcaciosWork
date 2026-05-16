package com.acacioswork.controller;

import com.acacioswork.model.Rol;
import com.acacioswork.service.RolService;
import com.acacioswork.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Controlador REST estandarizado para la gestión de roles. @author RADJ */
@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*")
public class RolController {

    @Autowired
    private RolService rolService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Rol>>> getAll() {
        List<Rol> roles = rolService.findAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Roles obtenidos con éxito", roles));
    }
}
