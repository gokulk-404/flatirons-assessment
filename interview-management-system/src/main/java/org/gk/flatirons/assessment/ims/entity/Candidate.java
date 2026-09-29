package org.gk.flatirons.assessment.ims.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "candidates")
public class Candidate extends ImsUserBaseEntity {

    @Column(name = "phone", length = 20)
    private String phone;

    @Column
    private String experience;

    @Column(name = "skill_set")
    private String skills;

    @Column(name = "resume_url")
    private String resumeUrl;

    public Candidate() {
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Candidate that)) return false;
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
        private String phone;
        private String experience;
        private String skills;
        private String resumeUrl;

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

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder experience(String experience) {
            this.experience = experience;
            return this;
        }

        public Builder skills(String skills) {
            this.skills = skills;
            return this;
        }

        public Builder resumeUrl(String resumeUrl) {
            this.resumeUrl = resumeUrl;
            return this;
        }

        public Candidate build() {
            Candidate candidate = new Candidate();
            candidate.setFullName(this.fullName);
            candidate.setEmail(this.email);
            candidate.setPhone(this.phone);
            candidate.setExperience(this.experience);
            candidate.setSkills(this.skills);
            candidate.setResumeUrl(this.resumeUrl);
            return candidate;
        }
    }
}