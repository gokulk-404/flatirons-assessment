package org.gk.flatirons.assessment.ims.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.gk.flatirons.assessment.ims.constant.InterviewMode;
import org.gk.flatirons.assessment.ims.constant.InterviewRound;

import java.time.Instant;
import java.util.Set;

public record ScheduleInterviewRequest(
        @NotNull Integer candidateId,
        @NotEmpty Set<Integer> interviewerIds,
        @NotNull InterviewRound round,
        @NotNull InterviewMode mode,
        @NotNull @Future Instant scheduledStart,
        @NotNull Instant scheduledEnd
) {
    @AssertTrue(message = "Scheduled end time must be after the start time")
    public boolean isEndAfterStart() {
        if (scheduledStart == null || scheduledEnd == null) {
            return true;
        }
        return scheduledEnd.isAfter(scheduledStart);
    }
}
