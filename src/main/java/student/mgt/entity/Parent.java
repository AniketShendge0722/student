package student.mgt.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "parents",
        indexes = {
                @Index(name = "idx_parent_email", columnList = "email"),
                @Index(name = "idx_parent_phone", columnList = "phone")
        }
)
public class Parent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "father_name", nullable = false, length = 100)
    private String fatherName;

    @Column(name = "mother_name", nullable = false, length = 100)
    private String motherName;

    @Column(name = "phone", nullable = false, length = 10)
    private String phone;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "address", nullable = false, length = 500)
    private String address;

    @OneToMany(
            mappedBy = "parent",
            fetch = FetchType.LAZY
    )
    private List<Student> students = new ArrayList<>();

    public Parent() {
    }

    public Parent(Long id, String fatherName, String motherName,
                  String phone, String email, String address) {

        this.id = id;
        this.fatherName = fatherName;
        this.motherName = motherName;
        this.phone = phone;
        this.email = email;
        this.address = address;
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

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }
}