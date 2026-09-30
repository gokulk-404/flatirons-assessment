package org.gk.flatirons.assessment.ims.service;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.gk.flatirons.assessment.common.exception.dto.customExceptions.ResourceNotFoundException;
import org.gk.flatirons.assessment.common.exception.dto.customExceptions.SchedulingConflictException;
import org.gk.flatirons.assessment.ims.constant.InterviewStatus;
import org.gk.flatirons.assessment.ims.dto.request.BulkCandidateCreateRequest;
import org.gk.flatirons.assessment.ims.dto.request.CreateCandidateRequest;
import org.gk.flatirons.assessment.ims.dto.response.CandidateDetail;
import org.gk.flatirons.assessment.ims.entity.Candidate;
import org.gk.flatirons.assessment.ims.repository.CandidateRepository;
import org.gk.flatirons.assessment.ims.repository.InterviewRepository;
import org.gk.flatirons.assessment.ims.utils.ResponseMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.List;

@Service
@Validated
public class CandidateService {

    private final InterviewRepository interviewRepository;
    private final CandidateRepository candidateRepository;
    private final ResponseMapper responseMapper;

    public CandidateService(InterviewRepository interviewRepository, CandidateRepository candidateRepository, ResponseMapper responseMapper) {
        this.interviewRepository = interviewRepository;
        this.candidateRepository = candidateRepository;
        this.responseMapper = responseMapper;
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

    private Candidate createAndPersistNewCandidate(CreateCandidateRequest request) {
        Candidate candidate = Candidate.builder()
                .fullName(request.fullName())
                .email(request.emailId())
                .phone(request.phoneNumber())
                .experience(request.experience())
                .skills(request.skills())
                .resumeUrl(request.resumeUrl())
                .build();
        return candidateRepository.save(candidate);
    }

    @Transactional
    public CandidateDetail createNewCandidate(CreateCandidateRequest request) {
        return responseMapper.mapToCandidateResponseDto(createAndPersistNewCandidate(request));
    }

    @Transactional
    public List<CandidateDetail> createNewCandidatesBulk(BulkCandidateCreateRequest request) {
        return request.candidates().stream()
                .map(candidateCreateRequest -> responseMapper.mapToCandidateResponseDto(createAndPersistNewCandidate(candidateCreateRequest)))
                .toList();
    }
}
