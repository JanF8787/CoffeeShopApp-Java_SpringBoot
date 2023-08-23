package com.example.backend.baristaService.controllers;

import com.example.backend.ordersService.models.Order;
import com.example.backend.baristaService.services.BaristaService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/barista")
public class BaristaController {

    private final BaristaService baristaService;

    public BaristaController(BaristaService baristaService) {
        this.baristaService = baristaService;
    }

    @PostMapping("/add_order")
    public ResponseEntity<Void> addOrder(@RequestBody Order order) {
        baristaService.addOrder(order);
        return ResponseEntity.ok().build();
    }
}
