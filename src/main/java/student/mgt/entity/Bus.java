package student.mgt.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "buses",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_bus_number",
                        columnNames = "bus_number"
                )
        },
        indexes = {
                @Index(name = "idx_bus_route", columnList = "route_id")
        }
)
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bus_number", nullable = false,
            unique = true, length = 30)
    private String busNumber;

    @Column(name = "driver_name", nullable = false, length = 100)
    private String driverName;

    @Column(name = "driver_phone", nullable = false, length = 10)
    private String driverPhone;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "route_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_bus_route")
    )
    private Route route;

    @OneToMany(
            mappedBy = "bus",
            fetch = FetchType.LAZY
    )
    private List<Student> students = new ArrayList<>();

    public Bus() {
    }

    public Bus(Long id, String busNumber, String driverName,
               String driverPhone, Integer capacity, Route route) {

        this.id = id;
        this.busNumber = busNumber;
        this.driverName = driverName;
        this.driverPhone = driverPhone;
        this.capacity = capacity;
        this.route = route;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }
}