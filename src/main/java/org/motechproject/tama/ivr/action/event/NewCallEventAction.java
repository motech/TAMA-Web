
package org.motechproject.tama.ivr.action.event;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.motechproject.eventtracking.service.EventService;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.IVRCallAttribute;
import org.motechproject.tama.ivr.IVRCallState;
import org.motechproject.tama.ivr.IVREvent;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.action.UserNotFoundAction;
import org.motechproject.tama.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
