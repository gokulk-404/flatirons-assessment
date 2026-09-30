package org.gk.flatirons.assessment.ims.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SubmitFeedbackRequest(
        @NotNull Integer interviewerId,
        @NotNull @Min(1) @Max(9) Integer rating,
        @NotBlank @Size(max = 255) String comments
) {
}