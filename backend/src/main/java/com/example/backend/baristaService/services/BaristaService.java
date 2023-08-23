package com.example.backend.baristaService.services;

import com.example.backend.baristaService.models.Barista;
import com.example.backend.ordersService.models.Order;

import java.util.Optional;

public interface BaristaService {

    Barista addOrder(Order order);

    void processOrders();
    Optional<Barista> findBaristaByOrderNumber(Long orderNumber);
    Barista save(Barista barista);
}
