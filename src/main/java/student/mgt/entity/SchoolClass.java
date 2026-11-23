package student.mgt.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "school_classes",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_school_class_name",
                columnNames = "class_name"
        )
)
public class SchoolClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "class_name", nullable = false, unique = true, length = 60)
    private String className;

    @Column(nullable = false)
    private boolean active = true;

    public SchoolClass() {
    }

    public SchoolClass(String className) {
        this.className = className;
        this.active = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}