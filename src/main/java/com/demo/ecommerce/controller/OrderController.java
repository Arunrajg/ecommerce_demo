package com.demo.ecommerce.controller;

import com.demo.ecommerce.model.Order;
import com.demo.ecommerce.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<Order> getAllOrders(@RequestParam(required = false) String email,
                                    @RequestParam(required = false) Order.Status status) {
        if (email != null) return orderService.getOrdersByEmail(email);
        if (status != null) return orderService.getOrdersByStatus(status);
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public Order getOrder(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(
                request.customerName(),
                request.customerEmail(),
                request.items()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @PatchMapping("/{id}/status")
    public Order updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Order.Status status = Order.Status.valueOf(body.get("status").toUpperCase());
        return orderService.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }

    public record CreateOrderRequest(
            String customerName,
            String customerEmail,
            Map<Long, Integer> items
    ) {}
}
