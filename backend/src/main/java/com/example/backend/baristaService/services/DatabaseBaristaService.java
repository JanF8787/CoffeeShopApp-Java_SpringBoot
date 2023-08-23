package com.example.backend.baristaService.services;

import com.example.backend.baristaService.models.Barista;
import com.example.backend.ordersService.models.Order;
import com.example.backend.baristaService.repositories.BaristaRepository;
import com.example.backend.ordersService.repositories.OrderRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class DatabaseBaristaService implements BaristaService{

    private final BaristaRepository baristaRepository;
    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    public DatabaseBaristaService(BaristaRepository baristaRepository, OrderRepository orderRepository, RestTemplate restTemplate) {
        this.baristaRepository = baristaRepository;
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
    }


    @Override
    public Barista addOrder(Order order) {
        Barista barista = new Barista();

        barista.setOrderNumber(order.getOrderNumber());
        barista.setStatus("waiting");

        baristaRepository.save(barista);
        return barista;
    }

    @Override
    @Scheduled(fixedDelay = 10000)
    public void processOrders() {
        List<Barista> baristas = baristaRepository.findByStatus("waiting");

        for (Barista barista : baristas) {

            timer(10);

            if (!barista.getStatus().equals("canceled")) {
                barista.setStatus("in preparation");
                baristaRepository.save(barista);
                timer(10);

                barista.setStatus("finished");
                baristaRepository.save(barista);
                timer(10);

                barista.setStatus("picked up");
                baristaRepository.save(barista);

                restTemplate.postForObject("http://localhost:8080/orders/notify", orderRepository.findById(barista.getOrderNumber()), Void.class);
                baristaRepository.delete(barista);
            }
        }
    }

    private void timer(Integer seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    public Optional<Barista> findBaristaByOrderNumber(Long orderNumber) {
        return baristaRepository.findByOrderNumber(orderNumber);
    }

    @Override
    public Barista save(Barista barista) {
        return baristaRepository.save(barista);
    }

}
