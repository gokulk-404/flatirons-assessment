package org.gk.flatirons.assessment.ims.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.gk.flatirons.assessment.ims.constant.InterviewerDepartment;

public record CreateInterviewerRequest(
        @NotNull String fullName,
        @NotNull @Email String emailId,
        @NotNull InterviewerDepartment department) {}
