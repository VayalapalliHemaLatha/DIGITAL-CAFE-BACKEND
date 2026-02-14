package com.cafe.digital_cafe.dto;

import com.cafe.digital_cafe.entity.Gender;
import com.cafe.digital_cafe.entity.RoleType;

import java.time.LocalDate;
import java.util.List;

/**
 * Response for user profile. Includes all profile fields.
 */
public class ProfileResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private RoleType roleType;

    private String firstName;
    private String lastName;
    private LocalDate dob;
    private Gender gender;

    private List<AcademicEntry> academicInformation;
    private List<WorkExperienceEntry> workExperience;

    private String street;
    private String plotNo;
    private String city;
    private String pincode;

    public ProfileResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public List<AcademicEntry> getAcademicInformation() {
        return academicInformation;
    }

    public void setAcademicInformation(List<AcademicEntry> academicInformation) {
        this.academicInformation = academicInformation;
    }

    public List<WorkExperienceEntry> getWorkExperience() {
        return workExperience;
    }

    public void setWorkExperience(List<WorkExperienceEntry> workExperience) {
        this.workExperience = workExperience;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPlotNo() {
        return plotNo;
    }

    public void setPlotNo(String plotNo) {
        this.plotNo = plotNo;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }
}
