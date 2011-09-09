
package org.motechproject.tama.ivr.action.event;

import org.motechproject.tama.ivr.IVRCallState;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.action.BaseIncomingAction;
import org.motechproject.tama.ivr.action.UserNotFoundAction;
import org.motechproject.tama.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class NewCallEventAction extends BaseIncomingAction {

    private UserNotFoundAction userNotFoundAction;
    private AllPatients allPatients;

    @Autowired
    public NewCallEventAction(IVRMessage messages, UserNotFoundAction userNotFoundAction, AllPatients allPatients) {
        this.userNotFoundAction = userNotFoundAction;
        this.allPatients = allPatients;
        this.messages = messages;
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
}
