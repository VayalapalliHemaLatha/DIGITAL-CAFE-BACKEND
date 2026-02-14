package com.cafe.digital_cafe.service;

import com.cafe.digital_cafe.dto.*;
import com.cafe.digital_cafe.entity.*;
import com.cafe.digital_cafe.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Update profile of the authenticated user. All fields optional - only non-null fields are updated.
     * Requires valid JWT.
     */
    @Transactional
    public ProfileResponse updateProfile(UpdateProfileRequest request) {
        User user = getCurrentUserOrThrow();

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getDob() != null) user.setDob(request.getDob());
        if (request.getGender() != null) user.setGender(request.getGender());
        if (request.getStreet() != null) user.setStreet(request.getStreet());
        if (request.getPlotNo() != null) user.setPlotNo(request.getPlotNo());
        if (request.getCity() != null) user.setCity(request.getCity());
        if (request.getPincode() != null) user.setPincode(request.getPincode());

        if (request.getAcademicInformation() != null) {
            user.getEducationList().clear();
            for (AcademicEntry e : request.getAcademicInformation()) {
                user.getEducationList().add(new UserEducation(user,
                        e.getDegree(), e.getInstitution(), e.getCompletionYear()));
            }
        }

        if (request.getWorkExperience() != null) {
            user.getWorkExperienceList().clear();
            for (WorkExperienceEntry w : request.getWorkExperience()) {
                user.getWorkExperienceList().add(new UserWorkExperience(user,
                        w.getCompany(), w.getRole(), w.getDuration(), w.getDescription()));
            }
        }

        user = userRepository.save(user);
        return toProfileResponse(user);
    }

    /**
     * Get profile of the authenticated user.
     */
    public ProfileResponse getProfile() {
        User user = getCurrentUserOrThrow();
        return toProfileResponse(user);
    }

    private User getCurrentUserOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            throw new org.springframework.security.authentication.BadCredentialsException("Not authenticated");
        }
        Object principal = auth.getPrincipal();
        if (!(principal instanceof String email)) {
            throw new org.springframework.security.authentication.BadCredentialsException("Not authenticated");
        }
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new org.springframework.security.authentication.BadCredentialsException("User not found"));
    }

    private ProfileResponse toProfileResponse(User user) {
        ProfileResponse r = new ProfileResponse();
        r.setId(user.getId());
        r.setName(user.getName());
        r.setEmail(user.getEmail());
        r.setPhone(user.getPhone());
        r.setAddress(user.getAddress());
        r.setRoleType(user.getRoleType());
        r.setFirstName(user.getFirstName());
        r.setLastName(user.getLastName());
        r.setDob(user.getDob());
        r.setGender(user.getGender());
        r.setStreet(user.getStreet());
        r.setPlotNo(user.getPlotNo());
        r.setCity(user.getCity());
        r.setPincode(user.getPincode());

        List<AcademicEntry> academics = user.getEducationList().stream()
                .map(e -> new AcademicEntry(e.getDegree(), e.getInstitution(), e.getCompletionYear()))
                .collect(Collectors.toList());
        r.setAcademicInformation(academics);

        List<WorkExperienceEntry> work = user.getWorkExperienceList().stream()
                .map(w -> new WorkExperienceEntry(w.getCompany(), w.getRole(), w.getDuration(), w.getDescription()))
                .collect(Collectors.toList());
        r.setWorkExperience(work);

        return r;
    }
}
