package student.mgt.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "school_sections",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_school_section_name",
                columnNames = "section_name"
        )
)
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "section_name", nullable = false, unique = true, length = 20)
    private String sectionName;

    @Column(nullable = false)
    private boolean active = true;

    public Section() {
    }

    public Section(String sectionName) {
        this.sectionName = sectionName;
        this.active = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}