package org.motechproject.tama.ivr.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.motechproject.ivr.kookoo.action.AuthenticateAction;
import org.motechproject.ivr.kookoo.action.IvrAction;
import org.motechproject.server.decisiontree.DecisionTreeBasedResponseBuilder;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.server.service.ivr.IVRCallState;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.server.service.ivr.IVRSession;
import org.motechproject.server.service.ivr.IVRSession.IVRCallAttribute;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.decisiontree.TamaTreeChooser;
import org.motechproject.tama.ivr.logging.service.CallLogService;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.tama.repository.AllTreatmentAdvices;
import org.motechproject.tama.util.TamaSessionUtil.TamaSessionAttribute;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TamaAuthenticateAction extends AuthenticateAction {

    private PillReminderService pillReminderService;
    private TamaRetryAction retryAction;
    private TamaUserNotFoundAction userNotFoundAction;
    private AllPatients allPatients;
    private final TamaTreeChooser treeChooser;
    private DecisionTreeBasedResponseBuilder responseBuilder;

    @Autowired
    public TamaAuthenticateAction(PillReminderService pillReminderService,
                                  AllPatients allPatients, TamaRetryAction retryAction,
                                  TamaUserNotFoundAction userNotFoundAction, TamaTreeChooser treeChooser,
                                  DecisionTreeBasedResponseBuilder ivrResponseBuilder) {
        super();
        this.pillReminderService = pillReminderService;
        this.allPatients = allPatients;
        this.retryAction = retryAction;
        this.treeChooser = treeChooser;
        this.userNotFoundAction = userNotFoundAction;
        this.responseBuilder = ivrResponseBuilder;
    }

    @Override
    public String createResponse(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        return handle(ivrRequest, request, response, new IvrAction(treeChooser, messages, responseBuilder));
    }

    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response, IvrAction tamaIvrAction) {
        IVRSession ivrSession = getIVRSession(request);
        String passcode = ivrRequest.getInput();
        String phoneNumber = (String) ivrSession.get(IVRCallAttribute.CALLER_ID);
        Patient patient = allPatients.findByMobileNumber(phoneNumber);
        if (patient == null)
            return userNotFoundAction.createResponse(ivrRequest, request, response);

        patient = allPatients.findByMobileNumberAndPasscode(phoneNumber, passcode);
        if (!isAuthenticatedUser(passcode, patient)) {
            return retryAction.createResponse(ivrRequest, request, response);
        }
        if (!patient.isActive())
            return userNotFoundAction.createResponse(ivrRequest, request, response);

        ivrSession.renew(request);
        ivrSession.setState(IVRCallState.AUTH_SUCCESS);
        ivrSession.set(TamaSessionAttribute.PATIENT_DOC_ID, patient.getId());
        ivrSession.set(IVRCallAttribute.PREFERRED_LANGUAGE_CODE, patient.getPatientPreferences().getIvrLanguage().getCode());

        ivrSession.setCallTime(DateUtil.now());
        PillRegimenResponse pillRegimen = pillReminderService.getPillRegimen(patient.getId());
        ivrSession.set(TamaSessionAttribute.REGIMEN_FOR_PATIENT, pillRegimen);
        ivrRequest.setData("");
        return tamaIvrAction.handle(ivrRequest, ivrSession);
    }

    private boolean isAuthenticatedUser(String passcode, Patient patient) {
        return patient != null && patient.getPatientPreferences().getPasscode().equals(passcode);
    }
}
