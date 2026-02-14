package com.cafe.digital_cafe.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_education")
public class UserEducation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 100)
    private String degree;

    @Column(length = 200)
    private String institution;

    @Column(name = "completion_year", length = 10)
    private String completionYear;

    public UserEducation() {
    }

    public UserEducation(User user, String degree, String institution, String completionYear) {
        this.user = user;
        this.degree = degree;
        this.institution = institution;
        this.completionYear = completionYear;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
