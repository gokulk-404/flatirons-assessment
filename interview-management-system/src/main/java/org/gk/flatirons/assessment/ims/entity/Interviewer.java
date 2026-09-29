package org.gk.flatirons.assessment.ims.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import org.gk.flatirons.assessment.ims.constant.InterviewerDepartment;

import java.util.Objects;

@Entity
@Table(name = "interviewers")
public class Interviewer extends ImsUserBaseEntity {

    @Enumerated(EnumType.STRING)
    @Column
    private InterviewerDepartment department;

    public Interviewer() {
    }

    public InterviewerDepartment getDepartment() {
        return department;
    }

    public void setDepartment(InterviewerDepartment department) {
        this.department = department;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Interviewer that)) return false;
        return getEmail() != null && Objects.equals(getEmail(), that.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String fullName;
        private String email;
        private InterviewerDepartment department;

        private Builder() {
        }

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder department(InterviewerDepartment department) {
            this.department = department;
            return this;
        }

        public Interviewer build() {
            Interviewer interviewer = new Interviewer();
            interviewer.setFullName(this.fullName);
            interviewer.setEmail(this.email);
            interviewer.setDepartment(this.department);
            return interviewer;
        }
    }
}