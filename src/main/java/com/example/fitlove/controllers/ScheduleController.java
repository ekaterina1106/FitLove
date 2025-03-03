package com.example.fitlove.controllers;

import com.example.fitlove.models.Clients;
import com.example.fitlove.models.GroupClasses;

import com.example.fitlove.models.enums.Role;
import com.example.fitlove.services.ClientsService;
import com.example.fitlove.services.EnrollmentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.fitlove.services.GroupClassesService;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RequestMapping("/schedule")
@Controller
public class ScheduleController {

    @Value("${class.max-capacity}")
    private int maxCapacity;

    private final GroupClassesService groupClassesService;
    private final EnrollmentsService enrollmentsService;
    private final ClientsService clientsService;

    @Autowired
    public ScheduleController(GroupClassesService groupClassesService, EnrollmentsService enrollmentsService, ClientsService clientsService) {
        this.groupClassesService = groupClassesService;
        this.enrollmentsService = enrollmentsService;
        this.clientsService = clientsService;
    }



    @GetMapping()
    public String showSchedule(Model model, Principal principal) {
        Clients client = clientsService.getUserByPrincipal(principal);
        model.addAttribute("client", client);

        model.addAttribute("maxCapacity", maxCapacity);

        List<LocalDate> weekDates = groupClassesService.getWeekDates();
        model.addAttribute("weekDates", weekDates);
        model.addAttribute("dayOfWeek", groupClassesService.getWeekDays(weekDates));
        model.addAttribute("times", groupClassesService.getTimes());
//        model.addAttribute("classes", groupClassesService.getAllGroupClasses());
        List<GroupClasses> classes = groupClassesService.getAllGroupClasses();
        classes.forEach(groupClass -> {
            int enrollmentCount = enrollmentsService.getEnrollmentCountForClass(groupClass.getId());
            groupClass.setEnrollmentCount(enrollmentCount); // Поле enrollmentCount должно быть в GroupClasses
        });
        model.addAttribute("classes", classes);



        // Инициализируем clientEnrollments пустым списком, если клиент не авторизован или у него нет записей
        List<Integer> clientEnrollments = client != null ? enrollmentsService.getClientEnrollments(client.getId()) : Collections.emptyList();
        model.addAttribute("clientEnrollments", clientEnrollments);


        return "schedule/schedule";
    }




    @PostMapping("/enroll")
    public String enrollClient(@RequestParam(value = "classId") String classId, Model model, Principal principal) {
        // Получаем клиента по Principal
        Clients client = clientsService.getUserByPrincipal(principal);

        if (client == null) {
            // Если клиент не найден, перенаправляем на страницу логина
            return "redirect:/login";
        }

        if (classId == null || classId.isEmpty()) {
            // Обработка ошибки, если ID занятия не был предоставлен
            return "redirect:/schedule?error=classIdNotProvided";
        }

        try {
            // Записываем клиента на занятие
            enrollmentsService.enrollClient(client.getId(), Integer.parseInt(classId));
        } catch (IllegalStateException e) {
            // Обработка ошибки, если клиент уже записан на это занятие
            return "redirect:/schedule?error=alreadyEnrolled";
        } catch (Exception e) {
            // Общая обработка ошибок
            return "redirect:/schedule?error=unknownError";
        }

        return "redirect:/schedule?success=enrolled";
    }

    @PostMapping("/cancel")
    public String cancelEnrollment(@RequestParam int classId, Principal principal) {
        Clients client = clientsService.getUserByPrincipal(principal);
        enrollmentsService.cancelEnrollment(classId, client.getId());
        return "redirect:/schedule";
    }


    @PreAuthorize("hasRole('ADMIN')")
    // Удаление занятия
    @PostMapping("/{id}/delete")
    public String deleteClass(@PathVariable int id, Model model, Principal principal) {
        model.addAttribute("client", clientsService.getUserByPrincipal(principal));
        groupClassesService.deleteGroupClass(id);
        return "redirect:/schedule";
    }

}