package org.gk.flatirons.assessment.ims.repository;

import org.gk.flatirons.assessment.ims.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {

    boolean existsByInterviewIdAndInterviewerId(Integer interviewId, Integer interviewerId);
}