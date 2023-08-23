package com.example.backend.ordersService.controllers;

import com.example.backend.baristaService.models.Barista;
import com.example.backend.baristaService.services.BaristaService;
import com.example.backend.ordersService.models.Order;
import com.example.backend.ordersService.models.OrderDto;
import com.example.backend.ordersService.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final BaristaService baristaService;

    private final RestTemplate restTemplate;

    public OrderController(OrderService orderService, BaristaService baristaService, RestTemplate restTemplate) {
        this.orderService = orderService;
        this.baristaService = baristaService;
        this.restTemplate = restTemplate;
    }

    @PostMapping("/add_order")
    public ResponseEntity<?> addOrder(@RequestBody OrderDto orderDto) {
        return new ResponseEntity<>(orderService.addOrder(orderDto), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllOrders() {
        List<OrderDto> orders = orderService.allOrders();

        if (orders.size() == 0) {
            return new ResponseEntity<>("All orders are done.", HttpStatus.OK);
        }

        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        Optional<Order> order = orderService.findById(id);
        Optional<Barista> barista = baristaService.findBaristaByOrderNumber(order.get().getOrderNumber());

        if (!barista.get().getStatus().equals("waiting")) {
            return ResponseEntity.badRequest().body("Your order with number " + order.get().getOrderNumber() + " is in preparation, you can't cancel it!");
        }

        orderService.cancelOrder(id);

        barista.get().setStatus("canceled");
        baristaService.save(barista.get());

        return new ResponseEntity<>("Your order number " + order.get().getOrderNumber() +" was canceled.", HttpStatus.OK);
    }

    @PostMapping("/notify")
    public ResponseEntity<Void> notification(@RequestBody Order order) {
        orderService.deleteOrder(order);
        return ResponseEntity.ok().build();
    }
}
