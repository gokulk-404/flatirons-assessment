package org.gk.flatirons.assessment.ims.repository;

import org.gk.flatirons.assessment.ims.constant.InterviewStatus;
import org.gk.flatirons.assessment.ims.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface InterviewRepository extends JpaRepository<Interview, Integer> {

    @Query("""
            select count(i) > 0
            from Interview i join i.interviewers iv
            where iv.id in :interviewerIds
              and i.status = :status
              and i.scheduledStart < :end
              and i.scheduledEnd > :start
            """)
    boolean existsInterviewerConflict(@Param("interviewerIds") List<Integer> interviewerIds,
                                      @Param("status") InterviewStatus status,
                                      @Param("start") Instant start,
                                      @Param("end") Instant end);

    @Query("""
            select count(i) > 0
            from Interview i
            where i.candidate.id = :candidateId
              and i.status = :status
              and i.scheduledStart < :end
              and i.scheduledEnd > :start
            """)
    boolean existsCandidateConflict(@Param("candidateId") Integer candidateId,
                                    @Param("status") InterviewStatus status,
                                    @Param("start") Instant start,
                                    @Param("end") Instant end);
}
