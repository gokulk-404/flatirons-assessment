package org.gk.flatirons.assessment.ims.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BulkInterviewerCreateRequest(@NotEmpty @Size(max = 100) List<@Valid CreateInterviewerRequest> interviewers) {}
