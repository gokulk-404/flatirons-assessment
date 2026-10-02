package org.gk.flatirons.assessment.ims.service;

import org.gk.flatirons.assessment.common.exception.dto.customExceptions.FeedbackConflictException;
import org.gk.flatirons.assessment.common.exception.dto.customExceptions.InvalidRequestException;
import org.gk.flatirons.assessment.common.exception.dto.customExceptions.ResourceNotFoundException;
import org.gk.flatirons.assessment.ims.constant.FeedbackStatus;
import org.gk.flatirons.assessment.ims.dto.request.SubmitFeedbackRequest;
import org.gk.flatirons.assessment.ims.dto.response.FeedbackResponse;
import org.gk.flatirons.assessment.ims.entity.Feedback;
import org.gk.flatirons.assessment.ims.entity.Interview;
import org.gk.flatirons.assessment.ims.entity.Interviewer;
import org.gk.flatirons.assessment.ims.repository.FeedbackRepository;
import org.gk.flatirons.assessment.ims.utils.ResponseMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private InterviewService interviewService;

    @Mock
    private ResponseMapper responseMapper;

    @InjectMocks
    private FeedbackService feedbackService;

    @Captor
    private ArgumentCaptor<Feedback> feedbackCaptor;

    private static final Integer INTERVIEW_ID = 10;
    private static final Integer INTERVIEWER_ID = 5;

    private Interview interview;
    private Interviewer panelInterviewer;

    @BeforeEach
    void setUp() {
        panelInterviewer = interviewer(INTERVIEWER_ID, "Ravi", "ravi@x.com");
        interview = new Interview();
        ReflectionTestUtils.setField(interview, "id", INTERVIEW_ID);
        interview.getInterviewers().add(panelInterviewer);
    }

    private Interviewer interviewer(Integer id, String name, String email) {
        Interviewer i = Interviewer.builder().fullName(name).email(email).build();
        ReflectionTestUtils.setField(i, "id", id);
        return i;
    }


    private SubmitFeedbackRequest request(Integer interviewerId) {
        return new SubmitFeedbackRequest(interviewerId, 4, "Strong on Spring Boot", FeedbackStatus.PROCEED_FURTHER);
    }

    @Test
    @DisplayName("saves feedback built from the request and returns the mapped response")
    void submit_savesAndMaps() {
        SubmitFeedbackRequest request = request(INTERVIEWER_ID);
        Feedback saved = Feedback.builder().interview(interview).interviewer(panelInterviewer).build();
        FeedbackResponse response = org.mockito.Mockito.mock(FeedbackResponse.class);

        when(interviewService.fetchInterviewForFeedback(INTERVIEW_ID)).thenReturn(interview);
        when(feedbackRepository.existsByInterviewIdAndInterviewerId(INTERVIEW_ID, INTERVIEWER_ID)).thenReturn(false);
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(saved);
        when(responseMapper.mapToFeedbackResponseDto(saved)).thenReturn(response);

        FeedbackResponse result = feedbackService.submit(INTERVIEW_ID, request);

        assertThat(result).isSameAs(response);

        verify(feedbackRepository).save(feedbackCaptor.capture());
        Feedback toSave = feedbackCaptor.getValue();
        assertThat(toSave.getInterview()).isSameAs(interview);
        assertThat(toSave.getInterviewer()).isSameAs(panelInterviewer);
        assertThat(toSave.getRating()).isEqualTo(request.rating());
        assertThat(toSave.getComments()).isEqualTo(request.comments());
        assertThat(toSave.getStatus()).isEqualTo(request.feedbackStatus());
    }

    @Test
    @DisplayName("uses the interviewer id from the request, not the interview id")
    void submit_usesInterviewerIdFromRequest() {
        SubmitFeedbackRequest request = request(INTERVIEWER_ID);
        Feedback saved = Feedback.builder().interview(interview).interviewer(panelInterviewer).build();

        when(interviewService.fetchInterviewForFeedback(INTERVIEW_ID)).thenReturn(interview);
        when(feedbackRepository.existsByInterviewIdAndInterviewerId(INTERVIEW_ID, INTERVIEWER_ID)).thenReturn(false);
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(saved);

        feedbackService.submit(INTERVIEW_ID, request);

        verify(feedbackRepository).existsByInterviewIdAndInterviewerId(INTERVIEW_ID, INTERVIEWER_ID);
        verify(feedbackRepository, never()).existsByInterviewIdAndInterviewerId(INTERVIEW_ID, INTERVIEW_ID);
    }

    @Test
    @DisplayName("throws FeedbackConflictException when the interviewer already submitted")
    void submit_throwsConflict_whenAlreadySubmitted() {
        when(interviewService.fetchInterviewForFeedback(INTERVIEW_ID)).thenReturn(interview);
        when(feedbackRepository.existsByInterviewIdAndInterviewerId(INTERVIEW_ID, INTERVIEWER_ID)).thenReturn(true);

        assertThatThrownBy(() -> feedbackService.submit(INTERVIEW_ID, request(INTERVIEWER_ID)))
                .isInstanceOf(FeedbackConflictException.class)
                .hasMessage("Feedback already submitted by interviewer " + INTERVIEWER_ID);

        verify(feedbackRepository, never()).save(any());
        verifyNoInteractions(responseMapper);
    }

    @Test
    @DisplayName("throws InvalidRequestException when the interviewer is not on the panel")
    void submit_throwsInvalid_whenNotOnPanel() {
        Integer outsiderId = 99;
        when(interviewService.fetchInterviewForFeedback(INTERVIEW_ID)).thenReturn(interview);
        when(feedbackRepository.existsByInterviewIdAndInterviewerId(INTERVIEW_ID, outsiderId)).thenReturn(false);

        assertThatThrownBy(() -> feedbackService.submit(INTERVIEW_ID, request(outsiderId)))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("Interviewer " + outsiderId + " is not on the panel for interview " + INTERVIEW_ID);

        verify(feedbackRepository, never()).save(any());
        verifyNoInteractions(responseMapper);
    }

    @Test
    @DisplayName("propagates the exception when the interview does not exist")
    void submit_propagatesNotFound_whenInterviewMissing() {
        when(interviewService.fetchInterviewForFeedback(INTERVIEW_ID))
                .thenThrow(new ResourceNotFoundException("Interview", INTERVIEW_ID));

        assertThatThrownBy(() -> feedbackService.submit(INTERVIEW_ID, request(INTERVIEWER_ID)))
                .isInstanceOf(ResourceNotFoundException.class);

        verifyNoInteractions(feedbackRepository, responseMapper);
    }
}