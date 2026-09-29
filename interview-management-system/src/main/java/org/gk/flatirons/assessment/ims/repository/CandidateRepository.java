package org.gk.flatirons.assessment.ims.repository;

import jakarta.persistence.LockModeType;
import org.gk.flatirons.assessment.ims.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CandidateRepository extends JpaRepository<Candidate, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Candidate c where c.id = :candidateId")
    Optional<Candidate> findByIdForInterviewSchedule(@Param("candidateId") Integer candidateId);
}
