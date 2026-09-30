package org.gk.flatirons.assessment.ims.service;

import org.gk.flatirons.assessment.common.exception.dto.customExceptions.FeedbackConflictException;
import org.gk.flatirons.assessment.common.exception.dto.customExceptions.ResourceNotFoundException;
import org.gk.flatirons.assessment.ims.constant.InterviewStatus;
import org.gk.flatirons.assessment.ims.dto.request.InterviewSearchRequest;
import org.gk.flatirons.assessment.ims.dto.request.ScheduleInterviewRequest;
import org.gk.flatirons.assessment.ims.dto.response.InterviewResponse;
import org.gk.flatirons.assessment.ims.dto.response.PagedResponse;
import org.gk.flatirons.assessment.ims.dto.specification.InterviewSpecifications;
import org.gk.flatirons.assessment.ims.entity.Candidate;
import org.gk.flatirons.assessment.ims.entity.Interview;
import org.gk.flatirons.assessment.ims.entity.Interviewer;
import org.gk.flatirons.assessment.ims.factory.InterviewModeHandlerFactory;
import org.gk.flatirons.assessment.ims.repository.InterviewRepository;
import org.gk.flatirons.assessment.ims.utils.ResponseMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
public class InterviewService {

    private static final Set<String> SORTABLE = Set.of("scheduledStart", "scheduledEnd", "round", "status");

    private final CandidateService candidateService;
    private final InterviewerService interviewerService;
    private final InterviewRepository interviewRepository;
    private final ResponseMapper responseMapper;
    private final InterviewModeHandlerFactory modeHandlerFactory;


    public InterviewService(CandidateService candidateService, InterviewerService interviewerService, InterviewRepository interviewRepository, ResponseMapper responseMapper, InterviewModeHandlerFactory modeHandlerFactory) {
        this.candidateService = candidateService;
        this.interviewerService = interviewerService;
        this.interviewRepository = interviewRepository;
        this.responseMapper = responseMapper;
        this.modeHandlerFactory = modeHandlerFactory;
    }

    @Transactional
    public InterviewResponse schedule(ScheduleInterviewRequest request) {
        Candidate candidate = candidateService.fetchCandidateWithConflictValidation(request.candidateId(), request.scheduledStart(), request.scheduledEnd());
        List<Interviewer> interviewers = interviewerService.fetchAllInterviewersWithCountAndConflictValidation(request.interviewerIds(), request.scheduledStart(), request.scheduledEnd());
        Interview scheduledInterview = createAndPersistNewInterview(candidate, interviewers, request);
        modeHandlerFactory.getHandler(scheduledInterview.getMode()).prepare(scheduledInterview);
        return responseMapper.mapToInterviewResponseDto(scheduledInterview);
    }


    private Interview createAndPersistNewInterview(Candidate candidate, List<Interviewer> interviewers, ScheduleInterviewRequest request) {
        Interview interview = Interview.builder()
                .candidate(candidate)
                .interviewers(interviewers)
                .round(request.round())
                .mode(request.mode())
                .schedule(request.scheduledStart(), request.scheduledEnd())
                .build();
        return interviewRepository.save(interview);
    }


    public Interview fetchInterviewForFeedback(Integer interviewId) {
        Interview interview = interviewRepository.findByIdForFeedback(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview", interviewId));
        if (interview.getStatus() == InterviewStatus.CANCELLED ) {
            throw new FeedbackConflictException("Cannot submit feedback for a cancelled interview");
        } else if (Set.of(InterviewStatus.OFFERED,  InterviewStatus.REJECTED).contains(interview.getStatus())) {
            throw new FeedbackConflictException("Cannot submit feedback for a Offered / Rejected interview");
        } else if (Instant.now().isBefore(interview.getScheduledStart())) {
            throw new FeedbackConflictException("Cannot submit feedback before the interview has started");
        }
        return interview;
    }

    @Transactional(readOnly = true)
    public PagedResponse<InterviewResponse> search(InterviewSearchRequest request, Pageable pageable) {
        Specification<Interview> spec = Specification.allOf(
                InterviewSpecifications.candidateIdEquals(request.candidateId()),
                InterviewSpecifications.candidateNameContains(request.candidateName()),
                InterviewSpecifications.interviewerNameContains(request.interviewerName()));
        return PagedResponse.from(interviewRepository.findAll(spec, sanitize(pageable))
                        .map(responseMapper::mapToInterviewResponseDto));
    }

    private Pageable sanitize(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) {
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "scheduledStart").and(Sort.by("id")));
        }
        pageable.getSort().forEach(o -> {
            if (!SORTABLE.contains(o.getProperty())) {
                throw new IllegalArgumentException("Unsupported sort field: " + o.getProperty());
            }
        });
        return pageable;
    }
}
