package org.gk.flatirons.assessment.ims.service;

import jakarta.validation.constraints.NotNull;
import org.gk.flatirons.assessment.common.exception.dto.customExceptions.ResourceNotFoundException;
import org.gk.flatirons.assessment.common.exception.dto.customExceptions.SchedulingConflictException;
import org.gk.flatirons.assessment.ims.constant.InterviewStatus;
import org.gk.flatirons.assessment.ims.entity.Candidate;
import org.gk.flatirons.assessment.ims.repository.CandidateRepository;
import org.gk.flatirons.assessment.ims.repository.InterviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;

@Service
@Validated
public class CandidateService {

    private final InterviewRepository interviewRepository;
    private final CandidateRepository candidateRepository;

    public CandidateService(InterviewRepository interviewRepository, CandidateRepository candidateRepository) {
        this.interviewRepository = interviewRepository;
        this.candidateRepository = candidateRepository;
    }

    public Candidate fetchCandidateWithConflictValidation(@NotNull Integer candidateId, Instant startDateTime, Instant endDateTime) {
        Candidate candidate = candidateRepository.findByIdForInterviewSchedule(candidateId).orElseThrow(() -> new ResourceNotFoundException("Candidate", candidateId));
        validateIfCandidateHasOtherInterview(candidate, startDateTime, endDateTime);
        return candidate;
    }

    private void validateIfCandidateHasOtherInterview(Candidate candidate, Instant startDateTime, Instant endDateTime) {
        boolean conflictExists = interviewRepository.existsCandidateConflict(
                candidate.getId(),
                InterviewStatus.SCHEDULED,
                startDateTime,
                endDateTime);
        if (conflictExists) {
            throw new SchedulingConflictException("The Candidate already has an interview in this time slot");
        }
    }
}
