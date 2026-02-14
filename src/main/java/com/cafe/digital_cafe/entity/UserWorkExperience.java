package com.cafe.digital_cafe.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_work_experience")
public class UserWorkExperience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 150)
    private String company;

    @Column(length = 100)
    private String role;

    @Column(length = 50)
    private String duration;

    @Column(length = 500)
    private String description;

    public UserWorkExperience() {
    }

    public UserWorkExperience(User user, String company, String role, String duration, String description) {
        this.user = user;
        this.company = company;
        this.role = role;
        this.duration = duration;
        this.description = description;
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
