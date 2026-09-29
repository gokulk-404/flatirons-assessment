package org.gk.flatirons.assessment.ims.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.gk.flatirons.assessment.ims.constant.InterviewMode;
import org.gk.flatirons.assessment.ims.constant.InterviewRound;
import org.gk.flatirons.assessment.ims.constant.InterviewStatus;

import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "interviews")
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "interview_interviewers",
            joinColumns = @JoinColumn(name = "interview_id"),
            inverseJoinColumns = @JoinColumn(name = "interviewer_id")
    )
    private Set<Interviewer> interviewers;

    @Enumerated(EnumType.STRING)
    @Column(name = "interview_round")
    private InterviewRound round;

    @Enumerated(EnumType.STRING)
    @Column(name = "interview_mode")
    private InterviewMode mode;

    @Enumerated(EnumType.STRING)
    @Column(name = "interview_status")
    private InterviewStatus status;

    @Column(name = "scheduled_start")
    private Instant scheduledStart;

    @Column(name = "scheduled_end")
    private Instant scheduledEnd;

    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Feedback> feedbacks;

    public Interview() {
        this.interviewers = new LinkedHashSet<>();
        this.status = InterviewStatus.SCHEDULED;
        this.feedbacks = new LinkedHashSet<>();
    }

    public void addFeedback(Feedback feedback) {
        feedbacks.add(feedback);
        feedback.setInterview(this);
    }

    public void removeFeedback(Feedback feedback) {
        feedbacks.remove(feedback);
        feedback.setInterview(null);
    }

    public Integer getId() {
        return id;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public Set<Interviewer> getInterviewers() {
        return interviewers;
    }

    public InterviewRound getRound() {
        return round;
    }

    public void setRound(InterviewRound round) {
        this.round = round;
    }

    public InterviewMode getMode() {
        return mode;
    }

    public void setMode(InterviewMode mode) {
        this.mode = mode;
    }

    public InterviewStatus getStatus() {
        return status;
    }

    public void setStatus(InterviewStatus status) {
        this.status = status;
    }

    public Instant getScheduledStart() {
        return scheduledStart;
    }

    public void setScheduledStart(Instant scheduledStart) {
        this.scheduledStart = scheduledStart;
    }

    public Instant getScheduledEnd() {
        return scheduledEnd;
    }

    public void setScheduledEnd(Instant scheduledEnd) {
        this.scheduledEnd = scheduledEnd;
    }

    public Set<Feedback> getFeedbacks() {
        return feedbacks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Interview other)) {
            return false;
        }
        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // builder class
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Candidate candidate;
        private final Set<Interviewer> interviewers = new LinkedHashSet<>();
        private InterviewRound round;
        private InterviewMode mode;
        private Instant scheduledStart;
        private Instant scheduledEnd;

        private Builder() {
        }

        public Builder candidate(Candidate candidate) {
            this.candidate = candidate;
            return this;
        }

        public Builder interviewers(Collection<Interviewer> interviewers) {
            this.interviewers.addAll(interviewers);
            return this;
        }

        public Builder round(InterviewRound round) {
            this.round = round;
            return this;
        }

        public Builder mode(InterviewMode mode) {
            this.mode = mode;
            return this;
        }

        public Builder schedule(Instant start, Instant end) {
            this.scheduledStart = start;
            this.scheduledEnd = end;
            return this;
        }

        public Interview build() {
            Interview interview = new Interview();
            interview.candidate = candidate;
            interview.interviewers.addAll(interviewers);
            interview.round = round;
            interview.mode = mode;
            interview.scheduledStart = scheduledStart;
            interview.scheduledEnd = scheduledEnd;
            return interview;
        }
    }
}