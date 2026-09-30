package org.gk.flatirons.assessment.ims.controller;


import jakarta.validation.Valid;
import org.gk.flatirons.assessment.ims.dto.request.InterviewSearchRequest;
import org.gk.flatirons.assessment.ims.dto.request.ScheduleInterviewRequest;
import org.gk.flatirons.assessment.ims.dto.response.InterviewResponse;
import org.gk.flatirons.assessment.ims.dto.response.PagedResponse;
import org.gk.flatirons.assessment.ims.service.InterviewService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/interviews")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @PostMapping
    public ResponseEntity<InterviewResponse> schedule(@Valid @RequestBody ScheduleInterviewRequest request) {
        InterviewResponse scheduledInterview = interviewService.schedule(request);
        return ResponseEntity.status(CREATED).body(scheduledInterview);
    }

    @GetMapping
    public PagedResponse<InterviewResponse> search(InterviewSearchRequest request, @PageableDefault(size = 20) Pageable pageable) {
        return interviewService.search(request, pageable);
    }
}
