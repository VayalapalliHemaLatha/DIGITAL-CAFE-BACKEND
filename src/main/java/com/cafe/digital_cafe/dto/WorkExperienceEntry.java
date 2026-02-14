package com.cafe.digital_cafe.dto;

/**
 * Single work experience entry for profile. All fields optional.
 */
public class WorkExperienceEntry {

    private String company;
    private String role;
    private String duration;
    private String description;

    public WorkExperienceEntry() {
    }

    public WorkExperienceEntry(String company, String role, String duration, String description) {
        this.company = company;
        this.role = role;
        this.duration = duration;
        this.description = description;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
