package student.mgt.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class StudentDTO {

    private Long id;

    @NotBlank(message = "Student name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "Name can contain only letters and spaces")
    private String name;

    @NotBlank(message = "Roll number is required")
    @Size(min = 1, max = 30)
    private String rollNo;

    @NotBlank(message = "Class is required")
    @Size(max = 30)
    private String className;


    @Pattern(regexp = "Male|Female|Other",
            message = "Select a valid gender")
    private String gender;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dob;

    @NotBlank(message = "Address is required")
    @Size(max = 500)
    private String address;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9][0-9]{9}$",
            message = "Enter a valid 10-digit mobile number")
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    @Size(max = 100)
    private String email;

    @NotNull(message = "Admission date is required")
    @PastOrPresent(message = "Admission date cannot be in the future")
    private LocalDate admissionDate;

    @NotNull(message = "Please select a parent")
    @Positive(message = "Invalid parent selection")
    private Long parentId;

    @Positive(message = "Invalid bus selection")
    private Long busId;

    @NotNull(message = "Student active status is required")
    private Boolean active = true;

    public StudentDTO() {
    }

    public StudentDTO(Long id, String name, String rollNo,
                      String className, String gender, LocalDate dob,
                      String address, String phone, String email,
                      LocalDate admissionDate, Long parentId,
                      Long busId, Boolean active) {
        this.id = id;
        this.name = name;
        this.rollNo = rollNo;
        this.className = className;
        this.gender = gender;
        this.dob = dob;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.admissionDate = admissionDate;
        this.parentId = parentId;
        this.busId = busId;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(LocalDate admissionDate) {
        this.admissionDate = admissionDate;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getBusId() {
        return busId;
    }

    public void setBusId(Long busId) {
        this.busId = busId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}