package com.example.fitlove.services;

import com.example.fitlove.dto.ClientDTO;
import com.example.fitlove.dto.ClientRegistrationDTO;
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
import java.util.Set;
import java.util.stream.Collectors;

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

    public boolean existsByEmail(String email) {
        return clientsRepository.existsByEmail(email);
    }


    @Transactional
    public void createUser(ClientRegistrationDTO clientDTO) {
        if (clientsRepository.existsByEmail(clientDTO.getEmail())) {
            throw new IllegalArgumentException("Аккаунт с таким email уже существует");
        }

        Clients client = new Clients(clientDTO.getName(), clientDTO.getEmail(), clientDTO.getPhone());
        client.setPassword(passwordEncoder.encode(clientDTO.getPassword()));
        client.setRole(Set.of(Role.ROLE_USER));

        clientsRepository.save(client); // Если ошибки не возникает, сохраняем клиента
    }


    public Clients getUserByPrincipal(Principal principal) {
        if (principal == null) return null;
        return clientsRepository.findByEmail(principal.getName());
    }


    public List<ClientDTO> getAllClientsDTO() {
        List<Clients> clients = clientsRepository.findAll();
        return clients.stream()
                .map(this::convertToDto)  // Преобразуем модель в DTO
                .collect(Collectors.toList());
    }


    // Удаление клиента по ID
//    @Transactional
//    public void deleteClient(int clientId) {
//        clientsRepository.deleteById(clientId);
//    }

    @Transactional
    public void blockClient(int clientId) {
        Clients client = clientsRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));
        client.setBlocked(true);
        clientsRepository.save(client);
    }

    @Transactional
    public void unblockClient(int clientId) {
        Clients client = clientsRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));
        client.setBlocked(false);
        clientsRepository.save(client);
    }

    // Проверка на заблокированный email
    public boolean isBlockedByEmail(String email) {
        return clientsRepository.existsByEmailAndIsBlockedTrue(email); // Используем новый метод для проверки блокировки по email
    }

    // Проверка на заблокированный телефон
    public boolean isBlockedByPhone(String phone) {
        return clientsRepository.existsByPhoneAndIsBlockedTrue(phone); // Используем новый метод для проверки блокировки по телефону
    }


//    public boolean isClientBlocked(String email) {
//        Clients client = clientsRepository.findByEmail(email);
//        return client != null && client.isBlocked();
//    }


    public ClientDTO convertToDto(Clients client) {
        return new ClientDTO(client.getId(), client.getName(), client.getEmail(), client.getPhone(), client.isBlocked());
    }

    // Конвертация из сущности Clients в DTO
    public ClientRegistrationDTO convertToDTO(Clients client) {
        return new ClientRegistrationDTO(client.getName(), client.getEmail(), "", client.getPhone());
    }


// Получение всех клиентов
//    public List<Clients> getAllClients() {
//        return clientsRepository.findAll();
//    }
//    // Получение клиента по ID
//    public Optional<Clients> getClientById(int id) {
//        return clientsRepository.findById(id);
//    }
//
//    // Сохранение нового клиента
//    @Transactional
//    public Clients saveClient(Clients client) {
//        return clientsRepository.save(client);
//    }
//
//    // Обновление существующего клиента
//    @Transactional
//    public Clients updateClient(Clients client) {
//        return clientsRepository.findById(client.getId())
//                .map(existingClient -> {
//                    existingClient.setName(client.getName());
//                    existingClient.setEmail(client.getEmail());
//                    existingClient.setPassword(passwordEncoder.encode(client.getPassword())); // Можно зашифровать только, если это обновление пароля
//                    existingClient.setPhone(client.getPhone());
//                    // Роль не изменяется
//                    return clientsRepository.save(existingClient);
//                })
//                .orElseThrow(() -> new RuntimeException("Client not found with id " + client.getId()));
//    }


}
