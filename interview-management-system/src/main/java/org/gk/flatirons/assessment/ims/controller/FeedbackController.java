package org.gk.flatirons.assessment.ims.controller;

import jakarta.validation.Valid;
import org.gk.flatirons.assessment.ims.dto.request.SubmitFeedbackRequest;
import org.gk.flatirons.assessment.ims.dto.response.FeedbackResponse;
import org.gk.flatirons.assessment.ims.service.FeedbackService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/interviews/{interviewId}/feedbacks")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping
    public ResponseEntity<FeedbackResponse> submit(@PathVariable Integer interviewId,
                                                   @Valid @RequestBody SubmitFeedbackRequest request) {
        return ResponseEntity.status(CREATED).body(feedbackService.submit(interviewId, request));
    }
}