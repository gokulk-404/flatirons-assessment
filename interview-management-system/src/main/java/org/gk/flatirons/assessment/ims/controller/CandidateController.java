package org.gk.flatirons.assessment.ims.controller;

import jakarta.validation.Valid;
import org.gk.flatirons.assessment.ims.dto.request.CreateCandidateRequest;
import org.gk.flatirons.assessment.ims.dto.response.CandidateDetail;
import org.gk.flatirons.assessment.ims.service.CandidateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/candidates")
public class CandidateController {

    private final CandidateService candidateService;

    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @PostMapping
    public ResponseEntity<CandidateDetail> createAndPersistNewCandidate(@Valid @RequestBody CreateCandidateRequest request) {
        CandidateDetail createdCandidate = candidateService.createAndPersistNewCandidate(request);
        return ResponseEntity.status(CREATED).body(createdCandidate);
    }
}
