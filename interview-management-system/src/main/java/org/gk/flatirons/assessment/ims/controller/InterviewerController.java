package org.gk.flatirons.assessment.ims.controller;

import jakarta.validation.Valid;
import org.gk.flatirons.assessment.ims.dto.request.BulkInterviewerCreateRequest;
import org.gk.flatirons.assessment.ims.dto.request.CreateInterviewerRequest;
import org.gk.flatirons.assessment.ims.dto.response.InterviewerDetail;
import org.gk.flatirons.assessment.ims.service.InterviewerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/interviewers")
public class InterviewerController {

    private final InterviewerService interviewerService;

    public InterviewerController(InterviewerService interviewerService) {
        this.interviewerService = interviewerService;
    }

    @PostMapping
    public ResponseEntity<InterviewerDetail> createAndPersistNewInterviewer(@Valid @RequestBody CreateInterviewerRequest request) {
        InterviewerDetail createdInterviewer = interviewerService.createNewInterviewer(request);
        return ResponseEntity.status(CREATED).body(createdInterviewer);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<InterviewerDetail>> createAndPersistNewInterviewerBulk(@Valid @RequestBody BulkInterviewerCreateRequest request) {
        List<InterviewerDetail> createdInterviewers = interviewerService.createNewInterviewersBulk(request);
        return ResponseEntity.status(CREATED).body(createdInterviewers);
    }
}
