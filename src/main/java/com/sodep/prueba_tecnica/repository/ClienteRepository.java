package com.sodep.prueba_tecnica.repository;

import com.sodep.prueba_tecnica.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByEmail(String email);

    Optional<Cliente> findByIdExterno(String idExterno);

    boolean existsByEmail(String email);
}
