package org.gk.flatirons.assessment.ims.handlers;

import org.gk.flatirons.assessment.ims.constant.InterviewMode;
import org.gk.flatirons.assessment.ims.entity.Interview;

public interface InterviewModeBaseHandler {

    InterviewMode mode();

    void prepare(Interview interview);
}
