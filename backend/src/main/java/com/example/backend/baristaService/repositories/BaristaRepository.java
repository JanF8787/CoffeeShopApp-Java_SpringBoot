package com.example.backend.baristaService.repositories;

import com.example.backend.baristaService.models.Barista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BaristaRepository extends JpaRepository<Barista, Long> {

    List<Barista> findByStatus(String status);

    Optional<Barista> findByOrderNumber(Long orderNumber);
}
