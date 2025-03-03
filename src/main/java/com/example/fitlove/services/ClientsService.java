package com.example.fitlove.services;

import com.example.fitlove.models.Clients;
import com.example.fitlove.models.enums.Role;
import com.example.fitlove.repositories.ClientsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ClientsService {

    private final ClientsRepository clientsRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ClientsService(ClientsRepository clientsRepository, PasswordEncoder passwordEncoder) {
        this.clientsRepository = clientsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = false)
    public String createUser(Clients clients) {
        String message = "true";
        System.out.println("Attempting to save user");
        if (clientsRepository.findByEmail(clients.getEmail()) != null) {
            System.out.println("User with this email already exists");
            message = "false mail";
        } else {
            clients.setPassword(passwordEncoder.encode(clients.getPassword()));
            clients.getRole().add(Role.ROLE_USER);
            clientsRepository.save(clients);
        }
        return message;
    }


    public Clients getUserByPrincipal(Principal principal) {
        if (principal == null) return null;
        return clientsRepository.findByEmail(principal.getName());
    }


    // Получение всех клиентов
    public List<Clients> getAllClients() {
        return clientsRepository.findAll();
    }

    // Получение клиента по ID
    public Optional<Clients> getClientById(int id) {
        return clientsRepository.findById(id);
    }

    // Сохранение нового клиента
    @Transactional
    public Clients saveClient(Clients client) {
        return clientsRepository.save(client);
    }

    // Обновление существующего клиента
    @Transactional
    public Clients updateClient(Clients client) {
        return clientsRepository.findById(client.getId())
                .map(existingClient -> {
                    existingClient.setName(client.getName());
                    existingClient.setEmail(client.getEmail());
                    existingClient.setPassword(passwordEncoder.encode(client.getPassword())); // Можно зашифровать только, если это обновление пароля
                    existingClient.setPhone(client.getPhone());
                    // Роль не изменяется
                    return clientsRepository.save(existingClient);
                })
                .orElseThrow(() -> new RuntimeException("Client not found with id " + client.getId()));
    }

    // Удаление клиента по ID
    @Transactional
    public void deleteClient(int clientId) {
        clientsRepository.deleteById(clientId);
    }
}
