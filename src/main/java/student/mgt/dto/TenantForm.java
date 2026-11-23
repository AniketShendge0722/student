
package student.mgt.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class TenantForm {

    private Long id;

    @NotBlank(message = "School name is required")
    @Size(min = 2, max = 150,
            message = "School name must be between 2 and 150 characters")
    private String tenantName;

    @NotBlank(message = "School code is required")
    @Size(min = 3, max = 50,
            message = "School code must be between 3 and 50 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9_-]+$",
            message = "School code can contain letters, numbers, hyphens and underscores only"
    )
    private String tenantCode;

    @Size(max = 500, message = "Address cannot exceed 500 characters")
    private String address;

    @Email(message = "Enter a valid email address")
    @Size(max = 150, message = "Email cannot exceed 150 characters")
    private String contactEmail;

    @Pattern(
            regexp = "^[0-9+() -]{0,15}$",
            message = "Enter a valid contact phone number"
    )
    private String contactPhone;

    private boolean active = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
