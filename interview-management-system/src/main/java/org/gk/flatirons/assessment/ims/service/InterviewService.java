package org.gk.flatirons.assessment.ims.service;

import org.gk.flatirons.assessment.ims.dto.request.ScheduleInterviewRequest;
import org.gk.flatirons.assessment.ims.dto.response.InterviewResponse;
import org.gk.flatirons.assessment.ims.entity.Candidate;
import org.gk.flatirons.assessment.ims.entity.Interview;
import org.gk.flatirons.assessment.ims.entity.Interviewer;
import org.gk.flatirons.assessment.ims.factory.InterviewModeHandlerFactory;
import org.gk.flatirons.assessment.ims.repository.InterviewRepository;
import org.gk.flatirons.assessment.ims.utils.ResponseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InterviewService {

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


}
