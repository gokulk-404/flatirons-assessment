package org.gk.flatirons.assessment.ims.service;

import jakarta.transaction.Transactional;
import org.gk.flatirons.assessment.common.exception.dto.customExceptions.ResourceNotFoundException;
import org.gk.flatirons.assessment.common.exception.dto.customExceptions.SchedulingConflictException;
import org.gk.flatirons.assessment.ims.constant.InterviewStatus;
import org.gk.flatirons.assessment.ims.dto.request.BulkInterviewerCreateRequest;
import org.gk.flatirons.assessment.ims.dto.request.CreateInterviewerRequest;
import org.gk.flatirons.assessment.ims.dto.response.InterviewerDetail;
import org.gk.flatirons.assessment.ims.entity.Interviewer;
import org.gk.flatirons.assessment.ims.repository.InterviewRepository;
import org.gk.flatirons.assessment.ims.repository.InterviewerRepository;
import org.gk.flatirons.assessment.ims.utils.ResponseMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class InterviewerService {

    private final InterviewerRepository interviewerRepository;
    private final InterviewRepository interviewRepository;
    private final ResponseMapper responseMapper;

    public InterviewerService(InterviewerRepository interviewerRepository, InterviewRepository interviewRepository, ResponseMapper responseMapper) {
        this.interviewerRepository = interviewerRepository;
        this.interviewRepository = interviewRepository;
        this.responseMapper = responseMapper;
    }

    public List<Interviewer> fetchAllInterviewersWithCountAndConflictValidation(Set<Integer> interviewerIds, Instant scheduleStart, Instant scheduledEnd) {
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

    private Interviewer createAndPersistNewInterviewer(CreateInterviewerRequest request) {
        Interviewer interviewer = Interviewer.builder()
                .fullName(request.fullName())
                .email(request.emailId())
                .department(request.department())
                .build();
        return interviewerRepository.save(interviewer);
    }

    @Transactional
    public InterviewerDetail createNewInterviewer(CreateInterviewerRequest request) {
        return responseMapper.mapToInterviewerDetailDto(createAndPersistNewInterviewer(request));
    }

    @Transactional
    public List<InterviewerDetail> createNewInterviewersBulk(BulkInterviewerCreateRequest request) {
        return request.interviewers().stream()
                .map(interviewerCreateRequest -> responseMapper.mapToInterviewerDetailDto(createAndPersistNewInterviewer(interviewerCreateRequest)))
                .toList();
    }
}
