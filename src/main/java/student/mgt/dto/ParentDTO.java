package student.mgt.dto;

import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

public class ParentDTO {

    private Long id;

    @NotBlank(message = "Father name is required")
    @Size(min = 2, max = 100)
    @Pattern(regexp = "^[a-zA-Z ]+$",
            message = "Father name must contain only letters")
    private String fatherName;

    @NotBlank(message = "Mother name is required")
    @Size(min = 2, max = 100)
    @Pattern(regexp = "^[a-zA-Z ]+$",
            message = "Mother name must contain only letters")
    private String motherName;

    @NotBlank(message = "Parent mobile number is required")
    @Pattern(regexp = "^[6-9][0-9]{9}$",
            message = "Enter a valid 10-digit mobile number")
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    @Size(max = 100)
    private String email;

    @NotBlank(message = "Address is required")
    @Size(max = 500)
    private String address;

    private List<Long> studentIds = new ArrayList<>();

    public ParentDTO() {
    }

    public ParentDTO(Long id, String fatherName, String motherName,
                     String phone, String email, String address,
                     List<Long> studentIds) {
        this.id = id;
        this.fatherName = fatherName;
        this.motherName = motherName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.studentIds = studentIds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Long> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(List<Long> studentIds) {
        this.studentIds = studentIds;
    }
}