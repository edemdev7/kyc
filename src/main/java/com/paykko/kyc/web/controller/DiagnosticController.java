package com.paykko.kyc.web.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataAccessException;

@RestController
@RequestMapping("/diagnostic")
public class DiagnosticController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/db-status")
    public Map<String, Object> checkDatabaseConnection() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Exécute une requête SQL simple
            String dbVersion = jdbcTemplate.queryForObject("SELECT version()", String.class);
            
            result.put("status", "connected");
            result.put("databaseVersion", dbVersion);
            result.put("message", "Successfully connected to PostgreSQL");
        } catch (DataAccessException e) {
            result.put("status", "error");
            result.put("message", "Failed to connect: " + e.getMessage());
        }
        
        return result;
    }
}