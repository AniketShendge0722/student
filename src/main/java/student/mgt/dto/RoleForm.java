
package student.mgt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RoleForm {

    @NotBlank(message = "Role name is required")
    @Size(min = 2, max = 50,
            message = "Role name must be between 2 and 50 characters")
    private String roleName;

    @Size(max = 300,
            message = "Description cannot exceed 300 characters")
    private String description;

    public RoleForm() {
    }

    public RoleForm(String roleName, String description) {
        this.roleName = roleName;
        this.description = description;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
