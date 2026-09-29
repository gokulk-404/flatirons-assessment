package org.gk.flatirons.assessment.ims.repository;

import jakarta.persistence.LockModeType;
import org.gk.flatirons.assessment.ims.entity.Interviewer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface InterviewerRepository extends JpaRepository<Interviewer, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Interviewer i where i.id in :interviewerIds order by i.id")
    List<Interviewer> findAllByIdForInterviewSchedule(@Param("interviewerIds") Set<Integer> interviewerIds);
}
