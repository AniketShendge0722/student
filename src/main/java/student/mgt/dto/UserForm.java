
package student.mgt.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

public class UserForm {

    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 60,
            message = "Username must be between 3 and 60 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9._-]+$",
            message = "Username can contain letters, numbers, dots, underscores and hyphens only"
    )
    private String username;

    // Required during creation; optional during editing.
    private String password;

    @NotEmpty(message = "Assign the user to at least one school and role")
    @Valid
    private List<UserTenantRoleForm> assignments = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<UserTenantRoleForm> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<UserTenantRoleForm> assignments) {
        this.assignments = assignments;
    }
}
