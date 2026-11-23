package student.mgt.controller;

import student.mgt.entity.AttendanceStatus;
import student.mgt.service.AttendanceService;
import student.mgt.service.BusService;
import student.mgt.service.ParentService;
import student.mgt.service.RouteService;
import student.mgt.service.StudentService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardRestController {

    private final StudentService studentService;
    private final ParentService parentService;
    private final BusService busService;
    private final RouteService routeService;
    private final AttendanceService attendanceService;

    public DashboardRestController(
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

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getDashboardSummary() {
        LocalDate today = LocalDate.now();
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("date", today);
        summary.put("totalStudents", studentService.count());
        summary.put("activeStudents", studentService.countActiveStudents());
        summary.put("totalParents", parentService.count());
        summary.put("totalBuses", busService.count());
        summary.put("totalRoutes", routeService.count());
        summary.put("todayAttendance", attendanceService.countByDate(today));
        summary.put("presentCount", attendanceService.countByDateAndStatus(today, AttendanceStatus.PRESENT));
        summary.put("absentCount", attendanceService.countByDateAndStatus(today, AttendanceStatus.ABSENT));
        summary.put("lateCount", attendanceService.countByDateAndStatus(today, AttendanceStatus.LATE));

        return ResponseEntity.ok(summary);
    }
}