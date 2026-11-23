
package student.mgt.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class UserTenantRoleForm {

    @NotNull(message = "Please select a school")
    @Positive(message = "Invalid school selection")
    private Long tenantId;

    @NotNull(message = "Please select a role")
    @Positive(message = "Invalid role selection")
    private Long roleId;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
