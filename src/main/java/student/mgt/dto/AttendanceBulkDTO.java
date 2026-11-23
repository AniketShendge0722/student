package student.mgt.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AttendanceBulkDTO {

    private LocalDate date;
    private String className;

    private List<AttendanceBulkItemDTO> rows = new ArrayList<>();

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<AttendanceBulkItemDTO> getRows() {
        return rows;
    }

    public void setRows(List<AttendanceBulkItemDTO> rows) {
        this.rows = rows;
    }
}