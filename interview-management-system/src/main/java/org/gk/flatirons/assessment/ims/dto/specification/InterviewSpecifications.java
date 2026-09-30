package org.gk.flatirons.assessment.ims.dto.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.gk.flatirons.assessment.ims.entity.Interview;
import org.gk.flatirons.assessment.ims.entity.Interviewer;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class InterviewSpecifications {

    private InterviewSpecifications() {}

    public static Specification<Interview> candidateNameContains(String name) {
        return (root, query, cb) -> !StringUtils.hasText(name) ? null
                : cb.like(cb.lower(root.get("candidate").get("fullName")), pattern(name), '\\');
    }

    public static Specification<Interview> candidateIdEquals(Integer candidateId) {
        return (root, query, cb) -> candidateId == null ? null
                : cb.equal(root.get("candidate").get("id"), candidateId);
    }

    public static Specification<Interview> interviewerNameContains(String name) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(name)) return null;
            Subquery<Integer> sub = query.subquery(Integer.class);
            Root<Interview> correlated = sub.correlate(root);
            Join<Interview, Interviewer> iv = correlated.join("interviewers");
            sub.select(cb.literal(1))
                    .where(cb.like(cb.lower(iv.get("fullName")), pattern(name), '\\'));
            return cb.exists(sub);
        };
    }

    private static String pattern(String raw) {
        String escaped = raw.trim().toLowerCase()
                .replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
        return "%" + escaped + "%";
    }
}
