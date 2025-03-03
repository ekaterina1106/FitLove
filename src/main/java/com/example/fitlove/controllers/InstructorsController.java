package com.example.fitlove.controllers;

import com.example.fitlove.models.Instructors;
import com.example.fitlove.services.ClientsService;
import com.example.fitlove.services.InstructorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/instructors")
public class InstructorsController {

    private final InstructorsService instructorsService;
    private final ClientsService clientsService;

    @Autowired
    public InstructorsController(InstructorsService instructorsService, ClientsService clientsService) {
        this.instructorsService = instructorsService;
        this.clientsService = clientsService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    // Получение всех инструкторов
    @GetMapping
    public String listInstructors(Model model, Principal principal) {
        model.addAttribute("client", clientsService.getUserByPrincipal(principal));
        List<Instructors> instructors = instructorsService.getAllInstructors();
        model.addAttribute("instructors", instructors);
        return "instructors/list"; // Возвращает представление со списком инструкторов
    }

    @PreAuthorize("hasRole('ADMIN')")
    // Форма создания нового инструктора
    @GetMapping("/new")
    public String createInstructorForm(Model model, Principal principal) {
        model.addAttribute("client", clientsService.getUserByPrincipal(principal));
        model.addAttribute("instructor", new Instructors());
        return "instructors/create"; // Возвращает шаблон формы создания
    }

    @PreAuthorize("hasRole('ADMIN')")
    // Сохранение нового инструктора
    @PostMapping
    public String saveInstructor(@ModelAttribute("instructor") Instructors instructor, Model model, Principal principal) {
        model.addAttribute("client", clientsService.getUserByPrincipal(principal));
        instructorsService.saveInstructor(instructor);
        return "redirect:/instructors";
    }

    @PreAuthorize("hasRole('ADMIN')")
    // Удаление инструктора
    @PostMapping("/{id}/delete")
    public String deleteInstructor(@PathVariable int id, Model model, Principal principal) {
        model.addAttribute("client", clientsService.getUserByPrincipal(principal));
        instructorsService.deleteInstructor(id);
        return "redirect:/instructors";
    }

    @PreAuthorize("hasRole('ADMIN')")
    // Форма редактирования существующего инструктора
    @GetMapping("/{id}/edit")
    public String editInstructorForm(@PathVariable int id, Model model, Principal principal) {
        model.addAttribute("client", clientsService.getUserByPrincipal(principal));
        Instructors instructor = instructorsService.getInstructorById(id)
                .orElseThrow(() -> new RuntimeException("Instructor not found with id " + id));
        model.addAttribute("instructor", instructor);
        return "instructors/edit"; // Возвращает шаблон формы редактирования
    }

    @PreAuthorize("hasRole('ADMIN')")
    // Обновление инструктора
    @PostMapping("/{id}")
    public String updateInstructor(@PathVariable int id, @ModelAttribute("instructor") Instructors instructor, Model model, Principal principal) {
        model.addAttribute("client", clientsService.getUserByPrincipal(principal));
        instructor.setId(id); // Установите ID для обновления
        instructorsService.updateInstructor(instructor);
        return "redirect:/instructors"; // Перенаправление обратно к списку инструкторов
    }



    // Другие методы для создания, редактирования и удаления инструкторов можно добавить здесь
}
