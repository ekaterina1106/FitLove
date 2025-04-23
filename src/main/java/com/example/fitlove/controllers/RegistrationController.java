package com.example.fitlove.controllers;

import com.example.fitlove.dto.ClientRegistrationDTO;
import com.example.fitlove.models.Clients;
import com.example.fitlove.services.ClientsService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Map;
import java.util.stream.Collectors;





@Controller
@RequestMapping("/registration")
@Slf4j
public class RegistrationController {

    private final ClientsService clientsService;

    @Autowired
    public RegistrationController(ClientsService clientsService) {
        this.clientsService = clientsService;
    }

    @GetMapping
    public String registration(Model model,
                               @ModelAttribute("errorMessage") String errorMessage,
                               @ModelAttribute("successMessage") String successMessage,
                               @ModelAttribute("clientDTO") ClientRegistrationDTO clientDTO) {
        log.info("Запрос страницы регистрации");

        if (!model.containsAttribute("clientDTO")) {
            model.addAttribute("clientDTO", new ClientRegistrationDTO());
        }
        if (errorMessage != null && !errorMessage.isEmpty()) {
            model.addAttribute("errorMessage", errorMessage);
        }
        if (successMessage != null && !successMessage.isEmpty()) {
            model.addAttribute("successMessage", successMessage);
        }

        return "registration";
    }

    @PostMapping
    public String createUser(@Valid @ModelAttribute("clientDTO") ClientRegistrationDTO clientDTO,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            log.warn("Ошибка валидации данных регистрации");

            // Логирование всех ошибок
            bindingResult.getFieldErrors().forEach(fieldError ->
                    log.warn("Поле: {}, Ошибка: {}", fieldError.getField(), fieldError.getDefaultMessage())
            );

            // Добавляем ошибки вручную в модель
            model.addAttribute("clientDTO", clientDTO);
            return "registration";
        }

        if (clientsService.existsByEmail(clientDTO.getEmail())) {
            log.error("Ошибка регистрации: Аккаунт с таким email уже существует");

            model.addAttribute("clientDTO", clientDTO);
            model.addAttribute("errorMessage", "Аккаунт с таким email уже существует");
            return "registration";
        }

        clientsService.createUser(clientDTO);
        model.addAttribute("successMessage", "Регистрация прошла успешно!");
        return "registration";
    }

    // после регистрации переход на вход с успешным сообщением, сделать что телефон или емайл уже заблокированы/заняты

}



