package org.gk.flatirons.assessment.ims.utils;

import org.gk.flatirons.assessment.ims.dto.response.CandidateDetail;
import org.gk.flatirons.assessment.ims.dto.response.FeedbackResponse;
import org.gk.flatirons.assessment.ims.dto.response.InterviewResponse;
import org.gk.flatirons.assessment.ims.dto.response.InterviewerDetail;
import org.gk.flatirons.assessment.ims.entity.Candidate;
import org.gk.flatirons.assessment.ims.entity.Feedback;
import org.gk.flatirons.assessment.ims.entity.Interview;
import org.gk.flatirons.assessment.ims.entity.Interviewer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public final class ResponseMapper {

    private ResponseMapper() {}

    public InterviewResponse mapToInterviewResponseDto(Interview interview) {
        Candidate candidate = interview.getCandidate();
        List<InterviewerDetail> interviewers = interview.getInterviewers().stream()
                .map(interviewer -> new InterviewerDetail(interviewer.getId(), interviewer.getFullName(), interviewer.getEmail(), interviewer.getDepartment()))
                .toList();

        return new InterviewResponse(
                interview.getId(),
                candidate.getId(),
                candidate.getFullName(),
                interviewers,
                interview.getRound(),
                interview.getMode(),
                interview.getStatus(),
                interview.getScheduledStart(),
                interview.getScheduledEnd());
    }

    public CandidateDetail mapToCandidateResponseDto(Candidate candidate) {
        return new CandidateDetail(
                candidate.getId(),
                candidate.getFullName(),
                candidate.getEmail()
        );
    }

    public InterviewerDetail mapToInterviewerDetailDto(Interviewer interviewer) {
        return new InterviewerDetail(
                interviewer.getId(),
                interviewer.getFullName(),
                interviewer.getEmail(),
                interviewer.getDepartment()
        );
    }

    public FeedbackResponse mapToFeedbackResponseDto(Feedback feedback) {
        return new FeedbackResponse(
                feedback.getId(),
                feedback.getInterview().getId(),
                feedback.getInterviewer().getId(),
                feedback.getInterviewer().getFullName(),
                feedback.getRating(),
                feedback.getComments(),
                feedback.getStatus());
    }
}
