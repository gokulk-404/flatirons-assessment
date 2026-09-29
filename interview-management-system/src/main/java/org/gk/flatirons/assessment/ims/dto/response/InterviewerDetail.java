package org.gk.flatirons.assessment.ims.dto.response;

import org.gk.flatirons.assessment.ims.constant.InterviewerDepartment;

public record InterviewerDetail(Integer id, String fullName, String email, InterviewerDepartment department) {
}
