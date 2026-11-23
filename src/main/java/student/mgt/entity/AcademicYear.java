package student.mgt.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(
        name = "academic_years",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_academic_year_name",
                columnNames = "year_name"
        )
)
public class AcademicYear {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "year_name", nullable = false, unique = true, length = 20)
    private String yearName;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(nullable = false, length = 20)
    private String status = "INACTIVE";

    public AcademicYear() {
    }

    public AcademicYear(
            String yearName,
            LocalDate startDate,
            LocalDate endDate,
            String status) {

        this.yearName = yearName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getYearName() {
        return yearName;
    }

    public void setYearName(String yearName) {
        this.yearName = yearName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}