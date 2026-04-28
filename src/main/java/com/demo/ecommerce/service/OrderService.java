package com.demo.ecommerce.service;

import com.demo.ecommerce.model.Order;
import com.demo.ecommerce.model.OrderItem;
import com.demo.ecommerce.model.Product;
import com.demo.ecommerce.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;

    public OrderService(OrderRepository orderRepository, ProductService productService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + id));
    }

    public List<Order> getOrdersByEmail(String email) {
        return orderRepository.findByCustomerEmail(email);
    }

    public List<Order> getOrdersByStatus(Order.Status status) {
        return orderRepository.findByStatus(status);
    }

    /**
     * Creates an order and decrements stock.
     * items map: productId -> quantity
     */
    public Order createOrder(String customerName, String customerEmail, Map<Long, Integer> items) {
        Order order = new Order(customerName, customerEmail);

        for (Map.Entry<Long, Integer> entry : items.entrySet()) {
            Product product = productService.getProductById(entry.getKey());
            int qty = entry.getValue();

            if (product.getStockQuantity() < qty) {
                throw new IllegalStateException("Insufficient stock for: " + product.getName());
            }

            product.setStockQuantity(product.getStockQuantity() - qty);
            OrderItem item = new OrderItem(order, product, qty);
            order.getItems().add(item);
        }

        return orderRepository.save(order);
    }

    public Order updateStatus(Long id, Order.Status status) {
        Order order = getOrderById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public void cancelOrder(Long id) {
        Order order = getOrderById(id);
        if (order.getStatus() == Order.Status.SHIPPED || order.getStatus() == Order.Status.DELIVERED) {
            throw new IllegalStateException("Cannot cancel an order that is already " + order.getStatus());
        }
        // restore stock
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
        }
        order.setStatus(Order.Status.CANCELLED);
        orderRepository.save(order);
    }
}
