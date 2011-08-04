package org.motechproject.tama.ivr.action.event;

import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.*;
import org.motechproject.tama.ivr.action.BaseIncomingAction;
import org.motechproject.tama.ivr.action.UserNotFoundAction;
import org.motechproject.tama.repository.Patients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class NewCallEventAction extends BaseIncomingAction {
    private PillReminderService pillReminderService;
    private Patients patients;
    private UserNotFoundAction userNotFoundAction;

    @Autowired
    public NewCallEventAction(PillReminderService pillReminderService, IVRMessage messages, Patients patients, UserNotFoundAction userNotFoundAction) {
        this.pillReminderService = pillReminderService;
        this.messages = messages;
        this.patients = patients;
        this.userNotFoundAction = userNotFoundAction;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        IVRSession ivrSession = createIVRSession(request);
        Patient patient = patients.findByMobileNumber(ivrRequest.getCid());
        if (!isValidCaller(patient)) {
            return userNotFoundAction.handle(ivrRequest, request, response);
        }
        ivrSession.setState(IVRCallState.COLLECT_PIN);
        ivrSession.set(IVRCallAttribute.PATIENT_DOC_ID, patient.getId());
        PillRegimenResponse pillRegimen = pillReminderService.getPillRegimen(patient.getId());
        ivrSession.set(IVRCallAttribute.REGIMEN_FOR_PATIENT, pillRegimen);
        return dtmfResponseWithWav(ivrRequest, IVRMessage.SIGNATURE_MUSIC_URL);
    }

    private boolean isValidCaller(Patient patient) {
        return (patient != null && patient.isActive());
    }
}
