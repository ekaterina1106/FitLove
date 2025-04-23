package com.example.fitlove.controllers;

import com.example.fitlove.dto.LoginRequestDTO;
import com.example.fitlove.models.Clients;
import com.example.fitlove.services.ClientsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import java.security.Principal;

@Controller
public class LoginPageController {

    private final ClientsService clientsService;

    @Autowired
    public LoginPageController(ClientsService clientsService) {
        this.clientsService = clientsService;
    }

    @GetMapping("/login")
    public String loginPage(Model model, Principal principal,
                            @RequestParam(name = "logout", required = false) String logout,
                            @RequestParam(name = "error", required = false) String error,
                            HttpSession session) {
        // Если клиент уже вошёл, редиректим на главную
        if (principal != null) {
            return "redirect:/main";
        }

        // Показываем сообщение о регистрации, если есть
        String successMessage = (String) session.getAttribute("successMessage");
        if (successMessage != null) {
            model.addAttribute("successMessage", successMessage);
            session.removeAttribute("successMessage");
        }

        // Показываем сообщение о выходе
        if (logout != null) {
            model.addAttribute("successMessage", "Вы успешно вышли");
        }

        // Показываем сообщения об ошибке, если они есть
        if ("bad_credentials".equals(error)) {
            model.addAttribute("errorMessage", "Неверный логин или пароль");
        } else if ("blocked".equals(error)) {
            model.addAttribute("errorMessage", "Ваш аккаунт заблокирован");
        }

        // DTO для формы логина
        model.addAttribute("loginDTO", new LoginRequestDTO());

        return "login";
    }


}



