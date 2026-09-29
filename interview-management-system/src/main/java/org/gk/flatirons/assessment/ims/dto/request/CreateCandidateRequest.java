package org.gk.flatirons.assessment.ims.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record CreateCandidateRequest(
        @NotNull String fullName,
        @NotNull @Email String emailId,
        @NotNull String phoneNumber,
        @NotNull String experience,
        @NotNull String skills,
        String resumeUrl) {}
