package org.gk.flatirons.assessment.ims.service;

import org.gk.flatirons.assessment.common.exception.dto.customExceptions.FeedbackConflictException;
import org.gk.flatirons.assessment.common.exception.dto.customExceptions.InvalidRequestException;
import org.gk.flatirons.assessment.ims.dto.request.SubmitFeedbackRequest;
import org.gk.flatirons.assessment.ims.dto.response.FeedbackResponse;
import org.gk.flatirons.assessment.ims.entity.Feedback;
import org.gk.flatirons.assessment.ims.entity.Interview;
import org.gk.flatirons.assessment.ims.entity.Interviewer;
import org.gk.flatirons.assessment.ims.repository.FeedbackRepository;
import org.gk.flatirons.assessment.ims.utils.ResponseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final InterviewService interviewService;
    private final ResponseMapper responseMapper;

    public FeedbackService(FeedbackRepository feedbackRepository, InterviewService interviewService, ResponseMapper responseMapper) {
        this.feedbackRepository = feedbackRepository;
        this.interviewService = interviewService;
        this.responseMapper = responseMapper;
    }

    private Interviewer validateAndGetMappedInterviewer(Interview interview, Integer expectedInterviewerId) {
        if (feedbackRepository.existsByInterviewIdAndInterviewerId(interview.getId(), expectedInterviewerId)) {
            throw new FeedbackConflictException("Feedback already submitted by interviewer " + expectedInterviewerId);
        }
        return interview.getInterviewers().stream()
                .filter(mappedInterviewer -> mappedInterviewer.getId().equals(expectedInterviewerId))
                .findFirst().orElseThrow(() -> new InvalidRequestException(
                        "Interviewer " + expectedInterviewerId + " is not on the panel for interview " + interview.getId()));
    }

    private Feedback createAndPersistNewFeedback(Interview interview, Interviewer interviewer, SubmitFeedbackRequest request) {
        Feedback feedback = Feedback.builder()
                .interview(interview)
                .interviewer(interviewer)
                .rating(request.rating())
                .comments(request.comments())
                .status(request.feedbackStatus())
                .build();
        return feedbackRepository.save(feedback);
    }

    @Transactional
    public FeedbackResponse submit(Integer interviewId, SubmitFeedbackRequest request) {
        Interview interview = interviewService.fetchInterviewForFeedback(interviewId);
        Interviewer interviewer = validateAndGetMappedInterviewer(interview, interviewId);
        Feedback feedback = createAndPersistNewFeedback(interview,interviewer, request);
        return responseMapper.mapToFeedbackResponseDto(feedback);
    }
}