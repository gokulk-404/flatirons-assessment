package org.gk.flatirons.assessment.ims.repository;

import jakarta.persistence.LockModeType;
import org.gk.flatirons.assessment.ims.constant.InterviewStatus;
import org.gk.flatirons.assessment.ims.entity.Interview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface InterviewRepository extends JpaRepository<Interview, Integer>, JpaSpecificationExecutor<Interview> {

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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i from Interview i where id=:interviewId")
    Optional<Interview> findByIdForFeedback(@Param("interviewId") Integer interviewId);

    @EntityGraph(attributePaths = "candidate")
    Page<Interview> findAll(Specification<Interview> spec, Pageable pageable);
}
