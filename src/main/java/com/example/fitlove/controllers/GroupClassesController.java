package com.example.fitlove.controllers;

import com.example.fitlove.dto.GroupClassDTO;
import com.example.fitlove.models.Clients;
import com.example.fitlove.models.GroupClasses;
import com.example.fitlove.models.Instructors; // Импортируем модель инструкторов
import com.example.fitlove.services.ClientsService;
import com.example.fitlove.services.GroupClassesService;
import com.example.fitlove.services.InstructorsService; // Импортируем сервис инструкторов
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

//import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/classes")
@PreAuthorize("hasRole('ADMIN')")
public class GroupClassesController {

    private final GroupClassesService groupClassesService;
    private final InstructorsService instructorsService;
    private final ClientsService clientsService;

    @Autowired
    public GroupClassesController(GroupClassesService groupClassesService, InstructorsService instructorsService, ClientsService clientsService) {
        this.groupClassesService = groupClassesService;
        this.instructorsService = instructorsService;
        this.clientsService = clientsService;
    }

    @ModelAttribute("client")
    public Clients getClient(Principal principal) {
        return clientsService.getUserByPrincipal(principal);
    }

    // Получение всех групповых занятий
    @GetMapping
    public String listClasses(Model model) {
        List<GroupClasses> classes = groupClassesService.getUpcomingClasses();
        model.addAttribute("classes", classes);
        return "class/class_list";
    }

    // Форма создания нового занятия
    @GetMapping("/new")
    public String createClassForm(Model model) {
        GroupClasses groupClass = new GroupClasses();
        groupClass.setInstructor(new Instructors());
        model.addAttribute("groupClass", groupClass);
        model.addAttribute("instructors", instructorsService.getAllInstructorDTOs());
        model.addAttribute("availableTimes", groupClassesService.getTimes());
        return "class/class_create";
    }


    // Сохранение занятия
    @PostMapping
    public String saveClass(@ModelAttribute("groupClass") @Valid GroupClasses groupClass,
                            BindingResult bindingResult, Model model) {

        model.addAttribute("instructors", instructorsService.getAllInstructorDTOs());
        model.addAttribute("availableTimes", groupClassesService.getTimes());

        if (groupClassesService.isTimeSlotOccupied(groupClass.getClassDate(), groupClass.getStartTime())) {
            bindingResult.rejectValue("startTime", "error.groupClass", "В это время занятие уже запланировано.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("groupClass", groupClass);
            return "class/class_create";
        }

        groupClassesService.saveGroupClass(groupClass);
        return "redirect:/classes";
    }

    // Форма редактирования существующего занятия
    @GetMapping("/{id}/edit")
    public String editClassForm(@PathVariable int id, Model model) {
        GroupClasses groupClass = groupClassesService.getGroupClassById(id)
                .orElseThrow(() -> new RuntimeException("Group class not found with id " + id));


        model.addAttribute("groupClass", groupClass);
        model.addAttribute("availableTimes", groupClassesService.getTimes());
        model.addAttribute("instructors", instructorsService.getAllInstructorDTOs());
        return "class/class_edit";
    }

    // Обновление занятия
    @PostMapping("/{id}")
    public String updateClass(@PathVariable int id, @ModelAttribute("groupClass") @Valid GroupClasses groupClass,
                              BindingResult bindingResult, Model model) {
        model.addAttribute("instructors", instructorsService.getAllInstructorDTOs());
        model.addAttribute("availableTimes", groupClassesService.getTimes());

        if (groupClassesService.isTimeSlotOccupied(groupClass.getClassDate(), groupClass.getStartTime())) {
            bindingResult.rejectValue("startTime", "error.groupClass", "В это время занятие уже запланировано.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("groupClass", groupClass);
            return "class/class_edit";
        }

        groupClass.setId(id);
        groupClassesService.updateGroupClass(groupClass);
        return "redirect:/classes";
    }

    // Удаление занятия
    @PostMapping("/{id}/delete")
    public String deleteClass(@PathVariable int id) {
        groupClassesService.deleteGroupClass(id);
        return "redirect:/classes";
    }
}

