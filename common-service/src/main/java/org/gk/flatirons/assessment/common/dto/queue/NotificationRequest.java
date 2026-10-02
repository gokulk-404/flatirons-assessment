package org.gk.flatirons.assessment.common.dto.queue;

import java.util.Set;

public record NotificationRequest(Set<String> toMailIds, Set<String> ccMailIds, String subject, String body) {
}
