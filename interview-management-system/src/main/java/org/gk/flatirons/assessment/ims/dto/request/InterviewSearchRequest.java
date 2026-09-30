package org.gk.flatirons.assessment.ims.dto.request;

public record InterviewSearchRequest(
        String interviewerName,
        String candidateName,
        Integer candidateId
) {}
