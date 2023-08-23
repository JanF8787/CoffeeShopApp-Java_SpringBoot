package com.example.backend.ordersService.services;

import com.example.backend.baristaService.models.Barista;
import com.example.backend.baristaService.repositories.BaristaRepository;
import com.example.backend.ordersService.models.Order;
import com.example.backend.ordersService.models.OrderDto;
import com.example.backend.ordersService.repositories.OrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DatabaseOrderService implements OrderService{

    private final OrderRepository orderRepository;
    private final BaristaRepository baristaRepository;
    private final RestTemplate restTemplate;

    public DatabaseOrderService(OrderRepository orderRepository, BaristaRepository baristaRepository, RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.baristaRepository = baristaRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public Order addOrder(OrderDto orderDto) {
        Order order = new Order();

        order.setCoffeeType(orderDto.getCoffeeType());
        order.setCoffeeSize(orderDto.getCoffeeSize());
        order.setMilkType(orderDto.getMilkType());
        order.setOrderType(orderDto.getOrderType());
        order.setPrice(orderDto.getPrice());

        orderRepository.save(order);

        order.setOrderNumber(order.getId());
        orderRepository.save(order);

        forwardOrderToBarista(order);

        return order;
    }

    @Override
    public List<OrderDto> allOrders() {
        List<Order> orders = orderRepository.findAll();

        List<OrderDto> orderDtoList = new ArrayList<>();

        for (Order order : orders) {
            OrderDto orderDto = new OrderDto();

            orderDto.setOrderNumber(order.getOrderNumber());
            orderDto.setCoffeeType(order.getCoffeeType());
            orderDto.setCoffeeSize(order.getCoffeeSize());
            orderDto.setMilkType(order.getMilkType());
            orderDto.setOrderType(order.getOrderType());
            orderDto.setPrice(order.getPrice());

            Optional<Barista> barista = baristaRepository.findByOrderNumber(order.getOrderNumber());
            orderDto.setStatus(barista.get().getStatus());

            orderDtoList.add(orderDto);
        }

        return orderDtoList;
    }

    @Override
    public void cancelOrder(Long id) {
        orderRepository.deleteById(id);
    }

    @Override
    public void deleteOrder(Order order) {
        orderRepository.delete(order);
    }

    @Override
    public void forwardOrderToBarista(Order order) {
        String baristaServiceUrl = "http://localhost:8080/barista/add_order";
        ResponseEntity<Void> response = restTemplate.postForEntity(baristaServiceUrl, order, Void.class);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }
}
