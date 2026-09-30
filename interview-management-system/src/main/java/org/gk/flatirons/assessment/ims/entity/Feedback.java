package org.gk.flatirons.assessment.ims.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.gk.flatirons.assessment.ims.constant.FeedbackStatus;

@Entity
@Table(name = "feedbacks")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id")
    private Interview interview;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interviewer_id")
    private Interviewer interviewer;

    @Min(value = 1, message = "Feedback Rating must be greater than 0")
    @Max(value = 9, message = "Feedback Rating must be less than 10")
    @Column(name = "rating")
    private Integer rating;

    @Column(name = "comments")
    private String comments;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private FeedbackStatus status;

    public Feedback() {}

    public Integer getId() {
        return id;
    }

    public Interview getInterview() {
        return interview;
    }

    public void setInterview(Interview interview) {
        this.interview = interview;
    }

    public Interviewer getInterviewer() {
        return interviewer;
    }

    public void setInterviewer(Interviewer interviewer) {
        this.interviewer = interviewer;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public FeedbackStatus getStatus() {
        return status;
    }

    public void setStatus(FeedbackStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Feedback other)) {
            return false;
        }
        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Interview interview;
        private Interviewer interviewer;
        private Integer rating;
        private String comments;
        private FeedbackStatus status;

        private Builder() {}

        public Builder interview(Interview interview) {
            this.interview = interview;
            return this;
        }

        public Builder interviewer(Interviewer interviewer) {
            this.interviewer = interviewer;
            return this;
        }

        public Builder rating(Integer rating) {
            this.rating = rating;
            return this;
        }

        public Builder comments(String comments) {
            this.comments = comments;
            return this;
        }

        public Builder status(FeedbackStatus status) {
            this.status = status;
            return this;
        }

        public Feedback build() {
            Feedback feedback = new Feedback();
            feedback.interview = interview;
            feedback.interviewer = interviewer;
            feedback.rating = rating;
            feedback.comments = comments;
            feedback.status = status;
            return feedback;
        }
    }
}