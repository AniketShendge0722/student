package student.mgt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import student.mgt.service.StudentService;
import student.mgt.service.ParentService;
import student.mgt.service.BusService;
import student.mgt.service.RouteService;
import student.mgt.service.AttendanceService;
import student.mgt.entity.AttendanceStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
public class DashboardController {

    private final StudentService studentService;
    private final ParentService parentService;
    private final BusService busService;
    private final RouteService routeService;
    private final AttendanceService attendanceService;

    public DashboardController(
            StudentService studentService,
            ParentService parentService,
            BusService busService,
            RouteService routeService,
            AttendanceService attendanceService) {

        this.studentService = studentService;
        this.parentService = parentService;
        this.busService = busService;
        this.routeService = routeService;
        this.attendanceService = attendanceService;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {

        LocalDate today = LocalDate.now();

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy");

        // Dashboard date
        String formattedDate = today.format(formatter);

        model.addAttribute("today", formattedDate);
        model.addAttribute("todayDate", formattedDate);

        // Total records
        model.addAttribute(
                "totalStudents",
                studentService.count()
        );

        model.addAttribute(
                "activeStudents",
                studentService.countActiveStudents()
        );

        model.addAttribute(
                "totalBuses",
                busService.count()
        );

        model.addAttribute(
                "totalRoutes",
                routeService.count()
        );

        model.addAttribute(
                "totalParents",
                parentService.count()
        );

        // Today's attendance
        model.addAttribute(
                "todayAttendance",
                attendanceService.countByDate(today)
        );

        model.addAttribute(
                "presentCount",
                attendanceService.countByDateAndStatus(
                        today,
                        AttendanceStatus.PRESENT
                )
        );

        model.addAttribute(
                "absentCount",
                attendanceService.countByDateAndStatus(
                        today,
                        AttendanceStatus.ABSENT
                )
        );

        model.addAttribute(
                "lateCount",
                attendanceService.countByDateAndStatus(
                        today,
                        AttendanceStatus.LATE
                )
        );

        // Recent activity data
        model.addAttribute(
                "recentAttendance",
                attendanceService.findByDate(today)
        );

        return "dashboard";
    }
}