package com.example.fitlove.controllers;

import com.example.fitlove.dto.ClientDTO;
import com.example.fitlove.models.Clients;
import com.example.fitlove.services.ClientsService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/clients")
@PreAuthorize("hasRole('ADMIN')")
@Slf4j  // Используем аннотацию для автоматического создания логгера
public class ClientsController {

    private final ClientsService clientsService;

    @Autowired
    public ClientsController(ClientsService clientsService) {
        this.clientsService = clientsService;
    }

    @GetMapping
    public String listClients(Model model, Principal principal) {
        log.info("Запрос списка клиентов");
        model.addAttribute("client", clientsService.getUserByPrincipal(principal));
        List<ClientDTO> clientsDTO = clientsService.getAllClientsDTO();
        model.addAttribute("clients", clientsDTO);
        return "client/client_list";
    }

    // Блокировка клиента
    @PostMapping("/{id}/block")
    public String blockClient(@PathVariable("id") int clientId) {
        log.info("Блокировка клиента с ID: {}", clientId);
        clientsService.blockClient(clientId);
        return "redirect:/clients";
    }

    // Разблокировка клиента
    @PostMapping("/{id}/unblock")
    public String unblockClient(@PathVariable("id") int clientId) {
        log.info("Разблокировка клиента с ID: {}", clientId);
        clientsService.unblockClient(clientId);
        return "redirect:/clients";
    }


//    @PostMapping("/{id}/delete")
//    public String deleteClient(@PathVariable int id) {
//        clientsService.deleteClient(id);
//        log.warn("Удален клиент с id={}", id);
//        return "redirect:/clients";
//    }
}









//    @GetMapping
//    public String listClients(Model model, Principal principal) {
//        logger.info("Запрос списка клиентов");
//        model.addAttribute("client", clientsService.getUserByPrincipal(principal));
//        List<Clients> clients = clientsService.getAllClients();
//        model.addAttribute("clients", clients);
//        return "client/client_list";
//    }

//    @GetMapping("/new")
//    public String createClientForm(Model model) {
//        logger.info("Запрос на создание нового клиента");
//        model.addAttribute("client", new Clients());
//        return "client/client_create";
//    }
//
//    @PostMapping
//    public String saveClient(@ModelAttribute Clients client) {
//        clientsService.saveClient(client);
//        logger.info("Добавлен новый клиент: {}", client.getName());
//        return "redirect:/clients";
//    }
//
//    @GetMapping("/{id}/edit")
//    public String editClientForm(@PathVariable int id, Model model) {
//        logger.info("Запрос на редактирование клиента с id={}", id);
//        Optional<Clients> clientOptional = clientsService.getClientById(id);
//        if (clientOptional.isPresent()) {
//            model.addAttribute("client", clientOptional.get());
//            return "client/client_edit";
//        } else {
//            logger.warn("Клиент с id={} не найден, перенаправление на список клиентов", id);
//            return "redirect:/clients";
//        }
//    }
//
//    @PostMapping("/{id}")
//    public String updateClient(@PathVariable int id, @ModelAttribute Clients client) {
//        client.setId(id);
//        clientsService.updateClient(client);
//        logger.info("Обновлен клиент с id={}", id);
//        return "redirect:/clients";
//    }



