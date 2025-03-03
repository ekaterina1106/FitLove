package com.example.fitlove.controllers;

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
public class GroupClassesController {

    private final GroupClassesService groupClassesService;
    private final InstructorsService instructorsService;
    private final ClientsService clientsService;

    @Autowired
    public GroupClassesController(GroupClassesService groupClassesService, InstructorsService instructorsService, ClientsService clientsService) {
        this.groupClassesService = groupClassesService;
        this.instructorsService = instructorsService; // Инициализируем сервис
        this.clientsService = clientsService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    // Получение всех групповых занятий
    @GetMapping
    public String listClasses(Model model, Principal principal) {
        model.addAttribute("client", clientsService.getUserByPrincipal(principal));
        List<GroupClasses> classes = groupClassesService.getUpcomingClasses();
        model.addAttribute("classes", classes);
        return "class/class_list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    // Форма создания нового занятия
    @GetMapping("/new")
    public String createClassForm(Model model, Principal principal) {
        model.addAttribute("client", clientsService.getUserByPrincipal(principal));
        GroupClasses groupClass = new GroupClasses();
        groupClass.setInstructor(new Instructors());
        model.addAttribute("groupClass", groupClass);
        List<Instructors> instructors = instructorsService.getAllInstructors();
        model.addAttribute("instructors", instructors);
        model.addAttribute("availableTimes", groupClassesService.getTimes()); // Добавляем временные интервалы
        return "class/class_create";
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String saveClass(@ModelAttribute("groupClass") @Valid GroupClasses groupClass,
                            BindingResult bindingResult, Model model, Principal principal) {
        model.addAttribute("client", clientsService.getUserByPrincipal(principal));
        List<Instructors> instructors = instructorsService.getAllInstructors();
        model.addAttribute("instructors", instructors);

        // Проверка на наличие занятие с таким же временем
        if (groupClassesService.isTimeSlotOccupied(groupClass.getClassDate(), groupClass.getStartTime())) {
            bindingResult.rejectValue("startTime", "error.groupClass", "В это время занятие уже запланировано.");
        }

        if (bindingResult.hasErrors()) {
            return "class/class_create"; // Возвращаем на страницу создания с ошибками
        }

        groupClassesService.saveGroupClass(groupClass);
        return "redirect:/classes"; // Перенаправление после успешного сохранения
    }



    @PreAuthorize("hasRole('ADMIN')")
    // Форма редактирования существующего занятия
    @GetMapping("/{id}/edit")
    public String editClassForm(@PathVariable int id, Model model, Principal principal) {
        model.addAttribute("client", clientsService.getUserByPrincipal(principal));
        GroupClasses groupClass = groupClassesService.getGroupClassById(id)
                .orElseThrow(() -> new RuntimeException("Group class not found with id " + id));

        // Получаем список инструкторов и добавляем в модель
        List<Instructors> instructors = instructorsService.getAllInstructors();
        model.addAttribute("groupClass", groupClass);
        model.addAttribute("availableTimes", groupClassesService.getTimes()); // Добавляем временные интервалы
        model.addAttribute("instructors", instructors); // Передаем список инструкторов
        return "class/class_edit"; // Возвращаем шаблон формы редактирования
    }

    @PreAuthorize("hasRole('ADMIN')")
    // Обновление занятия
    @PostMapping("/{id}")
    public String updateClass(@PathVariable int id, @ModelAttribute("groupClass") GroupClasses groupClass,Model model, Principal principal) {
        model.addAttribute("client", clientsService.getUserByPrincipal(principal));
        groupClass.setId(id);
        groupClassesService.updateGroupClass(groupClass);
        return "redirect:/classes";
    }

    @PreAuthorize("hasRole('ADMIN')")
    // Удаление занятия
    @PostMapping("/{id}/delete")
    public String deleteClass(@PathVariable int id, Model model, Principal principal) {
        model.addAttribute("client", clientsService.getUserByPrincipal(principal));
        groupClassesService.deleteGroupClass(id);
        return "redirect:/classes";
    }


}
