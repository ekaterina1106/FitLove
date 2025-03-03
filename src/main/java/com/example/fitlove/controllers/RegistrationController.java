package com.example.fitlove.controllers;

import com.example.fitlove.models.Clients;
import com.example.fitlove.repositories.ClientsRepository;
import com.example.fitlove.services.ClientsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Controller
public class RegistrationController {
    private final ClientsService clientsService;

   private final ClientsRepository clientsRepository;

    public RegistrationController(ClientsService clientsService, ClientsRepository clientsRepository) {
        this.clientsService = clientsService;
        this.clientsRepository = clientsRepository;
    }

    @GetMapping("/registration")
    public String registration(Model model, Principal principal){
        model.addAttribute("title", "Страница регистрации");

        // Создаем новый объект Clients, если пользователь не авторизован
        Clients client = (principal != null) ? clientsService.getUserByPrincipal(principal) : new Clients();
        model.addAttribute("client", client);


        System.out.println("get method");
        return "registration";

    }


    @PostMapping("/registration")
    public String createUser(@Valid Clients client, BindingResult bindingResult, Model model, HttpSession session) {
        model.addAttribute("client", client);

        // Проверка длины пароля
        if (client.getPassword().length() < 4) {
            bindingResult.rejectValue("password", "error.client", "Пароль должен быть не менее 4 символов.");
        }

        if (bindingResult.hasErrors()) {
            Map<String, String> errorsMap = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            fieldError -> fieldError.getField() + "Error",
                            FieldError::getDefaultMessage
                    ));
            model.mergeAttributes(errorsMap);
            return "registration";
        } else {
            String message = clientsService.createUser(client);
            if (message.equals("true")) {
                session.setAttribute("successMessage", "Успешная регистрация");
                return "redirect:/login";
            } else {
                model.addAttribute("errorMessage", "Аккаунт с такой почтой уже существует.");
                return "registration";
            }
        }
    }




}
