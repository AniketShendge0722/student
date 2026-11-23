
package student.mgt.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import student.mgt.validator.ValidBusNumber;

import java.util.ArrayList;
import java.util.List;

public class BusDTO {

    private Long id;

    @NotBlank(message = "Bus number is required.")
    @Size(
            max = 20,
            message = "Bus number cannot exceed 20 characters."
    )
    @Pattern(
            regexp = "^[A-Z]{2}\\s[0-9]{1,2}\\s[A-Z]{1,3}\\s[0-9]{4}$",
            message = "Enter bus number like MH 10 CR 3451."
    )
    @ValidBusNumber
    private String busNumber;

    @NotBlank(message = "Driver name is required.")
    @Size(min = 2, max = 100)
    @Pattern(
            regexp = "^[a-zA-Z ]+$",
            message = "Driver name must contain only letters."
    )
    private String driverName;

    @NotBlank(message = "Driver phone is required.")
    @Pattern(
            regexp = "^[6-9][0-9]{9}$",
            message = "Enter a valid 10-digit mobile number."
    )
    private String driverPhone;

    @NotNull(message = "Bus capacity is required.")
    @Min(value = 1, message = "Capacity must be at least 1.")
    @Max(value = 100, message = "Capacity cannot exceed 100.")
    private Integer capacity;

    @NotNull(message = "Please select a route.")
    @Positive(message = "Invalid route selection.")
    private Long routeId;

    private List<Long> studentIds = new ArrayList<>();

    public BusDTO() {
    }

    public BusDTO(
            Long id,
            String busNumber,
            String driverName,
            String driverPhone,
            Integer capacity,
            Long routeId,
            List<Long> studentIds) {

        this.id = id;
        this.busNumber = busNumber;
        this.driverName = driverName;
        this.driverPhone = driverPhone;
        this.capacity = capacity;
        this.routeId = routeId;
        this.studentIds = studentIds != null
                ? studentIds
                : new ArrayList<>();
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

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public List<Long> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(List<Long> studentIds) {
        this.studentIds = studentIds != null
                ? studentIds
                : new ArrayList<>();
    }
}
