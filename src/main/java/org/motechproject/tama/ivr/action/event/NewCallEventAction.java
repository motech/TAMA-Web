
package org.motechproject.tama.ivr.action.event;

import org.motechproject.eventtracking.service.EventService;
import org.motechproject.tama.ivr.*;
import org.motechproject.tama.ivr.action.UserNotFoundAction;
import org.motechproject.tama.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class NewCallEventAction extends BaseEventAction {
    private AllPatients allPatients;
    private UserNotFoundAction userNotFoundAction;
    @Autowired
    public NewCallEventAction(IVRMessage messages, UserNotFoundAction userNotFoundAction, EventService eventService, AllPatients allPatients) {
        this.messages = messages;
        this.allPatients = allPatients;
        this.userNotFoundAction = userNotFoundAction;
        this.eventService = eventService;
    }
    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        if (!isRegisteredNumber(ivrRequest.getCid())) {
            return userNotFoundAction.handle(ivrRequest, request, response);
        }
        IVRSession ivrSession = createIVRSession(request);
        ivrSession.setState(IVRCallState.COLLECT_PIN);
        return dtmfResponseWithWav(ivrRequest, IVRMessage.SIGNATURE_MUSIC_URL);
    }

    private boolean isRegisteredNumber(String phoneNumber) {
        return (phoneNumber != null && (allPatients.findByMobileNumber(phoneNumber) != null));
    }
	
	public IVREvent getCallEventName() {
		return IVREvent.NEW_CALL;
	}
}
