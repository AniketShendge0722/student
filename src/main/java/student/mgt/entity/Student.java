package student.mgt.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "students",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_student_roll_no",
                        columnNames = "roll_no"
                )
        },
        indexes = {
                @Index(name = "idx_student_name", columnList = "name"),
                @Index(name = "idx_student_class", columnList = "class_name"),
                @Index(name = "idx_student_bus", columnList = "bus_id"),
                @Index(name = "idx_student_parent", columnList = "parent_id")
        }
)
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "roll_no", nullable = false, unique = true, length = 30)
    private String rollNo;

    @Column(name = "class_name", nullable = false, length = 30)
    private String className;

    @Column(name = "gender", nullable = false, length = 10)
    private String gender;

    @Column(name = "dob", nullable = false)
    private LocalDate dob;

    @Column(name = "address", nullable = false, length = 500)
    private String address;

    @Column(name = "phone", nullable = false, length = 10)
    private String phone;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "admission_date", nullable = false)
    private LocalDate admissionDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "parent_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_student_parent")
    )
    private Parent parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "bus_id",
            foreignKey = @ForeignKey(name = "fk_student_bus")
    )
    private Bus bus;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    public Student() {
    }

    public Student(Long id, String name, String rollNo,
                   String className, String gender,
                   LocalDate dob, String address,
                   String phone, String email,
                   LocalDate admissionDate,
                   Parent parent, Bus bus, Boolean active) {

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
        this.parent = parent;
        this.bus = bus;
        this.active = active;
    }

    @PrePersist
    public void prePersist() {
        if (active == null) {
            active = true;
        }
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

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}