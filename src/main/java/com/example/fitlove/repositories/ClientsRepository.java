package com.example.fitlove.repositories;

import com.example.fitlove.models.Clients;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientsRepository extends JpaRepository<Clients, Integer> {
    // Здесь можно добавить дополнительные методы, если необходимо

    Clients findByEmail(String email);
}