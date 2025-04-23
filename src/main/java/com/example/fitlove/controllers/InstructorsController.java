

package com.example.fitlove.controllers;

import com.example.fitlove.dto.InstructorDTO;
import com.example.fitlove.models.Clients;
import com.example.fitlove.models.Instructors;
import com.example.fitlove.services.ClientsService;
import com.example.fitlove.services.InstructorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/instructors")
@PreAuthorize("hasRole('ADMIN')")
public class InstructorsController {

    private final InstructorsService instructorsService;
    private final ClientsService clientsService;

    @Autowired
    public InstructorsController(InstructorsService instructorsService,ClientsService clientsService) {
        this.instructorsService = instructorsService;
        this.clientsService = clientsService;
    }

    @ModelAttribute("client")
    public Clients getClient(Principal principal) {
        return clientsService.getUserByPrincipal(principal);
    }

    // Получение всех инструкторов
    @GetMapping
    public String listInstructors(Model model) {
        model.addAttribute("instructors", instructorsService.getAllInstructorDTOs());
        return "instructors/list"; // Возвращает представление со списком инструкторов
    }

    // Форма создания нового инструктора
    @GetMapping("/new")
    public String createInstructorForm(Model model) {
        model.addAttribute("instructor", new InstructorDTO());
        return "instructors/create"; // Возвращает шаблон формы создания
    }

    // Сохранение нового инструктора
    @PostMapping
    public String saveInstructor(@ModelAttribute("instructor") InstructorDTO instructorDTO) {
        Instructors instructor = new Instructors(instructorDTO.getName());
        instructorsService.saveInstructor(instructor);
        return "redirect:/instructors";
    }

    // Блокировка инструктора
    @PostMapping("/{id}/block")
    public String blockInstructor(@PathVariable int id) {
        instructorsService.blockInstructor(id);
        return "redirect:/instructors";
    }

    // Разблокировка инструктора
    @PostMapping("/{id}/unblock")
    public String unblockInstructor(@PathVariable int id) {
        instructorsService.unblockInstructor(id);
        return "redirect:/instructors";
    }

    // Удаление инструктора
    @PostMapping("/{id}/delete")
    public String deleteInstructor(@PathVariable int id) {
        instructorsService.deleteInstructor(id);
        return "redirect:/instructors";  // Перенаправление обратно к списку инструкторов
    }

    // Форма редактирования существующего инструктора
    @GetMapping("/{id}/edit")
    public String editInstructorForm(@PathVariable int id, Model model) {
        Instructors instructor = instructorsService.getInstructorById(id)
                .orElseThrow(() -> new RuntimeException("Instructor not found with id " + id));
        InstructorDTO instructorDTO = new InstructorDTO(instructor.getId(), instructor.getName());
        model.addAttribute("instructor", instructorDTO);
        return "instructors/edit"; // Возвращает шаблон формы редактирования
    }

    // Обновление инструктора
    @PostMapping("/{id}")
    public String updateInstructor(@PathVariable int id, @ModelAttribute("instructor") InstructorDTO instructorDTO) {
        Instructors instructor = new Instructors(id, instructorDTO.getName());
        instructorsService.updateInstructor(instructor);
        return "redirect:/instructors"; // Перенаправление обратно к списку инструкторов
    }
}






//@Controller
//@RequestMapping("/instructors")
//public class InstructorsController {
//
//    private final InstructorsService instructorsService;
//    private final ClientsService clientsService;
//
//    @Autowired
//    public InstructorsController(InstructorsService instructorsService, ClientsService clientsService) {
//        this.instructorsService = instructorsService;
//        this.clientsService = clientsService;
//    }
//
//    @PreAuthorize("hasRole('ADMIN')")
//    // Получение всех инструкторов
//    @GetMapping
//    public String listInstructors(Model model, Principal principal) {
//        model.addAttribute("client", clientsService.getUserByPrincipal(principal));
//        List<Instructors> instructors = instructorsService.getAllInstructors();
//        model.addAttribute("instructors", instructors);
//        return "instructors/list"; // Возвращает представление со списком инструкторов
//    }
//
//    @PreAuthorize("hasRole('ADMIN')")
//    // Форма создания нового инструктора
//    @GetMapping("/new")
//    public String createInstructorForm(Model model, Principal principal) {
//        model.addAttribute("client", clientsService.getUserByPrincipal(principal));
//        model.addAttribute("instructor", new Instructors());
//        return "instructors/create"; // Возвращает шаблон формы создания
//    }
//
//    @PreAuthorize("hasRole('ADMIN')")
//    // Сохранение нового инструктора
//    @PostMapping
//    public String saveInstructor(@ModelAttribute("instructor") Instructors instructor, Model model, Principal principal) {
//        model.addAttribute("client", clientsService.getUserByPrincipal(principal));
//        instructorsService.saveInstructor(instructor);
//        return "redirect:/instructors";
//    }
//
//    @PreAuthorize("hasRole('ADMIN')")
//    // Удаление инструктора
//    @PostMapping("/{id}/delete")
//    public String deleteInstructor(@PathVariable int id, Model model, Principal principal) {
//        model.addAttribute("client", clientsService.getUserByPrincipal(principal));
//        instructorsService.deleteInstructor(id);
//        return "redirect:/instructors";
//    }
//
//    @PreAuthorize("hasRole('ADMIN')")
//    // Форма редактирования существующего инструктора
//    @GetMapping("/{id}/edit")
//    public String editInstructorForm(@PathVariable int id, Model model, Principal principal) {
//        model.addAttribute("client", clientsService.getUserByPrincipal(principal));
//        Instructors instructor = instructorsService.getInstructorById(id)
//                .orElseThrow(() -> new RuntimeException("Instructor not found with id " + id));
//        model.addAttribute("instructor", instructor);
//        return "instructors/edit"; // Возвращает шаблон формы редактирования
//    }
//
//    @PreAuthorize("hasRole('ADMIN')")
//    // Обновление инструктора
//    @PostMapping("/{id}")
//    public String updateInstructor(@PathVariable int id, @ModelAttribute("instructor") Instructors instructor, Model model, Principal principal) {
//        model.addAttribute("client", clientsService.getUserByPrincipal(principal));
//        instructor.setId(id); // Установите ID для обновления
//        instructorsService.updateInstructor(instructor);
//        return "redirect:/instructors"; // Перенаправление обратно к списку инструкторов
//    }
//
//
//
//    // Другие методы для создания, редактирования и удаления инструкторов можно добавить здесь
//}
