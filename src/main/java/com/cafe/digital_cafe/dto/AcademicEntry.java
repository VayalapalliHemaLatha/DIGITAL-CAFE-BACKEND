package com.cafe.digital_cafe.dto;

/**
 * Single academic entry for profile. All fields optional.
 */
public class AcademicEntry {

    private String degree;
    private String institution;
    private String completionYear;

    public AcademicEntry() {
    }

    public AcademicEntry(String degree, String institution, String completionYear) {
        this.degree = degree;
        this.institution = institution;
        this.completionYear = completionYear;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getCompletionYear() {
        return completionYear;
    }

    public void setCompletionYear(String completionYear) {
        this.completionYear = completionYear;
    }
}
