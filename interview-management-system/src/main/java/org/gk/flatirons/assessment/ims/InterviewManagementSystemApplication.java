package org.gk.flatirons.assessment.ims;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.gk.flatirons.assessment")
public class InterviewManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(InterviewManagementSystemApplication.class, args);
    }

}
