package student.mgt.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "blood_groups",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_blood_group",
                columnNames = "blood_group"
        )
)
public class BloodGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "blood_group", nullable = false, unique = true, length = 5)
    private String bloodGroup;

    @Column(nullable = false)
    private boolean active = true;

    public BloodGroup() {
    }

    public BloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
        this.active = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}