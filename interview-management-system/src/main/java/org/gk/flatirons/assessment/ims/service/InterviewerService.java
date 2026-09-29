package org.gk.flatirons.assessment.ims.service;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.gk.flatirons.assessment.common.exception.dto.customExceptions.ResourceNotFoundException;
import org.gk.flatirons.assessment.common.exception.dto.customExceptions.SchedulingConflictException;
import org.gk.flatirons.assessment.ims.constant.InterviewStatus;
import org.gk.flatirons.assessment.ims.entity.Interviewer;
import org.gk.flatirons.assessment.ims.repository.InterviewRepository;
import org.gk.flatirons.assessment.ims.repository.InterviewerRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Validated
public class InterviewerService {

    private final InterviewerRepository interviewerRepository;
    private final InterviewRepository interviewRepository;

    public InterviewerService(InterviewerRepository interviewerRepository, InterviewRepository interviewRepository) {
        this.interviewerRepository = interviewerRepository;
        this.interviewRepository = interviewRepository;
    }

    public List<Interviewer> fetchAllInterviewersWithCountAndConflictValidation(@NotEmpty Set<Integer> interviewerIds, @NotNull Instant scheduleStart, @NotNull Instant scheduledEnd) {
        List<Interviewer> availableInterviewers = interviewerRepository.findAllByIdForInterviewSchedule(interviewerIds);
        validateMissingInterviewers(interviewerIds, availableInterviewers);
        validateNoInterviewerHasConflict(availableInterviewers, scheduleStart, scheduledEnd);
        return availableInterviewers;
    }

    private void validateMissingInterviewers(Set<Integer> expectedInterviewerIds, List<Interviewer> availableInterviewers) {
        Set<Integer> foundIds = availableInterviewers.stream().map(Interviewer::getId).collect(Collectors.toSet());
        Set<Integer> missingIds = new HashSet<>(expectedInterviewerIds);
        missingIds.removeAll(foundIds);

        if (!missingIds.isEmpty()) {
            throw new ResourceNotFoundException("Interviewer(s)", missingIds);
        }
    }

    private void validateNoInterviewerHasConflict(List<Interviewer> interviewers, Instant startDateTime, Instant endDateTime) {
        boolean conflictExists = interviewRepository.existsInterviewerConflict(
                interviewers.stream().map(Interviewer::getId).toList(),
                InterviewStatus.SCHEDULED,
                startDateTime,
                endDateTime);
        if (conflictExists) {
            throw new SchedulingConflictException("An interviewer already has an interview in this time slot");
        }
    }
}
