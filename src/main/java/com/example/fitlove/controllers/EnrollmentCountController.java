package com.example.fitlove.controllers;

import com.example.fitlove.models.GroupClasses;
import com.example.fitlove.services.ClientsService;
import com.example.fitlove.services.EnrollmentsService;
import com.example.fitlove.services.GroupClassesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/enrollment_count")
public class EnrollmentCountController {

    private final EnrollmentsService enrollmentsService;
    private final ClientsService clientsService;
    private final GroupClassesService groupClassesService;

    @Autowired
    public EnrollmentCountController(EnrollmentsService enrollmentsService, ClientsService clientsService, GroupClassesService groupClassesService) {
        this.enrollmentsService = enrollmentsService;
        this.clientsService = clientsService;
        this.groupClassesService = groupClassesService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public String showUpcomingAndPastClassesWithEnrollmentCount(Model model, Principal principal) {
        model.addAttribute("client", clientsService.getUserByPrincipal(principal));

        // Получаем предстоящие занятия
        List<GroupClasses> upcomingClasses = enrollmentsService.getUpcomingClassesWithEnrollmentCount();
        model.addAttribute("upcomingClasses", upcomingClasses);

        // Получаем прошедшие занятия
        List<GroupClasses> pastClasses = enrollmentsService.getPastClassesWithEnrollmentCount();
        model.addAttribute("pastClasses", pastClasses);

        // Получаем даты занятий для дней недели
        List<LocalDate> weekDates = groupClassesService.getWeekDates();
        model.addAttribute("weekDates", weekDates);
        model.addAttribute("dayOfWeek", groupClassesService.getWeekDays(weekDates));

        // Получаем даты и дни недели для прошедших занятий
        List<LocalDate> pastDates = pastClasses.stream()
                .map(GroupClasses::getClassDate)
                .distinct() // Убираем дубликаты
                .collect(Collectors.toList());
        model.addAttribute("pastDates", pastDates);
        model.addAttribute("pastDaysOfWeek", groupClassesService.getWeekDays(pastDates));


        return "enrollment_count";
    }

}


//
//@Controller
//@RequestMapping("/enrollment_count")
//public class EnrollmentCountController {
//
//    private final EnrollmentsService enrollmentsService;
//    private final ClientsService clientsService;
//
//    @Autowired
//    public EnrollmentCountController(EnrollmentsService enrollmentsService, ClientsService clientsService) {
//        this.enrollmentsService = enrollmentsService;
//        this.clientsService = clientsService;
//    }
//
//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping("/enrollmentCount")
//    public String showUpcomingClassesWithEnrollmentCount(Model model, Principal principal) {
//        model.addAttribute("client", clientsService.getUserByPrincipal(principal));
//        List<GroupClasses> upcomingClasses = enrollmentsService.getUpcomingClassesWithEnrollmentCount();
//        model.addAttribute("upcomingClasses", upcomingClasses);
//        return "/enrollment_count"; // Новый путь к шаблону
//    }
//}