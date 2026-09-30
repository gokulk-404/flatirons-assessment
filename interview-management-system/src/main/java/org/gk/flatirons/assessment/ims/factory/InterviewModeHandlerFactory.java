package org.gk.flatirons.assessment.ims.factory;

import org.gk.flatirons.assessment.ims.constant.InterviewMode;
import org.gk.flatirons.assessment.ims.handlers.InterviewModeBaseHandler;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class InterviewModeHandlerFactory {

    private final Map<InterviewMode, InterviewModeBaseHandler> handlers = new EnumMap<>(InterviewMode.class);

    public InterviewModeHandlerFactory(List<InterviewModeBaseHandler> handlerList) {
        handlerList.forEach(interviewModeHandler -> handlers.put(interviewModeHandler.mode(), interviewModeHandler));
    }

    public InterviewModeBaseHandler getHandler(InterviewMode interviewMode) {
        return handlers.get(interviewMode);
    }
}
