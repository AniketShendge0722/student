package student.mgt.util;

import java.util.Optional;

public final class TenantContextHolder {

    private static final ThreadLocal<Long> CURRENT_TENANT =
            new ThreadLocal<>();

    private TenantContextHolder() {
        // Utility class
    }

    /**
     * Set the tenant ID for the current request/thread.
     */
    public static void setTenantId(Long tenantId) {

        if (tenantId == null || tenantId <= 0) {
            throw new IllegalArgumentException(
                    "A valid tenant ID is required."
            );
        }

        CURRENT_TENANT.set(tenantId);
    }

    /**
     * Return the current tenant ID if one has been set.
     */
    public static Optional<Long> getTenantId() {
        return Optional.ofNullable(CURRENT_TENANT.get());
    }

    /**
     * Return the current tenant ID or reject the operation.
     */
    public static Long requireTenantId() {

        Long tenantId = CURRENT_TENANT.get();

        if (tenantId == null) {
            throw new IllegalStateException(
                    "No active school context is available."
            );
        }

        return tenantId;
    }

    /**
     * Check whether a tenant context is available.
     */
    public static boolean hasTenant() {
        return CURRENT_TENANT.get() != null;
    }

    /**
     * Clear the context after request processing.
     */
    public static void clear() {
        CURRENT_TENANT.remove();
    }
}