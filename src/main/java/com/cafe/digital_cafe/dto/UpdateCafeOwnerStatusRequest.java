package com.cafe.digital_cafe.dto;

/**
 * Request to activate or deactivate a cafe owner. Admin only.
 */
public class UpdateCafeOwnerStatusRequest {

    private boolean active;

    public UpdateCafeOwnerStatusRequest() {
    }

    public UpdateCafeOwnerStatusRequest(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
