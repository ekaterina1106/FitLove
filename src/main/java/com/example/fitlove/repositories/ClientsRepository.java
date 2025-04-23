package com.example.fitlove.repositories;

import com.example.fitlove.models.Clients;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientsRepository extends JpaRepository<Clients, Integer> {

    Clients findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<Clients> findByPhone(String phone);

    // Проверка, заблокирован ли клиент с данным email
    boolean existsByEmailAndIsBlockedTrue(String email);

    // Проверка, заблокирован ли клиент с данным телефоном
    boolean existsByPhoneAndIsBlockedTrue(String phone);

    List<Clients> findAllByIsBlockedFalse(); // Найти всех незаблокированных клиентов
}
