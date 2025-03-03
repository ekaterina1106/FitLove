package com.example.fitlove.controllers;

import com.example.fitlove.models.Clients;
import com.example.fitlove.models.GroupClasses;
import com.example.fitlove.services.ClientsService;
import com.example.fitlove.services.EnrollmentsService;
import com.example.fitlove.services.GroupClassesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


@Controller
public class PersonalAccountController {

    private final EnrollmentsService enrollmentsService; // Сервис для работы с записями
    private final ClientsService clientsService; // Сервис для работы с клиентами
    private final GroupClassesService groupClassesService; // Сервис для работы с групповыми занятиями

    @Autowired
    public PersonalAccountController(EnrollmentsService enrollmentsService,
                                     ClientsService clientsService,
                                     GroupClassesService groupClassesService) {
        this.enrollmentsService = enrollmentsService;
        this.clientsService = clientsService;
        this.groupClassesService = groupClassesService; // Инициализация
    }

    @GetMapping("/personalAccount")
    public String showPersonalAccount(Model model, Principal principal) {
        Clients client = clientsService.getUserByPrincipal(principal);
        model.addAttribute("client", client);

        // Получаем предстоящие записи клиента
        List<GroupClasses> upcomingEnrollments = enrollmentsService.getUpcomingEnrollmentsByClientId(client.getId());
        upcomingEnrollments.sort(Comparator.comparing(GroupClasses::getClassDate)); // Сортировка по дате
        model.addAttribute("upcomingEnrollments", upcomingEnrollments);

        // Получаем прошедшие записи клиента
        List<GroupClasses> pastEnrollments = enrollmentsService.getPastEnrollmentsByClientId(client.getId());
        model.addAttribute("pastEnrollments", pastEnrollments);

        // Получаем даты занятий для дней недели
        List<LocalDate> weekDates = groupClassesService.getWeekDates();
        model.addAttribute("weekDates", weekDates);
        model.addAttribute("dayOfWeek", groupClassesService.getWeekDays(weekDates));

        // Получаем даты и дни недели для прошедших занятий
        List<LocalDate> pastDates = pastEnrollments.stream()
                .map(GroupClasses::getClassDate)
                .distinct() // Убираем дубликаты
                .collect(Collectors.toList());
        model.addAttribute("pastDates", pastDates);
        model.addAttribute("pastDaysOfWeek", groupClassesService.getWeekDays(pastDates));

        return "/personalAccount"; // Название вашей страницы
    }


    @PostMapping("/personalAccount/cancel")
    public String cancelEnrollment(@RequestParam int classId, Principal principal) {
        Clients client = clientsService.getUserByPrincipal(principal);
        enrollmentsService.cancelEnrollment(classId, client.getId());
        return "redirect:/personalAccount";
    }


    public List<LocalDate> getPersonalAccountWeekDates() {
        // Возвращаем список дат на неделю начиная с сегодняшнего дня
        LocalDate startDate = LocalDate.now().minusDays(7); // Начинаем с прошлой недели
        return List.of(
                startDate,
                startDate.plusDays(1),
                startDate.plusDays(2),
                startDate.plusDays(3),
                startDate.plusDays(4),
                startDate.plusDays(5),
                startDate.plusDays(6)
        );
    }

    public List<String> getPersonalAccountWeekDays(List<LocalDate> weekDates) {
        Locale russianLocale = new Locale("ru"); // Устанавливаем локаль на русский
        return weekDates.stream()
                .map(date -> date.getDayOfWeek().getDisplayName(TextStyle.FULL, russianLocale).toLowerCase()) // Получаем название дня недели на русском в нижнем регистре
                .collect(Collectors.toList());
    }
}








//@Controller
//@RequestMapping("/personalAccount")
//public class PersonalAccountController {
//    private final ClientsService clientsService;
//
//    @Autowired
//    public PersonalAccountController(ClientsService clientsService) {
//        this.clientsService = clientsService;
//    }
//
//    @GetMapping
//    public String ShowPersonalAccount(Model model, Principal principal) {
//        model.addAttribute("client", clientsService.getUserByPrincipal(principal));
//        return "/personalAccount";
//    }
//}
