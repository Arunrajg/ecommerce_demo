package com.demo.ecommerce.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> index() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("app", "E-Commerce Demo API");
        info.put("status", "running");

        Map<String, String> endpoints = new LinkedHashMap<>();
        endpoints.put("GET  /api/products",            "List all products (optional ?search=)");
        endpoints.put("GET  /api/products/in-stock",   "In-stock products only");
        endpoints.put("GET  /api/products/{id}",       "Get product by ID");
        endpoints.put("POST /api/products",            "Create a product");
        endpoints.put("PUT  /api/products/{id}",       "Update a product");
        endpoints.put("DELETE /api/products/{id}",     "Delete a product");
        endpoints.put("GET  /api/orders",              "List orders (optional ?email= or ?status=)");
        endpoints.put("GET  /api/orders/{id}",         "Get order by ID");
        endpoints.put("POST /api/orders",              "Place an order");
        endpoints.put("PATCH /api/orders/{id}/status", "Update order status");
        endpoints.put("DELETE /api/orders/{id}",       "Cancel an order");
        endpoints.put("GET  /h2-console",              "H2 database console");

        info.put("endpoints", endpoints);
        return info;
    }
}
